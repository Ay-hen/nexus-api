package com.nexus.app.common.config;

import java.util.concurrent.TimeUnit;

import org.bson.Document;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.mongodb.client.model.IndexOptions;

/** Creates indexes explicitly at startup (idempotent). Auto-index-creation stays off. */
@Component
@Order(1)
public class IndexInitializer implements ApplicationRunner {

    private final MongoTemplate mongo;

    public IndexInitializer(MongoTemplate mongo) {
        this.mongo = mongo;
    }

    @Override
    public void run(ApplicationArguments args) {
        unique("users", new Document("email", 1), "uk_users_email");
        plain("users", new Document("kind", 1).append("status", 1), "ix_users_kind_status");

        unique("admin_users", new Document("userId", 1), "uk_admin_users_userId");
        plain("admin_users", new Document("role", 1), "ix_admin_users_role");

        unique("refresh_tokens", new Document("tokenHash", 1), "uk_refresh_tokens_hash");
        plain("refresh_tokens", new Document("userId", 1).append("revokedAt", 1), "ix_refresh_tokens_user_revoked");
        plain("refresh_tokens", new Document("familyId", 1), "ix_refresh_tokens_family");
        mongo.getCollection("refresh_tokens").createIndex(new Document("expireAt", 1),
                new IndexOptions().name("ttl_refresh_tokens_expireAt").expireAfter(0L, TimeUnit.SECONDS));
    }

    private void unique(String collection, Document keys, String name) {
        mongo.getCollection(collection).createIndex(keys, new IndexOptions().name(name).unique(true));
    }

    private void plain(String collection, Document keys, String name) {
        mongo.getCollection(collection).createIndex(keys, new IndexOptions().name(name));
    }
}