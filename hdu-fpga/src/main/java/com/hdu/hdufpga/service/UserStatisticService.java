package com.hdu.hdufpga.service;

import com.hdu.hdufpga.entity.vo.UserVO;

import java.time.Duration;

public interface UserStatisticService {
  void updateUserExpTime(UserVO user, Duration expTime);
}
