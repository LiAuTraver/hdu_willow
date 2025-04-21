package com.hdu.vboard.controller;

import com.hdu.hdufpga.annotation.CheckToken;
import com.hdu.hdufpga.entity.Result;
import com.hdu.vboard.service.VbSysFileService;
import com.hdu.vboard.service.VirtualBoardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import cn.hutool.json.JSONObject;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/vb")
@Slf4j
public class VirtualBoardController /*extends BaseController<VirtualBoardService,> */ {
    @Resource
    VirtualBoardService virtualBoardService;

    @Resource
    VbSysFileService vbSysFileService;

//    @Override
//    @PostMapping("/listPage")
//    public Result listPage(Integer current, Integer size) {
//        return super.listPage(current, size);
//    }

    //level >= 3
//    @Override
//    @PostMapping("/get")
//    public Result get(Integer id) {
//        return super.get(id);
//    }

//    //    @Override
//    @PostMapping("/create")
//    public Result create(@RequestBody CircuitBoardPO circuitBoardPO) {
//        return Result.error("不支持本方法");
//    }
//
//    //    @Override
//    @PostMapping("/update")
//    public Result update(@RequestBody CircuitBoardPO circuitBoardPO) {
//        return Result.error("不支持本方法");
//    }
//
//    //    @Override
//    @PostMapping("/delete")
//    public Result delete(@RequestBody CircuitBoardPO circuitBoardPO) {
//        return Result.error("不支持本方法");
//    }

    @PostMapping("/build")
    @CheckToken
    public Result simulate(@RequestParam("verilogFile") MultipartFile verilogFile,
                           @RequestParam("bindFile") MultipartFile bindFile,
                           HttpServletRequest request) {
        String workspaceName = request.getHeader("token");
        try {
            log.debug("Hello to simulate");
            String verilogFullPath = vbSysFileService.saveVerilogFile(request, verilogFile);
            String bindFullPath = vbSysFileService.saveBindFile(request, bindFile);
            log.debug("Now to create virtual board");
            virtualBoardService.createWorkbench(workspaceName, verilogFullPath, bindFullPath);
            log.debug("Ok to create workbench, now to check it");
            virtualBoardService.checkWorkbench(workspaceName);
            return Result.ok("Ok to build the virtual board");
        } catch (Exception e) {
            try {
                virtualBoardService.clearWorkbench(workspaceName);
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
            log.error(e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/start")
    @CheckToken
    public Result simulate(HttpServletRequest request) {
        String token = request.getHeader("token");
        try {
            virtualBoardService.runWorkbench(token);
            log.debug("Now it's running!");
            JSONObject finalJson = virtualBoardService.getSignalFromVirtualBoard(token);
            log.debug(finalJson.toString());
            return Result.ok(finalJson);
        } catch (Exception e) {
            try {
                virtualBoardService.clearWorkbench(token);
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
            log.error(e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/signal")
    @CheckToken
    public Result signal(HttpServletRequest request, @RequestBody JSONObject signalJson) {
        String token = request.getHeader("token");
        try {
            log.debug("Received input signal:" + signalJson.toString());
            log.debug("data:" + signalJson.get("data").toString());
            virtualBoardService.sendSignal(token, signalJson.get("data").toString());
            log.debug("output:" + signalJson.toString());
            return Result.ok(virtualBoardService.getSignalFromVirtualBoard(token));
        } catch (Exception e) {
            log.error(e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    //level >= 1
    @PostMapping("/finish")
    @CheckToken
    public Result finish(HttpServletRequest request) {
        String token = request.getHeader("token");
        try {
            return Result.ok(virtualBoardService.stopWorkbench(token));
        } catch (Exception e) {
            log.error(e.getMessage());
            return Result.error(e.getMessage());
        }
    }

//    //level >= 1
//    @GetMapping("/getRecordedStatus")
//    @CheckAndRefreshToken
//    public Result getRecordedStatus(HttpServletRequest request, String cbIp) {
//        String token = request.getHeader("token");
//        try {
//            return Result.ok(circuitBoardService.getRecordStatus(token, cbIp));
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            return Result.error(e.getMessage());
//        }
//    }
//
//    //level >= 1
//    @PostMapping("/sendButtonString")
//    @CheckAndRefreshToken
//    public Result sendButtonString(HttpServletRequest request, String switchButtonStatus, String tapButtonStatus) {
//        String token = request.getHeader("token");
//        try {
//            return Result.ok(circuitBoardService.sendButtonString(token, switchButtonStatus, tapButtonStatus));
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            return Result.error(e.getMessage());
//        }
//    }

//    //level >= 1
//    @GetMapping("/getLightString")
//    @CheckAndRefreshToken
//    public Result getLightString(HttpServletRequest request) {
//        String token = request.getHeader("token");
//        try {
//            return Result.ok(circuitBoardService.getLightString(token));
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            return Result.error(e.getMessage());
//        }
//    }

//    //level >= 1
//    @GetMapping("/getNixieTubeString")
//    @CheckAndRefreshToken
//    public Result getNixieTubeString(HttpServletRequest request) {
//        String token = request.getHeader("token");
//        try {
//            return Result.ok(circuitBoardService.getNixieTubeString(token));
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            return Result.error(e.getMessage());
//        }
//    }
//
//    //level >= 1
//    @GetMapping("/getProcessedBtnStr")
//    @CheckAndRefreshToken
//    public Result getProcessedBtnStr(HttpServletRequest request) {
//        String token = request.getHeader("token");
//        try {
//            return Result.ok(circuitBoardService.getProcessedBtnStr(token));
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            return Result.error(e.getMessage());
//        }
//    }
//
//    //level >= 1
//    @PostMapping("/loadHistory")
//    @CheckAndRefreshToken
//    public Result loadHistory(HttpServletRequest request, Boolean tag) {
//        String token = request.getHeader("token");
//        try {
//            if (tag) {
//                if (circuitBoardHistoryOperationService.loadOperationHistory(token)) {
//                    return Result.ok("载入成功");
//                } else {
//                    return Result.error("载入失败");
//                }
//            } else {
//                circuitBoardHistoryOperationService.clearSteps(token);
//                return Result.ok("初始化成功");
//            }
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            return Result.error(e.getMessage());
//        }
//    }
}
