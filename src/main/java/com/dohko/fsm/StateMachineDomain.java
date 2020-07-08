package com.dohko.fsm;

/**
 * @description: 状态机领域
 * @author: luxiaohua
 * @date: 2020-07-07 10:15
 */
public interface StateMachineDomain<S> {

   /**
    * 获取当前状态
    * @return
    */
   S getCurrentState();

   /**
    * 设置次态
    */
   void setNextState(S s);




}
