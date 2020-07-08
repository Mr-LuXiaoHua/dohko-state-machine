package com.dohko.fsm.order.bean;

import com.dohko.fsm.StateMachineDomain;
import com.dohko.fsm.order.enums.OrderState;
import lombok.Data;

/**
 * @description:
 * @author: luxiaohua
 * @date: 2020-07-07 10:53
 */
@Data
public class Order implements StateMachineDomain<OrderState> {


    /**
     * 订单号
     */
    private Long orderNo;

    /**
     *  订单状态
     */
    private OrderState state;


    public OrderState getCurrentState() {
        return this.state;
    }

    public void setNextState(OrderState orderState) {
        this.state = orderState;
    }
}
