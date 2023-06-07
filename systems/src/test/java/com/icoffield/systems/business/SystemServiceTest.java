package com.icoffield.systems.business;

import com.icoffield.systems.db.SystemEntity;
import com.icoffield.systems.db.SystemEntityRepository;
import com.icoffield.systems.rest.SystemResponse;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SystemServiceTest {
    @Mock
    private SystemEntityRepository repository;
    @InjectMocks
    private SystemService systemService;

    @Test
    void getSystems_Success() {
        when(repository.findAll())
                .thenReturn(
                        List.of(
                                createSystemEntity(1L),
                                createSystemEntity(2L)
                        )
                );

        List<SystemResponse> result = systemService.getSystems();

        assertThat(result.size(), Matchers.is(2));
    }

    private SystemEntity createSystemEntity(Long id) {
        SystemEntity systemEntity = new SystemEntity();
        systemEntity.setId(id);
        systemEntity.setName("system " + id);
        systemEntity.setReleaseDate(LocalDate.now());

        return systemEntity;
    }
}