package com.mo16.elasticsearchplayground;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class ElasticsearchPlaygroundApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElasticsearchPlaygroundApplication.class, args);
    }


    @Bean
    public ElasticsearchClient elasticsearchClient(
            JacksonJsonpMapper jacksonJsonpMapper,
            @Value("${application.elasticsearch.hostname}") String hostname,
            @Value("${application.elasticsearch.port}") int port,
            @Value("${application.elasticsearch.username}") String username,
            @Value("${application.elasticsearch.password}") String password) {

        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(username, password));

        RestClient restClient = RestClient.builder(new HttpHost(hostname, port,"https"))
                .setHttpClientConfigCallback(cb -> cb.setDefaultCredentialsProvider(credentialsProvider))
                .build();

        ElasticsearchTransport transport = new RestClientTransport(restClient, jacksonJsonpMapper);

        return new ElasticsearchClient(transport);
    }

    @Bean
    CommandLineRunner commandLineRunner() {
        return args -> System.setProperty("com.sun.net.ssl.checkRevocation", "false");
    }

    @Bean
    public JacksonJsonpMapper jacksonJsonpMapper() {
        JacksonJsonpMapper mapper = new JacksonJsonpMapper();
        mapper.objectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }
}
