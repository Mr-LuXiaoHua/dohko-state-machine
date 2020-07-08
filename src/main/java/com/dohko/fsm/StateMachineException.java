package com.dohko.fsm;

/**
 * @description: 状态机异常
 * @author: luxiaohua
 * @date: 2020-07-08 16:18
 */
public class StateMachineException extends RuntimeException {

    public StateMachineException(String message) {
        super(message);
    }
}
