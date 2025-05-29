package com.hdu.vboard.controller;

import com.hdu.hdufpga.annotation.CheckToken;
import com.hdu.hdufpga.entity.Result;
import com.hdu.hdufpga.entity.vo.UserVO;
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
  public Result build(@RequestParam("verilogFile") MultipartFile verilogFile,
                      @RequestParam("bindFile") MultipartFile bindFile,
                      HttpServletRequest request) {
    String workspaceName = request.getHeader("token");
    try {
      log.debug("Hello to start");
//      log.debug("token:{},type:{}", workspaceName,workspaceName.getClass());
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
  public Result start(HttpServletRequest request) {
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
      log.debug("Received input signal:{}", signalJson.toString());
      virtualBoardService.sendSignal(token, signalJson.get("data").toString());
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

  @GetMapping("generateToken")
  public Result generateToken(UserVO userVO) {
    return virtualBoardService.generateToken(userVO);
  }
}
