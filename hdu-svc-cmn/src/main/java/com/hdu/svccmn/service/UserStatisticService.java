package com.hdu.svccmn.service;

import com.hdu.hdufpga.entity.vo.UserVO;

import java.time.Duration;

public interface UserStatisticService {
  void updateUserExpTime(UserVO user, Duration expTime);

  void storeUserByToken(String token, UserVO userVO);

  UserVO getUserByToken(String token);
}
