package com.yxl.smmall.order.listener;

import com.rabbitmq.client.Channel;
import com.yxl.smmall.order.entity.OmsOrderEntity;
import com.yxl.smmall.order.service.OmsOrderService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;


/**
 * 监听由于长时间没有支付或者主动关闭订单（关闭订单的操作）
 * TODO：关闭订单操作
 *
 * @author SADSADSD
 */
@Service
@RabbitListener(queues = "order.release.order.queue")
public class OrderCloseListener {
    @Autowired
    OmsOrderService orderService;

    @RabbitHandler
    public void listeners(OmsOrderEntity order, Channel channel, Message message) throws IOException {
        System.out.println("收到货期的订单信息，准备关闭订单"+order.getOrderSn());
      try{
          orderService.closeOrder(order);
          channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
          //正常，无异常回复ACK
      }catch(Exception e){
          //业务出现异常，拒绝接收，放回队列，
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
      }

    }

}
