/*
 * Copyright (c) 2020. Yuriy Stul
 */

package com.stulsoft.pmongo.spring.data;

import com.stulsoft.pmongo.spring.common.AbstractDocument;
import org.bson.Document;

import java.util.Optional;

/**
 * @author Yuriy Stul
 */
public class Test02 extends AbstractDocument {
    public Test02(Document document) {
        super(document);
    }

    public Optional<String> name() {
        return getString("name");
    }

    public Test02 withName(String name) {
        putString("name", name);
        return this;
    }

    public Optional<Long> age() {
        return getLong("age");
    }

    public Test02 withAge(Long age) {
        putLong("age", age);
        return this;
    }

    public Test02 withSum(Double sum) {
        putDouble("sum", sum);
        return this;
    }
}
