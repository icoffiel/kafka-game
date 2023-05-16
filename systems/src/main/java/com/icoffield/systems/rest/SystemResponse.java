package com.icoffield.systems.rest;

import java.time.LocalDate;

public record SystemResponse(
        Long id,
        String name,
        LocalDate releaseDate
) {}