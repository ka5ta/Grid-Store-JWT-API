package com.shop.apistore.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.http.NameValuePair;

import java.util.List;

public final class JsonUtil {

    private JsonUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static String convertToJsonString(Object object, List<NameValuePair> listNameValuePairs) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement jsonElement = gson.toJsonTree(object);
        JsonObject asJsonObject = jsonElement.getAsJsonObject();

        for (NameValuePair nameValuePair : listNameValuePairs) {
            asJsonObject.addProperty(nameValuePair.getName(), nameValuePair.getValue());
        }

        return gson.toJson(asJsonObject);
    }
}
