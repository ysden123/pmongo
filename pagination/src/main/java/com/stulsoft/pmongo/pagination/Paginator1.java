/*
 * Copyright (c) 2020. Yuriy Stul
 */

package com.stulsoft.pmongo.pagination;

import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author Yuriy Stul
 */
public class Paginator1 {
    private static final Logger logger = LoggerFactory.getLogger(Paginator1.class);

    private final MongoClient client;
    private final String collection;

    public Paginator1(MongoClient client, String collection) {
        Objects.requireNonNull(client, "client is undefined");
        Objects.requireNonNull(collection, "collection is undefined");
        this.client = client;
        this.collection = collection;
    }

    /**
     * Reads a page
     *
     * @param pageSize   the page size (record number per page)
     * @param pageNumber the page number; 1st page has number 1
     * @return list of objects
     */
    public Single<List<JsonObject>> readPage(int pageSize, int pageNumber) {
        var thePageSize = Math.max(pageSize, 0);
        var thePageNumber = Math.max(pageNumber, 1);

        return Single.create(source -> {
            var query = new JsonObject();
            var skip = thePageSize * (thePageNumber - 1);
            var findOptions = new FindOptions()
                    .setSkip(skip)
                    .setBatchSize(pageSize)
                    .setLimit(pageSize);
            client.findWithOptions(collection, query, findOptions, ar -> {
                if (ar.succeeded()) {
                    if (!source.isDisposed())
                        source.onSuccess(ar.result());
                } else {
                    logger.error(ar.cause().getMessage(), ar.cause());
                    if (!source.isDisposed())
                        source.onError(ar.cause());
                }
            });
        });
    }
}
