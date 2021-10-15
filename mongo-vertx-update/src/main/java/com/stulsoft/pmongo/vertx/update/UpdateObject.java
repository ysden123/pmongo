/*
 * Copyright (c) 2021. StulSoft
 */

package com.stulsoft.pmongo.vertx.update;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Yuriy Stul
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UpdateObject(Object _id) {
}
