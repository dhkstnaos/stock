package com.example.stock.service;

import com.example.stock.repository.StockRepository;
import org.springframework.stereotype.Service;

@Service
public class PessimisticRockService {

  private final StockRepository stockRepository;

  public PessimisticRockService(StockRepository stockRepository) {
    this.stockRepository = stockRepository;
  }

  public void PessimisticRockStockService
}