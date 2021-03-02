package pl.wolny.junglenokaut.updater;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class Json {
    private static ObjectMapper objectMapper = getDefaultObjectMapper();

    public static ObjectMapper getDefaultObjectMapper(){
        ObjectMapper defaultObjectMapper = new ObjectMapper();
        defaultObjectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        return defaultObjectMapper;
    }
    public static JsonNode prase(String src) throws IOException {
        return  objectMapper.readTree(src);
    }
    //Source: https://www.youtube.com/watch?v=Hv_a3ZBSO_g
}
