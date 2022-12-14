package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.facade.NamedLockStockFacade;
import com.example.stock.facade.OptimisticLockStockFacade;
import com.example.stock.repository.StockRepository;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class StockServiceTest {

  @Autowired
  private StockService stockService;

  @Autowired
  private PessimisticRockStockService pessimisticRockStockService;

  @Autowired
  private OptimisticLockStockFacade optimisticLockStockFacade;

  @Autowired
  private NamedLockStockFacade namedLockStockFacade;
  @Autowired
  private StockRepository stockRepository;

  @BeforeEach
  public void before() {
    Stock stock = new Stock(1L, 100L);
    stockRepository.saveAndFlush(stock);
  }

  @AfterEach
  public void after() {
    stockRepository.deleteAll();
  }

  @Test
  public void 재고_감소() {
    stockService.decrease(1L, 1L);

    Stock stock = stockRepository.findById(1L).orElseThrow();
    Assertions.assertThat(stock.getQuantity()).isEqualTo(99L);
  }

  @Test
  public void synchronized_우선_적용후_트랜잭션_적용한_동시에_100개_요청() throws InterruptedException {
    int threadCount = 100;
    ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
    CountDownLatch countDownLatch = new CountDownLatch(threadCount);
    for (int i = 0; i < threadCount; i++) {
      executorService.submit(() -> {
        try {
          stockService.synchronizedDecrease(1L, 1L);
        } finally {
          countDownLatch.countDown();
        }
      });
    }
    countDownLatch.await();
    Stock stock = stockRepository.findById(1L).orElseThrow();
    Assertions.assertThat(stock.getQuantity()).isEqualTo(0L);
  }

  @Test
  public void 낙관적_락을_통한_동시에_100개_요청() throws InterruptedException {
    int threadCount = 100;
    ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
    CountDownLatch countDownLatch = new CountDownLatch(threadCount);
    for (int i = 0; i < threadCount; i++) {
      executorService.submit(() -> {
        try {
          optimisticLockStockFacade.decrease(1L, 1L);
        } catch (InterruptedException e) {
          e.printStackTrace();
        } finally {
          countDownLatch.countDown();
        }
      });
    }
    countDownLatch.await();
    Stock stock = stockRepository.findById(1L).orElseThrow();
    Assertions.assertThat(stock.getQuantity()).isEqualTo(0L);
  }

  @Test
  public void 비관적_락을_통한_동시에_100개_요청() throws InterruptedException {
    int threadCount = 100;
    ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
    CountDownLatch countDownLatch = new CountDownLatch(threadCount);
    for (int i = 0; i < threadCount; i++) {
      executorService.submit(() -> {
        try {
          pessimisticRockStockService.decrease(1L, 1L);
        } finally {
          countDownLatch.countDown();
        }
      });
    }
    countDownLatch.await();
    Stock stock = stockRepository.findById(1L).orElseThrow();
    Assertions.assertThat(stock.getQuantity()).isEqualTo(0L);
  }

  @Test
  public void 네임드_락을_통한_동시에_100개_요청() throws InterruptedException {
    int threadCount = 100;
    ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
    CountDownLatch countDownLatch = new CountDownLatch(threadCount);
    for (int i = 0; i < threadCount; i++) {
      executorService.submit(() -> {
        try {
          namedLockStockFacade.decrease(1L, 1L);
        } finally {
          countDownLatch.countDown();
        }
      });
    }
    countDownLatch.await();
    Stock stock = stockRepository.findById(1L).orElseThrow();
    Assertions.assertThat(stock.getQuantity()).isEqualTo(0L);
  }
}