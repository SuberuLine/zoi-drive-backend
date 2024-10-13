package com.zoi.drive.config;

import com.zoi.drive.utils.Const;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description TODO
 * @Author Yuzoi
 * @Date 2024/9/16 2:22
 **/
@Configuration
public class RabbitConfiguration {

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean("emailQueue")
    public Queue emailQueue() {
        return QueueBuilder
                .durable(Const.MQ_MAIL_QUEUE)
                .build();
    }

    @Bean("downloadQueue")
    public Queue downloadQueue() {
        return QueueBuilder
                .durable(Const.MQ_DOWNLOAD_QUEUE)
                .build();
    }
}
