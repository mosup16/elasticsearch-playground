package com.mo16.elasticsearchplayground;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Random;
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

    @Test
    @Disabled
    void sendFakeCPUUsageReadDocument() throws IOException {
        record CpuUsage(UUID id, double usage, LocalDateTime readAt){}

        IndexResponse response = elasticsearch.index(i -> {
            CpuUsage usage = new CpuUsage(UUID.randomUUID(), new Random().nextDouble(100),
                    LocalDateTime.now());
            return i.index("fake-cpu-usage").document(usage).id(usage.id().toString());
        });

        System.out.println(response);
    }

    @Test
    @Disabled
    void deleteIndex() throws IOException {
        DeleteIndexResponse response = elasticsearch.indices()
                .delete(deleteReq -> deleteReq.index("fake-cpu-usage"));
        System.out.println(response);
    }

    @Test
    @Disabled
    void createFakeCpuUsageIndex() throws IOException {
        String mappings = new ObjectMapper().writeValueAsString(
                new HashMap<>() {{
                    put("properties", new HashMap<>() {{
                        put("id", new HashMap<>() {{
                            put("type", "text");
                        }});
                        put("usage", new HashMap<>() {{
                            put("type", "float");
                        }});
                        put("readAt", new HashMap<>() {{
                            put("type", "date");
                        }});
                    }});
                }}
        );
        CreateIndexResponse response = elasticsearch.indices()
                .create(createReq -> createReq.index("fake-cpu-usage")
                        .mappings(mappingsBuilder -> mappingsBuilder
                                .withJson(new StringReader(mappings)))
                );
        System.out.println(response);
    }
}
