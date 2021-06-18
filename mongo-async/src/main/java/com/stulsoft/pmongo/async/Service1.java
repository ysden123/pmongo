/*
 * Copyright (c) 2020. Yuriy Stul
 */

package com.stulsoft.pmongo.async;

import com.mongodb.reactivestreams.client.MongoClient;
import org.bson.Document;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * @author Yuriy Stul
 */
@Service
public class Service1 {
    private static final Logger logger = LoggerFactory.getLogger(Service1.class);
    private final MongoClient mongoClient;

    public Service1(@Autowired @Qualifier("AppMongoClient") MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public CompletableFuture<Void> showDocuments() {
        var future = new CompletableFuture<Void>();
        logger.debug("==>showDocuments");
        try {
            var db = mongoClient.getDatabase("pmongo");
            var collection = db.getCollection("test_01");

/*
            collection
                    .countDocuments()
                    .subscribe(new Subscriber<Long>() {
                        @Override
                        public void onSubscribe(Subscription subscription) {
                            subscription.request(123);
                        }

                        @Override
                        public void onNext(Long aLong) {
                            logger.debug("test_01 contains {} documents", aLong);
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            logger.error(throwable.getMessage());
                            future.completeExceptionally(throwable);
                        }

                        @Override
                        public void onComplete() {
                            future.complete(null);
                        }
                    });
*/

            collection.find().subscribe(new Subscriber<>() {
                @Override
                public void onSubscribe(Subscription subscription) {
                    subscription.request(Long.MAX_VALUE);
                }

                @Override
                public void onNext(Document document) {
                    logger.debug("{}", document.toJson());
                }

                @Override
                public void onError(Throwable throwable) {
                    logger.error(throwable.getMessage());
                    future.completeExceptionally(throwable);
                }

                @Override
                public void onComplete() {
                    future.complete(null);
                }
            });
        } catch (Exception exception) {
            logger.error(exception.getMessage(), exception);
        }
        return future;
    }
}
