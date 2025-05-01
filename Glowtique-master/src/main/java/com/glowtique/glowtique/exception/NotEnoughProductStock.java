package com.glowtique.glowtique.exception;

public class NotEnoughProductStock extends RuntimeException {
  public NotEnoughProductStock(String message) {
    super(message);
  }
}
