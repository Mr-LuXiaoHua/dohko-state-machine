dohko-state-machine
---
以简化的订单状态机为例子，做了一个演示。


状态机核心类图：
![](https://github.com/Mr-LuXiaoHua/dohko-state-machine/blob/master/imgs/state-machine-class.png)


订单状态机：
![](https://github.com/Mr-LuXiaoHua/dohko-state-machine/blob/master/imgs/order-state-machine.png)


#### 核心类说明

```

public interface Handler<S> {


    /**
     * 业务处理方法
     * @param domain
     * @param s
     */
    void handle(StateMachineDomain domain, S s);
}

```
* Handler 是事件触发状态变化时的处理器，由具体业务实现，比如订单有一个支付的事件，需要一个支付业务处理器，则需要实现该接口，如：
```

public class PayHandler implements Handler<OrderState> {


    @Override
    public void handle(StateMachineDomain domain, OrderState orderState) {
        System.out.println(String.format("%s --> %s", domain.getCurrentState().toString(), orderState.toString()));
        domain.setNextState(orderState);

        System.out.println("========订单支付========");
    }
}

```

* StateMachineDomain 主要用于获取当前状态和设置下一个状态
```
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
```
以订单为例：
```
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
```

* StateMachineConfiguration 状态机配置类，存放当前状态，以及当前状态可能存在的事件对应的Handler和次态
```
@Data
public class StateMachineConfiguration<S, E, H> {

    /**
     * 当前状态
     */
    private S currentState;

    /**
     * 存放事件处理器
     */
    private Map<E, H> eventHandlerMap;

    /**
     * 存放次态
     */
    private Map<E, S> nextStateMap;


    public StateMachineConfiguration(S currentState) {
        this.currentState = currentState;
        this.eventHandlerMap = new HashMap<>();
        this.nextStateMap = new HashMap<>();
    }
}
```

* StateMachineConfigurationHolder 状态机配置持有器，用于构建并保存状态机配置
```
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


```

* StateMachine 状态机，里面的关键方法是触发状态流转
```
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
```



#### 订单状态机案例

##### 定义订单状态和事件
```
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

```

##### 定义订单事件处理器，参考test目录源码


##### 测试流程
```
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

```
