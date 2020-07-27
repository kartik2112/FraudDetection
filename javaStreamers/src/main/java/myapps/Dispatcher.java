package myapps;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author karti
 */
public class Dispatcher implements Runnable {
    private final KafkaProducer<String, JsonNode> producer;
    private final String topicName;
    private final String messageKey;
    private final List<JsonNode> dataList;
    private static final Logger logger = LogManager.getLogger();
    
    /**
     * A dispatcher thread takes a kafka producer and send a batch of messages to the given topic
     *
     * @param producer   A valid producer instance
     * @param topicName  Name of the Kafka Topic
     * @param messageKey Message key for the entire batch
     * @param dataList   List of Json messages
     */
    Dispatcher(KafkaProducer<String, JsonNode> producer, String topicName, String messageKey, List<JsonNode> dataList) {
        this.producer = producer;
        this.topicName = topicName;
        this.messageKey = messageKey;
        this.dataList = dataList;
    }

    @Override
    public void run() {
        int messageCounter = 1;
        String producerName = Thread.currentThread().getName();

        logger.trace("Starting Producer thread" + producerName);
        for (JsonNode data : dataList) {
            producer.send(new ProducerRecord(topicName, messageKey, data));
            messageCounter++;
            if(messageCounter % 200 == 0){
                System.out.println("Published "+messageCounter+" messages");
            }
        }
        logger.trace("Finished Producer thread" + producerName + " sent " + messageCounter + " messages");
    }
}
