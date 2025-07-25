package com.hdu.vboard.listener;

import com.hdu.hdufpga.config.RedisConfiguration;
import com.hdu.hdufpga.entity.constant.RedisConstant;
import com.hdu.hdufpga.util.RedisUtil;
import com.hdu.vboard.service.VirtualBoardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
@Import(RedisConfiguration.class)
public class VbRedisKeyExpiredListener extends KeyExpirationEventMessageListener {
  @Resource
  private RedisUtil redisUtil;

  @Resource
  private VirtualBoardService virtualBoardService;

  public VbRedisKeyExpiredListener(RedisMessageListenerContainer listenerContainer) {
    super(listenerContainer);
  }

  @Override
  public void onMessage(@NonNull Message message, @Nullable byte[] pattern) {
    String expiredKey = message.toString();
    try {
      String[] split = expiredKey.split(":");
      String token = split[1];
      if (RedisConstant.REDIS_TTL_PREFIX.contains(split[0])) {
        freeVirtualBoard(token);
      }

    } catch (Exception e) {
      log.error(e.toString());
      log.error(expiredKey + " 有key过期了，但是没有成功执行监听方法");
    }
  }

  private void freeVirtualBoard(String token) {
    try {
      virtualBoardService.stopWorkbench(token);
      log.info("超时自动释放板卡成功,对应token:{}", token);
    } catch (Exception e) {
      log.warn(e.toString());
    }
  }
}
