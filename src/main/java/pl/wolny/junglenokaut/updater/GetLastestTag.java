package pl.wolny.junglenokaut.updater;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GetLastestTag {
    public static String OpenCon(){
        try {
            URL url = new URL("https://api.github.com/repos/WcaleNieWolny/TobiaszNokaut/releases/latest");
            try {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                int status = connection.getResponseCode();
                if(status == 200){
                    StringBuffer stringBuffer = new StringBuffer();
                    String line;
                    java.io.BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    while ((line = reader.readLine()) != null){
                        stringBuffer.append(line);
                    }
                    reader.close();
                    String JsonToPrase = stringBuffer.toString().replace("\t", "");
                    //System.out.println(JsonToPrase);
                    JsonNode node = Json.prase(JsonToPrase);
                    return node.get("tag_name").asText();

                }else {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }
    //Source: https://www.youtube.com/watch?v=qzRKa8I36Ww&t=572s
}
