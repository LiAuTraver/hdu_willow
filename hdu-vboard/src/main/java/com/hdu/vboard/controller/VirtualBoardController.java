package com.hdu.vboard.controller;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.IdUtil;
import com.hdu.hdufpga.annotation.CheckToken;
import com.hdu.hdufpga.entity.Result;
import com.hdu.hdufpga.entity.constant.RedisConstant;
import com.hdu.hdufpga.entity.vo.UserVO;
import com.hdu.hdufpga.util.RedisUtil;
import com.hdu.vboard.service.VbSysFileService;
import com.hdu.vboard.service.VirtualBoardService;
import hdu.svccmn.ParamUtil;
import hdu.svccmn.UserStatisticService;
import hdu.svccmn.UserStatisticServiceImpl;
import hdu.svccmn.exception.IdentifyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import cn.hutool.json.JSONObject;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/vb")
@Slf4j
public class VirtualBoardController /*extends BaseController<VirtualBoardService,> */ {
  @Resource
  VirtualBoardService virtualBoardService;

  @Resource
  VbSysFileService vbSysFileService;

  @Resource
  private RedisUtil redisUtil;

  @Resource
  private UserStatisticService userStatisticService;

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
  public Result build(@RequestParam("verilogFile") MultipartFile[] verilogFiles,
                      @RequestParam("bindFile") MultipartFile bindFile,
                      HttpServletRequest request) {
    String workspaceName = request.getHeader("token");
    try {
      log.debug("Hello to start");
      List<String> verilogFullPaths = new ArrayList<>();
      for (MultipartFile verilogFile : verilogFiles) {
        String path = vbSysFileService.saveVerilogFile(request, verilogFile);
        verilogFullPaths.add(path);
      }
      String bindFullPath = vbSysFileService.saveBindFile(request, bindFile);
      log.debug("Now to create virtual board");
      virtualBoardService.createWorkbench(workspaceName, verilogFullPaths, bindFullPath);
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

      UserVO userIdFromToken = userStatisticService.getUserByToken(token);
      if (userIdFromToken == null)
        log.error("an unexpect error has occurred: user with token {} is null", token);
      else {
        final Long sTime = (Long) redisUtil.get(RedisConstant.REDIS_EXP_START_TIME_PREFIX + token);
        long curTime = System.currentTimeMillis();

        if (sTime == null || sTime <= 0 || sTime >= curTime)
          log.error("an unexpect error has occurred: user with token {} has no exp time", token);
        else {
          userStatisticService.updateUserExpTime(userIdFromToken, Duration.ofMillis(curTime - sTime));
          redisUtil.del(RedisConstant.REDIS_EXP_START_TIME_PREFIX + token);
        }
      }

      return Result.ok(virtualBoardService.stopWorkbench(token));
    } catch (Exception e) {
      log.error(e.getMessage());
      return Result.error(e.getMessage());
    }
  }

  @GetMapping("generateToken")
  public Result generateToken(UserVO userVO) {
    if (!ParamUtil.CheckUserInfoLegal(userVO)) {
      return Result.error("身份信息有误");
    }
    String salt = IdUtil.simpleUUID();
    String token = ParamUtil.generateUserToken(userVO, salt);
    redisUtil.set(RedisConstant.REDIS_TTL_PREFIX + token, true, RedisConstant.REDIS_TTL_LIMIT, TimeUnit.SECONDS);
    redisUtil.set(RedisConstant.REDIS_EXP_START_TIME_PREFIX + token, System.currentTimeMillis(), RedisConstant.REDIS_TTL_LIMIT, TimeUnit.SECONDS);
    userStatisticService.storeUserByToken(token, userVO);
    return Result.ok(token);
  }
}
