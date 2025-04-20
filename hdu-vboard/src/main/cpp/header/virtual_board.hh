#ifndef FPGA__HH_
#define FPGA__HH_

#include <map>
#include <vector>
#include <string>
#include <nlohmann/json.hpp>
#include <iostream>

#define INPUT_SW    SW00, SW01, SW02, SW03, SW04, SW05, SW06, SW07, \
                    SW08, SW09, SW10, SW11, SW12, SW13, SW14, SW15, \
                    SW16, SW17, SW18, SW19

#define INPUT_SWB   SWB00, SWB01, SWB02, SWB03, SWB04, SWB05, SWB06, SWB07, SWB08, SWB09

#define INPUT_HEX   INPUT0, INPUT1, INPUT2, INPUT3

#define OUTPUT_LED  L00, L01, L02, L03, L04, L05, L06, L07, \
                    L08, L09, L10, L11, L12, L13, L14, L15, \
                    L16, L17, L18, L19

#define OUTPUT_SEG  OUTPUT00, OUTPUT01, OUTPUT02, OUTPUT03

#define INPUT_SW_STR    "SW00", "SW01", "SW02", "SW03", "SW04", "SW05", "SW06", "SW07", \
                        "SW08", "SW09", "SW10", "SW11", "SW12", "SW13", "SW14", "SW15", \
                        "SW16", "SW17", "SW18", "SW19"

#define INPUT_SWB_STR   "SWB00", "SWB01", "SWB02", "SWB03", "SWB04", "SWB05", "SWB06", "SWB07", "SWB08", "SWB09"

#define INPUT_HEX_STR   "INPUT0", "INPUT1", "INPUT2", "INPUT3"

#define OUTPUT_LED_STR  "L00", "L01", "L02", "L03", "L04", "L05", "L06", "L07", \
                        "L08", "L09", "L10", "L11", "L12", "L13", "L14", "L15", \
                        "L16", "L17", "L18", "L19"

#define OUTPUT_SEG_STR  "OUTPUT00", "OUTPUT01", "OUTPUT02", "OUTPUT03"

inline const static char *pins_name[] = {
    "CLK",INPUT_SW_STR,INPUT_SWB_STR,INPUT_HEX_STR,OUTPUT_LED_STR,OUTPUT_SEG_STR
};

enum PINS {
    CLK,
    INPUT_SW,
    INPUT_SWB,
    INPUT_HEX,

    OUTPUT_LED,
    OUTPUT_SEG,
    PINS_LEN
};

std::map<void *, std::vector<int>> pins_map;

int pins_value[PINS_LEN] = { 0 };



// 置位函数（已内联）
#if 0
void set_bit(void *ptr, int bit_offset, int value) {
    uint8_t *byte_ptr = static_cast<uint8_t *>(ptr); // 将 void* 转换为字节指针

    // 计算目标字节的位置和位的偏移量
    int byte_index = bit_offset / 8;  // 目标字节索引
    int bit_index = bit_offset % 8;  // 目标字节内的位索引

    // 修改对应的位
    if (value) {
        byte_ptr[byte_index] |= (1 << bit_index); // 设置该位为 1
    } else {
        byte_ptr[byte_index] &= ~(1 << bit_index); // 清除该位为 0
    }
}
#endif

// 从json中读取并更新信号
void update_input_signal_from_json(const nlohmann::json &input_json, void *sgl_ptr) {
    const auto &v = pins_map[sgl_ptr];
    uint8_t *byte_ptr = static_cast<uint8_t *>(sgl_ptr);
    int byte_idx = 0;
    int bit_idx = 0;
    for (auto p = v.rbegin();p != v.rend();p++) {
        if (!input_json.contains(pins_name[*p]))
            continue;
        if (input_json[pins_name[*p]] != 0) {
            byte_ptr[byte_idx] |= (1 << bit_idx);
        } else {
            byte_ptr[byte_idx] &= ~(1 << bit_idx);
        }
        ++bit_idx;
        byte_idx += bit_idx / 8;
        bit_idx %= 8;
    }
}

// 需要自己在模块中实现
void update_input_signals_from_json(const nlohmann::json &input_json);

// 更新变量信号
// 理论上只需要更新输出变量即可
int update_output_signal(void *sgl_ptr) {
    auto &v = pins_map[sgl_ptr];
    uint8_t *byte_ptr = static_cast<uint8_t *>(sgl_ptr);
    int byte_idx = 0;
    int bit_idx = 0;
    int change_flag = 0;
    for (auto p = v.rbegin();p != v.rend();p++) {
        int new_value = (byte_ptr[byte_idx] >> bit_idx) & 1;
        change_flag = change_flag | (new_value ^ pins_value[*p]);

        pins_value[*p] = new_value;     // 更新信号值

        ++bit_idx;
        byte_idx += bit_idx / 8;
        bit_idx %= 8;
    }
    return change_flag;
}

// 需要自己在模块中实现
// 即调用所有需要的update_signal
int update_output_signals();

// 转化为json输出
void print_signals_as_json() {
    // 因为json不允许多余','
    // 第一行单独处理
    printf(R"({"L00":%d)", pins_value[L00]);
    for (int i = L01;i < PINS_LEN;++i) {
        printf(R"(,"%s":%d)", pins_name[i], pins_value[i]);  // 注意前缀','
    }
    printf("}\n");
    // fflush(stdout);
}

// 对update_out_put_map()和print_pins_map()的包装
// 如果有改变 输出对应信号值
// 没有改变 输出空行
// 自动flush
void update_and_print() {
    if (update_output_signals()) {
        print_signals_as_json();
    } else {
        printf("\n");
    }
    fflush(stdout);
}

#endif