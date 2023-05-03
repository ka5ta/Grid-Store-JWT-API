package com.shop.apistore.handler;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Instant;

public class InstantAdapter extends TypeAdapter<Instant> {

    @Override
    public void write(JsonWriter writer, Instant instant) throws IOException {
        if (instant == null) {
            writer.nullValue();
            return;
        }

        writer.value(instant.toString());
    }

    @Override
    public Instant read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }

        return Instant.parse(reader.nextString());
    }
}
