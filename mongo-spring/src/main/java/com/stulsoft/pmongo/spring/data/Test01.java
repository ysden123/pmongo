/*
 * Copyright (c) 2020. Yuriy Stul
 */

package com.stulsoft.pmongo.spring.data;

import org.bson.Document;

/**
 * @author Yuriy Stul
 */
public class Test01 extends Document {
    public Test01(Document document) {
        super(document);
    }

    public Test01 fromDocument(final Document document){
        return new Test01(document);
    }
}
