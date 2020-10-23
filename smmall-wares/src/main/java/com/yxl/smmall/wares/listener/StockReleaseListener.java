package com.yxl.smmall.wares.listener;

import com.alibaba.fastjson.TypeReference;
import com.rabbitmq.client.Channel;
import com.yxl.common.constant.OrderStatusEnum;
import com.yxl.common.to.mq.SrockLockedTo;
import com.yxl.common.to.mq.StockDetailTo;
import com.yxl.common.utils.R;
import com.yxl.common.vo.OrderVo;
import com.yxl.smmall.wares.entity.WmsWareOrderTaskDetailEntity;
import com.yxl.smmall.wares.entity.WmsWareOrderTaskEntity;
import com.yxl.smmall.wares.service.WmsWareSkuService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author SADSADSD
 */
@Service
@RabbitListener(queues = "stock.release.stock.queue")
public class StockReleaseListener {
    @Autowired
    WmsWareSkuService wmsWareSkuService;
    @RabbitHandler
    public void handlerStockLockerRelease(SrockLockedTo to, Message message, Channel channel) throws IOException {
      try{
          wmsWareSkuService.unlockStock(to);
          channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
      }catch (Exception e){
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
      }


    }
    @RabbitHandler
    public void handleOrderStockRelease(OrderVo orderVo, Message message,Channel channel) throws IOException {
        try{
            wmsWareSkuService.unlockStock(orderVo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }

    }
}
