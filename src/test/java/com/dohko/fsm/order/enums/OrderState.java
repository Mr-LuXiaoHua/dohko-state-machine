package com.dohko.fsm.order.enums;

/**
 * @description: 订单状态
 * @author: luxiaohua
 * @date: 2020-07-06 19:17
 */
public enum OrderState {

    // 提交订单
    SUBMIT_ORDER,

    // 待支付
    WAIT_PAY,

    // 待发货
    WAIT_DELIVERY,

    // 待收货
    WAIT_RECEIVE,

    // 订单完成
    COMPLETE,

    // 订单关闭
    CLOSED
}
