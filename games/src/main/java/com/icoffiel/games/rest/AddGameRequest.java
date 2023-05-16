package com.icoffiel.games.rest;

import java.time.LocalDate;

public record AddGameRequest(
    String name,
    String description,
    int numberOfPlayers,
    Double price,
    long systemId,
    LocalDate releaseDate
) { }
