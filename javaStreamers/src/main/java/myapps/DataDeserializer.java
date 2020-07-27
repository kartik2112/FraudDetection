/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myapps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

/**
 *
 * @author karti
 */
public class DataDeserializer implements Deserializer<JsonNode>{
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DataDeserializer() {

    }

    @Override
    public void configure(Map<String, ?> config, boolean isKey) {
        //Nothing to Configure
    }

    @Override
    public void close() {

    }

    @Override
    public JsonNode deserialize(String topic, byte[] data) {
        if(data==null){
            return null;
        }
        try{
            return objectMapper.readTree(data);
        }catch (Exception e){
            throw new SerializationException(e);
        }
    }
}
