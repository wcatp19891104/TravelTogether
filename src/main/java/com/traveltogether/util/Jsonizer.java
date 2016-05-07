package com.traveltogether.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import org.joda.money.Money;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class Jsonizer {

    private final ObjectMapper objectMapper;

    @Inject
    public Jsonizer(ObjectMapper mapper) {
        this.objectMapper = mapper;
    }

    public String toJson(Object object) {
        String ret;
        try {
            ret = objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("fail to convert obj to json " + e.getMessage());
        }

        return ret;
    }

    public String toJsonList(List<? extends Object> objects) {
        StringBuilder sb = new StringBuilder();
        for (Object o : objects) {
            sb.append(toJson(o));
            sb.append("\n");
        }

        return sb.toString();
    }

    public <T> T toObject(String jsonString, Class<T> clazz) {
        try {
            return objectMapper.readValue(jsonString, clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException("fail to convert obj to json " + e.getMessage());
        }
    }

    public <T> List<T> toObjectList(String jsonString, Class<? extends T> clazz) {
        List<T> t = Lists.newArrayList();
        Iterator<String> stringIterator = Splitter.on('\n').split(jsonString).iterator();
        String first = stringIterator.next();
        t.add(toObject(first, clazz));

        while (stringIterator.hasNext()) {
            String current = stringIterator.next();
            if ( !current.isEmpty() ) {
                t.add(toObject(current, clazz));
            }
        }

        return t;
    }

}
