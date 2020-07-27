/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myapps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.apache.kafka.streams.StreamsConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author karti
 */
public class BankSimDataProducer {

    private static final Logger logger = LogManager.getLogger();
    private static final String kafkaConfig = "/kafka.properties";

    /**
     * private static method to read data from given dataFile
     *
     * @param dataFile data file name in resource folder
     * @return List of StockData Instance
     * @throws IOException, NullPointerException
     */
    private static List<TransactionDataTrimmer> getTransactions(String dataFile) throws IOException {

        File file = new File(dataFile);
        MappingIterator<TransactionDataTrimmer> transactionDataIterator = new CsvMapper().readerWithTypedSchemaFor(TransactionDataTrimmer.class).readValues(file);
        return transactionDataIterator.readAll();
    }

    /**
     * Application entry point you must provide the topic name and at least one
     * event file
     *
     * @param args topicName (Name of the Kafka topic) list of files (list of
     * files in the classpath)
     */
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {

        final ObjectMapper objectMapper = new ObjectMapper();
        List<Thread> dispatchers = new ArrayList<>();

//        if (args.length < 2) {
//            System.out.println("Please provide command line arguments: topicName EventFiles");
//            System.exit(-1);
//        }
//        String topicName = args[0];
        String topicName = "bank-sim-transactions-input1";
//        String[] eventFiles = Arrays.copyOfRange(args, 1, args.length);
        System.out.println("Current Dir is: "+System.getProperty("user.dir"));
        String[] eventFiles = {"./bs140513_032310.csv"};
        List<JsonNode>[] stockArrayOfList = new List[eventFiles.length];
        for (int i = 0; i < stockArrayOfList.length; i++) {
            stockArrayOfList[i] = new ArrayList<>();
        }

        logger.trace("Creating Kafka producer...");
        Properties properties = new Properties();
//        try {
//            InputStream kafkaConfigStream = ClassLoader.class.getResourceAsStream(kafkaConfig);
//            properties.load(kafkaConfigStream);
//            properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "bank-sim-transactions-input");
            properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, DataSerializer.class.getName());
            properties.put("acks","all");

//        } catch (IOException e) {
//            logger.error("Cannot open Kafka config " + kafkaConfig);
//            throw new RuntimeException(e);
//        }

        KafkaProducer<String, JsonNode> producer = new KafkaProducer<>(properties);
        try {
            for (int i = 0; i < eventFiles.length; i++) {
                for (TransactionDataTrimmer s : getTransactions(eventFiles[i])) {
                    stockArrayOfList[i].add(objectMapper.valueToTree(s));
                }
                dispatchers.add(new Thread(new Dispatcher(producer, topicName, eventFiles[i], stockArrayOfList[i]), eventFiles[i]));
                dispatchers.get(i).start();
            }
        } catch (Exception e) {
            logger.error("Exception in reading data file.");
            producer.close();
            throw new RuntimeException(e);
        }

        //Wait for threads
        try {
            for (Thread t : dispatchers) {
                t.join();
            }
        } catch (InterruptedException e) {
            logger.error("Thread Interrupted ");
            throw new RuntimeException(e);
        } finally {
            producer.close();
            logger.info("Finished Application - Closing Kafka Producer.");
        }
    }
}
