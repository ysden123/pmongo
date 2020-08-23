/*
 * Copyright (c) 2020. Yuriy Stul
 */

package com.stulsoft.pmongo.spring.data;

import org.bson.Document;

/**
 * @author Yuriy Stul
 */
public class Test013 {
    private String name;
    private Integer age;
    private Integer sum;
    private String new_f;
    private Comb comb;

    @Override
    public String toString() {
        return "Test013{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", sum=" + sum +
                ", new_f='" + new_f + '\'' +
                ", comb=" + comb +
                '}';
    }
}
