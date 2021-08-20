/*
 * Copyright (c) 2021. Yuriy Stul
 */

package com.stulsoft.pmongo.spring.common;

import org.bson.BsonDouble;
import org.bson.BsonInt64;
import org.bson.BsonString;
import org.bson.Document;

import java.util.Objects;
import java.util.Optional;

/**
 * @author Yuriy Stul
 */
public abstract class AbstractDocument extends Document {
    protected AbstractDocument(Document document) {
        super(document);
    }

    public Optional<String> id() {
        try {
            return Optional.of(toBsonDocument().getObjectId("_id").getValue().toString());
        } catch (Exception ignore) {
            return Optional.empty();
        }
    }

    public Optional<String> getString(String key) {
        Objects.requireNonNull(key, "key is null");
        try {
            return Optional.ofNullable(toBsonDocument().getString(key).getValue());
        } catch (Exception ignore) {
            return Optional.empty();
        }
    }

    public void putString(String key, String text) {
        Objects.requireNonNull(key, "key is null");
        if (text == null)
            remove(key);
        else
            put(key, new BsonString(text));
    }

    public Optional<Long> getLong(String key) {
        Objects.requireNonNull(key, "key is null");
        try {
            return Optional.of(toBsonDocument().getNumber(key).longValue());
        } catch (Exception ignore) {
            return Optional.empty();
        }
    }

    public void putLong(String key, Long value) {
        Objects.requireNonNull(key, "key is null");
        if (value == null)
            remove(key);
        else
            put(key, new BsonInt64(value));
    }

    public Optional<Double> getDouble(String key) {
        Objects.requireNonNull(key, "key is null");
        try {
            return Optional.of(toBsonDocument().getDouble(key).getValue());
        } catch (Exception ignore) {
            return Optional.empty();
        }
    }

    public void putDouble(String key, Double value) {
        Objects.requireNonNull(key, "key is null");
        if (value == null)
            remove(key);
        else
            put(key, new BsonDouble(value));
    }
}
