package com.dohko.fsm;

/**
 * @description: 状态机
 * @author: luxiaohua
 * @date: 2020-07-07 10:27
 */
public class StateMachine<S, E, H extends Handler> {

    private StateMachineConfigurationHolder<S, E,H> stateMachineConfigurationHolder;

    public StateMachine(StateMachineConfigurationHolder<S, E,H> stateMachineConfigurationHolder) {
        this.stateMachineConfigurationHolder = stateMachineConfigurationHolder;
    }


    /**
     * 触发状态流转
     * @param domain
     * @param event
     */
    public void transition(StateMachineDomain<S> domain, E event) {
        S currentState = domain.getCurrentState();
        H handler = stateMachineConfigurationHolder.getHandler(currentState, event);
        S nextState = stateMachineConfigurationHolder.getNextState(currentState, event);
        handler.handle(domain, nextState);
    }




}
