/*
 * Copyright (c) 2020. Yuriy Stul
 */

package com.stulsoft.pmongo.pagination;

import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

/**
 * @author Yuriy Stul
 */
public class Paginator2 {
    private static final Logger logger = LoggerFactory.getLogger(Paginator2.class);

    private final MongoClient client;
    private final String collection;

    public Paginator2(MongoClient client, String collection) {
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
     * @param query the query
     * @return response with details and result documents
     */
    public Single<JsonObject> readPage(int pageSize, int pageNumber, JsonObject query) {
        Objects.requireNonNull(query, "query should be specified");
        var thePageSize = Math.max(pageSize, 0);
        var thePageNumber = Math.max(pageNumber, 1);

        return Single.create(source -> {
            client.count(collection, query, arCount ->{
               if (arCount.succeeded()){
                   var count = arCount.result();
                   var lastPageNumber = Math.ceil(1.0 * count / pageSize);
                   var skip = thePageSize * (thePageNumber - 1);
                   var findOptions = new FindOptions()
                           .setSkip(skip)
                           .setBatchSize(pageSize)
                           .setLimit(pageSize);
                   client.findWithOptions(collection, query, findOptions, ar -> {
                       if (ar.succeeded()) {
                           if (!source.isDisposed()) {
                               var response = new JsonObject()
                               .put("pageSize", pageSize)
                               .put("pageNumber", pageNumber)
                               .put("lastPageNumber", lastPageNumber)
                               .put("data", new JsonArray(ar.result()));
                               source.onSuccess(response);
                           }
                       } else {
                           logger.error(ar.cause().getMessage(), ar.cause());
                           if (!source.isDisposed())
                               source.onError(ar.cause());
                       }
                   });
               }else{
                   logger.error(arCount.cause().getMessage(), arCount.cause());
                   if (!source.isDisposed())
                       source.onError(arCount.cause());
               }
            });
        });
    }
}
