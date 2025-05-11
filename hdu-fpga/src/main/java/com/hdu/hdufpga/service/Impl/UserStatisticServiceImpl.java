package com.hdu.hdufpga.service.Impl;

import com.hdu.hdufpga.entity.po.UserPO;
import com.hdu.hdufpga.entity.vo.UserVO;
import com.hdu.hdufpga.service.UserService;
import com.hdu.hdufpga.service.UserStatisticService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.Duration;

@Service
@Slf4j
public class UserStatisticServiceImpl implements UserStatisticService {
  @Resource
  private UserService userService;

  @Override
  @Transactional
  public void updateUserExpTime(@NonNull UserVO userVO, @NonNull Duration expTime) {
    // todo: is it needed to also modify the UserVO?
    Duration currentTotalTime = userVO.getTotActiveTime();
    userVO.setTotActiveTime(currentTotalTime.plus(expTime));

    int currentExpCount = userVO.getTotExpCnt();
    userVO.setTotExpCnt(currentExpCount + 1);

    long expTimeInMillis = expTime.toMillis();

    // get UserPO by username and department ID
    UserPO userPO = userService.getUserByUserName(
        userVO.getUsername(),
        userVO.getUserDepartmentId());

    if (userPO == null) {
      log.error("Cannot find userPO in database for username: {}, departmentId: {}",
          userVO.getUsername(), userVO.getUserDepartmentId());
      return;
    }

    long currentTotalMillis = userPO.getTotActiveTime();
    userPO.setTotActiveTime(currentTotalMillis + expTimeInMillis);

    int currentCount = userPO.getTotExpCnt();
    userPO.setTotExpCnt(currentCount + 1);

    userService.updateById(userPO);
    log.info("Updated user {} statistics: +{} ms, +1 exp",
        userVO.getUsername(), expTimeInMillis);

  }
}

