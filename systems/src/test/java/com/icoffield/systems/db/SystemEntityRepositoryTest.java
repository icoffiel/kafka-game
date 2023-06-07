package com.icoffield.systems.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest
@Testcontainers
class SystemEntityRepositoryTest {
    @Autowired
    private SystemEntityRepository systemEntityRepository;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> POSTGRES_CONTAINER = new PostgreSQLContainer<>("postgres:13");

    @Test
    void containerIsRunning() {
        assertThat(POSTGRES_CONTAINER.isRunning(), is(true));
    }

    @Test
    void emptyResult_Success() {
        assertThat(
                systemEntityRepository.findAll().size(),
                is(0)
        );
    }

    @Test
    void oneResult_Success() {
        SystemEntity entity = new SystemEntity();
        entity.setName("test");
        entity.setCreatedDate(Instant.now());

        SystemEntity savedEntity = systemEntityRepository.save(entity);

        assertThat(
                systemEntityRepository.findAll().size(),
                is(1)
        );

        assertThat(savedEntity.getId(), notNullValue());
        assertThat(savedEntity.getName(), is(entity.getName()));
        assertThat(savedEntity.getCreatedDate(), is(entity.getCreatedDate()));
    }
}