package com.dohko.fsm.order.enums;

/**
 * @description: 订单事件
 * @author: luxiaohua
 * @date: 2020-07-06 19:18
 */
public enum OrderEvent {


    // 提交订单
    SUBMIT_ORDER,

    // 付款
    PAY,

    // 发货
    DELIVERY,

    // 确认收货
    CONFIRM_RECEIVE,

    // 取消
    CANCEL


}
