package com.icoffield.systems.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AddSystemRequest(
        @NotBlank
        @Size(max = 255)
        String name,
        LocalDate releaseDate
) {}
