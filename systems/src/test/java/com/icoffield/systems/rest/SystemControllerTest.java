package com.icoffield.systems.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icoffield.systems.business.SystemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.icoffield.systems.utils.JsonUtils.asJsonString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SystemController.class)
class SystemControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SystemService systemService;

    @Test
    void getSystems_Success() throws Exception {
        final var now = LocalDate.now();
        List<SystemResponse> expected = List.of(
                new SystemResponse(1L, "System 1", now),
                new SystemResponse(2L, "System 2", now)
        );

        given(systemService.getSystems())
                .willReturn(expected);

        mvc.perform(get("/systems/").accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(content().json(asJsonString(objectMapper, expected)));
    }

    @Test
    void getSystemById_Success() throws Exception {
        SystemResponse systemResponse = new SystemResponse(
                1L,
                "System 1",
                LocalDate.now()
        );
        given(systemService.getSystem(1L))
                .willReturn(Optional.of(systemResponse));

        mvc.perform(get("/systems/1").accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(objectMapper, systemResponse)));
    }

    @Test
    void getSystemById_NotFound() throws Exception {
        given(systemService.getSystem(1L))
                .willReturn(Optional.empty());

        mvc.perform(get("/systems/1").accept("application/json"))
                .andExpect(status().isNotFound());
    }

    @Test
    void save_Success() throws Exception {
        AddSystemRequest validRequest = validAddSystemRequest();
        SystemResponse systemResponse = new SystemResponse(1L, "System 1", LocalDate.now());

        given(systemService.save(validRequest))
                .willReturn(systemResponse);

        mvc.perform(
                        post("/systems/")
                                .contentType("application/json")
                                .content(asJsonString(objectMapper, validRequest))
                )
                .andExpect(status().isCreated())
                .andExpect(content().json(asJsonString(objectMapper, systemResponse)));
    }

    @Test
    void save_Invalid() throws Exception {
        AddSystemRequest invalidRequest = invalidAddSystemRequest();
        mvc.perform(
                        post("/systems/")
                                .contentType("application/json")
                                .content(asJsonString(objectMapper, invalidRequest))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete_Success() throws Exception {
        mvc.perform(delete("/systems/1"))
                .andExpect(status().isAccepted());
    }

    private AddSystemRequest validAddSystemRequest() {
        return new AddSystemRequest(
                "System 1",
                LocalDate.now()
        );
    }

    private AddSystemRequest invalidAddSystemRequest() {
        return new AddSystemRequest(
                null,
                LocalDate.now()
        );
    }

}