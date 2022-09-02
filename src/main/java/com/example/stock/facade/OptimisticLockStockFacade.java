package com.example.stock.facade;

import com.example.stock.service.OptimisticRockStockService;
import org.springframework.stereotype.Service;

@Service
public class OptimisticLockStockFacade {

  private final OptimisticRockStockService optimisticLockStockService;

  public OptimisticLockStockFacade(OptimisticRockStockService optimisticLockStockService) {
    this.optimisticLockStockService = optimisticLockStockService;
  }

  public void decrease(Long id, Long quantity) throws InterruptedException {
    while (true) {
      try {
        optimisticLockStockService.decrease(id, quantity);
      } catch (Exception e) {
        Thread.sleep(50);
      }
    }
  }

}