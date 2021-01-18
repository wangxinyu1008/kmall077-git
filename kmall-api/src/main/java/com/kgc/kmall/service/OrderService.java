package com.kgc.kmall.service;

/**
 * @author shkstart
 * @create 2021-01-15 16:59
 */
public interface OrderService {

    String genTradeCode(Long aLong);

    String checkTradeCode(Long aLong, String tradeCode);
}
