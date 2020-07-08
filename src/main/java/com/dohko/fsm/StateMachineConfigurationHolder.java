package com.dohko.fsm;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @description: 状态机配置持有器
 * @author: luxiaohua
 * @date: 2020-07-08 10:02
 */
public class StateMachineConfigurationHolder<S, E, H> {


    /**
     * 当前状态
     */
    private S currentState;

    /**
     * 事件
     */
    private E event;

    /**
     * 处理器
     */
    private H handler;

    /**
     * 次态
     */
    private S nextState;



    private final Map<S, StateMachineConfiguration<S, E, H>> stateMachineConfigurationMap = new HashMap<>();



    public StateMachineConfigurationHolder source(S s) {
        this.currentState = s;
        return this;
    }

    public StateMachineConfigurationHolder event(E e) {
        this.event = e;
        return this;
    }

    public StateMachineConfigurationHolder handler(H h) {
        this.handler = h;
        return this;
    }

    public StateMachineConfigurationHolder target(S s) {
        this.nextState = s;
        return this;
    }

    public void build() {

        if (Objects.isNull(this.currentState)) {
            throw new StateMachineException("currentState未配置");
        }

        if (Objects.isNull(this.event)) {
            throw new StateMachineException("event未配置");
        }

        if (Objects.isNull(this.nextState)) {
            throw new StateMachineException("nextState未配置");
        }


        StateMachineConfiguration stateMachineConfiguration = stateMachineConfigurationMap.get(this.currentState);
        if (Objects.isNull(stateMachineConfiguration)) {
            stateMachineConfiguration = new StateMachineConfiguration(this.currentState);
        }
        stateMachineConfiguration.getEventHandlerMap().put(this.event, this.handler);
        stateMachineConfiguration.getNextStateMap().put(this.event, this.nextState);

        stateMachineConfigurationMap.put(this.currentState, stateMachineConfiguration);



        this.currentState = null;
        this.event = null;
        this.handler = null;
        this.nextState = null;

    }



    public H getHandler(S s, E e) {
        StateMachineConfiguration sc = stateMachineConfigurationMap.get(s);
        if (Objects.isNull(sc)) {
            throw new StateMachineException(String.format("状态：%s 未配置", s.toString()));
        }

        if (Objects.isNull(sc.getEventHandlerMap().get(e))) {
            throw new StateMachineException(String.format("状态：%s 事件: %s 未配置处理器", s.toString(), e.toString()));
        }


        return (H) sc.getEventHandlerMap().get(e);
    }


    public S getNextState(S s, E e) {
        StateMachineConfiguration sc = stateMachineConfigurationMap.get(s);
        if (Objects.isNull(sc)) {
            throw new StateMachineException(String.format("状态：%s 未配置", s.toString()));
        }
        return (S) sc.getNextStateMap().get(e);
    }






















}
