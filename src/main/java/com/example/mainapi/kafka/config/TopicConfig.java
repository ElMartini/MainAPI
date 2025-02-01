package com.example.mainapi.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class TopicConfig {

    @Bean
    public NewTopic productTopic(){
        return TopicBuilder.name("kafkaProduct").build();
    }
    @Bean
    public NewTopic orderTopic(){
        return TopicBuilder.name("kafkaOrder").build();
    }

    @Bean
    public NewTopic mainProductBasketTopic(){
        return TopicBuilder.name("mainProductBasket").build();
    }

    @Bean
    public NewTopic productOrderBasketTopic(){
        return TopicBuilder.name("productOrderBasket").build();
    }
    @Bean
    public NewTopic mainCustomerWalletRequest(){
        return TopicBuilder.name("mainCustomerWalletRequest").build();
    }

}
