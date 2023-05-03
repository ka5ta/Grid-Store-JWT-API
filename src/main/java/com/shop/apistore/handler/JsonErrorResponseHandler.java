package com.shop.apistore.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shop.apistore.dto.ErrorResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;

public final class JsonErrorResponseHandler {

    private JsonErrorResponseHandler() {
    }

    public static void jsonErrorResponseIssuer(HttpServletResponse response, String errorMessage, int errorCode)
            throws IOException {

        String jsonString = createJsonErrorMessage(errorMessage);

        PrintWriter out = response.getWriter();
        response.setStatus(errorCode);
        response.setContentType("application/json");
        out.print(jsonString);
        out.flush();
    }

    public static String createJsonErrorMessage(String errorMessage) {
        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().setDateFormat("dd/MM/yyyy HH:mm:ss zzz")
                .registerTypeAdapter(Instant.class, new InstantAdapter()).create();

        return gson.toJson(new ErrorResponse(errorMessage));
    }
}
