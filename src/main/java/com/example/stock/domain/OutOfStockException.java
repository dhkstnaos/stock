package com.example.stock.domain;

public class OutOfStockException extends RuntimeException {

  private final String OUT_OF_STOCK = "재고가 부족합니다.";
  private String message;

  public void OutOfStockException() {
    this.message = OUT_OF_STOCK;
  }
}
