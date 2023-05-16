package com.icoffiel.games.rest;

import com.icoffiel.games.business.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/games")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/")
    public List<GameResponse> getGames() {
        return gameService.getGames();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameResponse> getGame(@PathVariable Long id) {
        return gameService
                .getGame(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/")
    public GameResponse createGame(@RequestBody AddGameRequest addGameRequest) {
        return gameService.createGame(addGameRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGame(@PathVariable Long id) {
        gameService.deleteGame(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
