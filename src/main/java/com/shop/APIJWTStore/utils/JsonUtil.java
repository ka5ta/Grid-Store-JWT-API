package com.shop.APIJWTStore.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.http.NameValuePair;
import org.springframework.stereotype.Component;


import java.util.List;

public class JsonUtil {


    public static String convertToJsonString(Object object, List<NameValuePair> ListNameValuePairs) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement jsonElement = gson.toJsonTree(object);
        JsonObject asJsonObject = jsonElement.getAsJsonObject();

        for (NameValuePair nameValuePair : ListNameValuePairs) {
            asJsonObject.addProperty(nameValuePair.getName(), nameValuePair.getValue());
        }

        return gson.toJson(asJsonObject);
    }
}

