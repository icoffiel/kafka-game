package com.icoffield.systems.rest;

import java.time.LocalDate;

public record AddSystemRequest(
        String name,
        LocalDate releaseDate
) {}
