package com.dohko.fsm;

/**
 * @description: 处理器
 * @author: luxiaohua
 * @date: 2020-07-06 20:56
 */
public interface Handler<S> {


    /**
     * 业务处理方法
     * @param domain
     * @param s
     */
    void handle(StateMachineDomain domain, S s);
}
