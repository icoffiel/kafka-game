package com.icoffield.systems.rest;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record AddSystemRequest(
        @NotBlank
        @Max(255)
        String name,
        LocalDate releaseDate
) {}
