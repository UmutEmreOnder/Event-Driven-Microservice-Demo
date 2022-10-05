package com.microservices.demo.kafka.producer.config.service.impl;

import com.microservices.demo.kafka.avro.model.TwitterAvroModel;
import com.microservices.demo.kafka.producer.config.service.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.PreDestroy;


@Service
@RequiredArgsConstructor
public class TwitterKafkaProducer implements KafkaProducer<Long, TwitterAvroModel> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TwitterKafkaProducer.class);

    private final KafkaTemplate<Long, TwitterAvroModel> kafkaTemplate;

    @Override
    public void send(String topicName, Long key, TwitterAvroModel message) {
        LOGGER.info("Sending message='{}' to topic='{}'", message, topicName);

        ListenableFuture<SendResult<Long, TwitterAvroModel>> kafkaResultFuture =  kafkaTemplate.send(topicName, key, message);
        addCallBack(topicName, message, kafkaResultFuture);
    }

    @PreDestroy
    public void close() {
        if (kafkaTemplate != null) {
            LOGGER.info("Closing kafka producer!");
            kafkaTemplate.destroy();
        }
    }

    private void addCallBack(String topicName, TwitterAvroModel message, ListenableFuture<SendResult<Long, TwitterAvroModel>> kafkaResultFuture) {
        kafkaResultFuture.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable ex) {
                LOGGER.error("Error while sending message {} to topic {}", message.toString(), topicName, ex);
            }

            @Override
            public void onSuccess(SendResult<Long, TwitterAvroModel> result) {
                RecordMetadata metadata = result.getRecordMetadata();
                LOGGER.debug("Received new metadata. Topic: {}; Partition: {}; Offset: {}; Timestamp: {}; at Time: {}",
                        metadata.topic(),
                        metadata.partition(),
                        metadata.offset(),
                        metadata.timestamp(),
                        System.nanoTime());
            }
        });
    }
}
