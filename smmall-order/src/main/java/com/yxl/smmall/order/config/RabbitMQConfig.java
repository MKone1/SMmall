package com.yxl.smmall.order.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Configuration;

/**
 * rabbitMQ的配置类
 */
@Configuration
public class RabbitMQConfig {
    private RabbitTemplate rabbitTemplate;

    /**
     * 手动创建构造器，否则就会产生循环引用
     * @param connectionFactory
     * @return
     */
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        this.rabbitTemplate = rabbitTemplate;
        rabbitTemplate.setMessageConverter(messageConverter());
    initRabbitTemplate();
    return rabbitTemplate;

    }

    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    public void initRabbitTemplate(){
        rabbitTemplate.setConfirmCallback(((correlationData, b, s) -> {

        }));
        rabbitTemplate.setReturnCallback(((message, i, s, s1, s2) -> {

        }));
    }
}
