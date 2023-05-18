package com.icoffiel.games.business;

import com.icoffiel.games.db.GameEntity;
import com.icoffiel.games.db.GameEntityRepository;
import com.icoffiel.games.db.kafka.KafkaSystemEntityRepository;
import com.icoffiel.games.exception.SystemNotFoundException;
import com.icoffiel.games.rest.AddGameRequest;
import com.icoffiel.games.rest.GameResponse;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

@Component
public class GameService {
    private final GameEntityRepository gameRepository;
    private final KafkaSystemEntityRepository kafkaSystemRepository;
    private final GameAdapter gameAdapter;

    public GameService(
            GameEntityRepository gameRepository,
            KafkaSystemEntityRepository kafkaSystemRepository,
            GameAdapter gameAdapter
    ) {
        this.gameRepository = gameRepository;
        this.kafkaSystemRepository = kafkaSystemRepository;
        this.gameAdapter = gameAdapter;
    }

    public List<GameResponse> getGames() {
        return gameRepository
                .findAll().stream()
                .map(gameAdapter::toGameResponse)
                .toList();
    }

    public Optional<GameResponse> getGame(Long id) {
        return gameRepository
                .findById(id)
                .map(gameAdapter::toGameResponse);
    }

    public GameResponse createGame(AddGameRequest addGameRequest) {
        return kafkaSystemRepository
                .findById(addGameRequest.systemId())
                .map(system -> {
                    GameEntity gameEntity = gameAdapter.toGameEntity(addGameRequest);
                    GameEntity savedGameEntity = gameRepository.save(gameEntity);
                    return gameAdapter.toGameResponse(savedGameEntity);
                })
                .orElseThrow(() -> new SystemNotFoundException(
                        MessageFormat.format("System with id {0} not found", addGameRequest.systemId())
                ));
    }

    public void deleteGame(Long id) {
        gameRepository.deleteById(id);
    }
}
