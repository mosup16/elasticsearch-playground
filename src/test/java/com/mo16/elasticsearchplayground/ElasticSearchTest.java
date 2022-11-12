package com.mo16.elasticsearchplayground;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootTest
public class ElasticSearchTest {

    @Autowired
    private ElasticsearchClient elasticsearch;

    @Test
    @Disabled
    void testCreate10Documents() throws IOException {
        record TestDocument(UUID id, int index, LocalDateTime timestamp) {}
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            UUID id = UUID.randomUUID();
            int index = i;
            elasticsearch.index(b -> b.index("test-create-10-documents")
                    .document(new TestDocument(id, index, LocalDateTime.now()))
                    .id(id.toString()));
        }
        System.out.println("taken time = " + (System.currentTimeMillis() - start));
    }
}
