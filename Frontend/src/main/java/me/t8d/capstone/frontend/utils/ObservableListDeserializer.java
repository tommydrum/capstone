package me.t8d.capstone.frontend.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.type.CollectionType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.util.List;

public class ObservableListDeserializer<T> extends JsonDeserializer<ObservableList<T>> {

    private Class<T> type;

    // no-argument constructor
    public ObservableListDeserializer() {
    }

    public void setType(Class<T> type) {
        this.type = type;
    }

    @Override
    public ObservableList<T> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        CollectionType javaType = ctxt.getTypeFactory().constructCollectionType(List.class, type);
        List<T> list = ctxt.readValue(p, javaType);
        return FXCollections.observableArrayList(list);
    }
}