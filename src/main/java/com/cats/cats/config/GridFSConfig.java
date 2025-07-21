package com.cats.cats.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GridFSConfig {

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    private final MongoClient mongoClient;

    @Autowired
    public GridFSConfig(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @Bean(name = "gridFSImagesBucket")
    public GridFSBucket gridFSImagesBucket() {
        MongoDatabase db = mongoClient.getDatabase(databaseName);
        return GridFSBuckets.create(db, "cats_images");
    }

    @Bean(name = "gridFSVideosBucket")
    public GridFSBucket gridFSVideosBucket() {
        MongoDatabase db = mongoClient.getDatabase(databaseName);
        return GridFSBuckets.create(db, "cats_videos");
    }
}
