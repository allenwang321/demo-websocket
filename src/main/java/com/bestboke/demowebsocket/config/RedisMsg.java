package com.bestboke.demowebsocket.config;


import org.springframework.stereotype.Component;

@Component
public interface RedisMsg {
    /**
     * 接受信息
     * @param message
     */
    void receiveMessage(String message);
}