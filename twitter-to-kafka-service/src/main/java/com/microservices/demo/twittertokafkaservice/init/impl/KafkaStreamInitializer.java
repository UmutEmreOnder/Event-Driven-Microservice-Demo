package com.microservices.demo.twittertokafkaservice.init.impl;

import com.microservices.demo.appconfigdata.KafkaConfigData;
import com.microservices.demo.kafka.admin.config.client.KafkaAdminClient;
import com.microservices.demo.twittertokafkaservice.init.StreamInitializer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class KafkaStreamInitializer implements StreamInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaStreamInitializer.class);

    private final KafkaConfigData kafkaConfigData;
    private final KafkaAdminClient kafkaAdminClient;

    @Override
    public void init() {
        kafkaAdminClient.createTopics();
        kafkaAdminClient.checkSchemaRegistry();
        LOGGER.info("Topic with name {} is ready for operations!", kafkaConfigData.getTopicNamesToCreate().toArray());
    }
}
