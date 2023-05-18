package com.icoffiel.games.rest;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record AddGameRequest(
        @NotBlank
        @Size(max = 255)
        String name,

        @Size(max = 255)
        String description,

        @Min(1)
        Integer numberOfPlayers,

        @Min(0)
        Double price,

        @NotNull
        long systemId,

        LocalDate releaseDate
) {
}
