
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myapps;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;

/**
 *
 * @author karti
 */
public class CassandraInteract {

    public static void main(String[] args) {
        //Kafka consumer configuration settings
        String topicName = "bank-sim-transactions-input1";
        String destTopicName = "bank-sim-enh-trans-output";
        Properties props = new Properties();

        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "test");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer",
                DataDeserializer.class.getName());
        KafkaConsumer<String, JsonNode> consumer = new KafkaConsumer<String, JsonNode>(props);

        //Kafka Consumer subscribes list of topics here.
        consumer.subscribe(Arrays.asList(topicName));

        //print the topic name
        System.out.println("Subscribed to topic " + topicName);
        int i = 0;

        ObjectMapper objectMapper = new ObjectMapper();

        //https://www.tutorialspoint.com/cassandra/cassandra_create_data.htm
        //Cassandra driver v3 had to be used instead of the latest v4
        Cluster cluster = Cluster.builder().addContactPoint("127.0.0.1").build();

        Session session = cluster.connect("test");

        //https://docs.datastax.com/en/developer/java-driver/3.0/manual/statements/prepared/
        PreparedStatement prepared = session.prepare(
                "INSERT INTO transactions (idx,step,customer,age,gender,zipcodeOri,merchant,zipMerchant,category,amount,fraud) "
                + "values(?,?,?,?,?,?,?,?,?,?,?)");
        
        int recordCount = 0;
        long startTime = 0, endTime = 0, timeDiff = 0;
        long minDuration = 99999999999L,maxDuration = 0;
        
        Properties producerProps = new Properties();
//        try {
//            InputStream kafkaConfigStream = ClassLoader.class.getResourceAsStream(kafkaConfig);
//            producerProps.load(kafkaConfigStream);
//            producerProps.put(StreamsConfig.APPLICATION_ID_CONFIG, "bank-sim-transactions-input");
            producerProps.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, DataSerializer.class.getName());
            producerProps.put("acks","all");

//        } catch (IOException e) {
//            logger.error("Cannot open Kafka config " + kafkaConfig);
//            throw new RuntimeException(e);
//        }

        KafkaProducer<String, JsonNode> producer = new KafkaProducer<>(producerProps);
        
        while (true) {
            ConsumerRecords<String, JsonNode> records = consumer.poll(100);
            for (ConsumerRecord<String, JsonNode> record : records) {
                // print the offset,key and value for the consumer records.
//                System.out.printf("offset = %d, key = %s, value = %s\n",
//                        record.offset(), record.key(), record.value());
                if(record.value() == null){
                    continue;
                }
                recordCount++;

                try {
                    startTime = System.nanoTime();
                    TransactionData transaction = objectMapper.treeToValue(record.value(), TransactionData.class);
                    
                    Long resCust1Day = session.execute("SELECT count(*) FROM transactions WHERE customer='"+transaction.getCustomer()+"' AND step="+transaction.getStep()+"")
                                                .one().getLong(0);
                    
                    Long resCust7Days = session.execute("SELECT count(*) FROM transactions WHERE customer='"+transaction.getCustomer()+"' AND step>"+(transaction.getStep()-7) +" AND step<="+transaction.getStep()+"")
                                                .one().getLong(0);
                    
                    Long resCust30Days = session.execute("SELECT count(*) FROM transactions WHERE customer='"+transaction.getCustomer()+"' AND step>"+(transaction.getStep()-30) +" AND step<="+transaction.getStep()+"")
                                                .one().getLong(0);
                    
                    Long resCustMerch1Day = session.execute("SELECT count(*) FROM transactionsCustMerchs WHERE merchant='"+transaction.getMerchant()+"' AND customer='"+transaction.getCustomer()+"' AND step="+transaction.getStep()+"")
                                                .one().getLong(0);
                    
                    Long resCustMerch7Days = session.execute("SELECT count(*) FROM transactionsCustMerchs WHERE merchant='"+transaction.getMerchant()+"' AND customer='"+transaction.getCustomer()+"' AND step>"+(transaction.getStep()-7) +" AND step<="+transaction.getStep()+"")
                                                .one().getLong(0);
                    
                    Long resCustMerch30Days = session.execute("SELECT count(*) FROM transactionsCustMerchs WHERE merchant='"+transaction.getMerchant()+"' AND customer='"+transaction.getCustomer()+"' AND step>"+(transaction.getStep()-30) +" AND step<="+transaction.getStep()+"")
                                                .one().getLong(0);
                    
                    
                    BoundStatement bound = prepared.bind(transaction.getIdx(),transaction.getStep(),
                                                        transaction.getCustomer(),transaction.getAge(),
                                                        transaction.getGender(),transaction.getZipcodeOri(),
                                                        transaction.getMerchant(),transaction.getZipMerchant(),
                                                        transaction.getCategory(),transaction.getAmount(),
                                                        transaction.getFraud());
                    session.execute(bound);
                    endTime = System.nanoTime();
                    timeDiff = (endTime - startTime)/1000000;
                    minDuration = timeDiff < minDuration ? timeDiff : minDuration;
                    maxDuration = timeDiff > maxDuration ? timeDiff : maxDuration;
                    JsonNode obj = objectMapper.valueToTree(new EnhancedTransactionData(transaction,resCust1Day,resCust7Days,resCust30Days,resCustMerch1Day,resCustMerch7Days,resCustMerch30Days));
//                    JsonNode obj = objectMapper.valueToTree(new EnhancedTransactionData(transaction,0L,0L,0L,0L,0L,0L));
                    producer.send(new ProducerRecord(destTopicName, transaction.getIdx()+"", obj));
                    
                } catch (JsonProcessingException ex) {
                    Logger.getLogger(CassandraInteract.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                if(recordCount % 200 == 0){
                    System.out.println("Processed "+recordCount+" transactions. Time: min: "+minDuration+" ms, max: "+maxDuration+" ms");
                    minDuration = 99999999999L;
                    maxDuration = 0;
                }

            }

        }
    }
}
