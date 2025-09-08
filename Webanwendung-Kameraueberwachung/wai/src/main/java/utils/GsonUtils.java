package utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletRequest;

public class GsonUtils {
    
    public static <T> List<T> parseBodyIntoJson(HttpServletRequest req, Class<T> clazz, String rootTarget) throws IOException{

        Gson gson = new Gson();
        List<T> result = new ArrayList<>();

        String body = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        JsonObject root = JsonParser.parseString(body).getAsJsonObject();

        try {

            JsonArray array = root.getAsJsonArray(rootTarget);

            for(JsonElement element: array){
                result.add(gson.fromJson(element, clazz));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return result;
    }

    public static <T> List<T> parseJsonArray(String target, Class<T> clazz){

        Gson gson = new Gson();
        List<T> result = new ArrayList<>();

        if(target == null){
            return result;
        }
        JsonArray root = JsonParser.parseString(target).getAsJsonArray();

        for(JsonElement ele : root){
            
            result.add(gson.fromJson(ele, clazz));
        }
        
        return result;
    }
}
// }