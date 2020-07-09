package com.dohko.fsm.order;

import com.dohko.fsm.Handler;
import com.dohko.fsm.StateMachine;
import com.dohko.fsm.StateMachineConfigurationHolder;
import com.dohko.fsm.order.bean.Order;
import com.dohko.fsm.order.enums.OrderEvent;
import com.dohko.fsm.order.enums.OrderState;
import com.dohko.fsm.order.handler.*;
import org.junit.Assert;
import org.junit.Test;

/**
 * @description:
 * @author: luxiaohua
 * @date: 2020-07-08 15:58
 */
public class OrderStateMachineTest {


    @Test
    public void testStateMachine() {

        StateMachineConfigurationHolder<OrderState, OrderEvent, Handler> holder = new StateMachineConfigurationHolder();


        holder.source(OrderState.SUBMIT_ORDER)
                .event(OrderEvent.SUBMIT_ORDER)
                .handler(new SubmitOrderHandler())
                .target(OrderState.WAIT_PAY)
                .build();



        holder.source(OrderState.WAIT_PAY)
                .event(OrderEvent.CANCEL)
                .handler(new CancelHandler())
                .target(OrderState.CLOSED)
                .build();


        holder.source(OrderState.WAIT_PAY)
                .event(OrderEvent.PAY)
                .handler(new PayHandler())
                .target(OrderState.WAIT_DELIVERY)
                .build();


        holder.source(OrderState.WAIT_DELIVERY)
                .event(OrderEvent.DELIVERY)
                .handler(new DeliveryHandler())
                .target(OrderState.WAIT_RECEIVE)
                .build();


        holder.source(OrderState.WAIT_RECEIVE)
                .event(OrderEvent.CONFIRM_RECEIVE)
                .handler(new ConfirmReceviveHandler())
                .target(OrderState.COMPLETE)
                .build();



        StateMachine<OrderState, OrderEvent, Handler> stateMachine = new StateMachine<>(holder);



        Order order = new Order();
        order.setOrderNo(10086L);
        order.setState(OrderState.SUBMIT_ORDER);


        stateMachine.transition(order, OrderEvent.SUBMIT_ORDER);
        Assert.assertEquals(OrderState.WAIT_PAY, order.getState());

        stateMachine.transition(order, OrderEvent.PAY);
        Assert.assertEquals(OrderState.WAIT_DELIVERY, order.getState());

        stateMachine.transition(order, OrderEvent.DELIVERY);
        Assert.assertEquals(OrderState.WAIT_RECEIVE, order.getState());

        stateMachine.transition(order, OrderEvent.CONFIRM_RECEIVE);
        Assert.assertEquals(OrderState.COMPLETE, order.getState());



    }
}
