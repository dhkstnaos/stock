package com.example.stock.facade;

import com.example.stock.repository.RedisLockRepository;
import com.example.stock.service.StockService;
import org.springframework.stereotype.Component;

@Component
public class LettuceLockStockFacade {

  private final RedisLockRepository redisLockRepository;
  private final StockService stockService;

  public LettuceLockStockFacade(RedisLockRepository redisLockRepository,
      StockService stockService) {
    this.redisLockRepository = redisLockRepository;
    this.stockService = stockService;
  }

  public void decrease(Long key, Long quantuty) throws InterruptedException {
    while (redisLockRepository.lock(key)) {
      Thread.sleep(100); // spin lock 방식이 redis 부하를 일으킬 수 있으므로 lock 획득 재시도간에 텀을 부여한다.
    }

    try {
      stockService.decrease(key, quantuty);
    } finally {
      redisLockRepository.unlock(key);
    }
  }

}