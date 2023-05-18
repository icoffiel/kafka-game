package com.icoffiel.games.db.kafka;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KafkaSystemEntityRepository extends ListCrudRepository<KafkaSystemEntity, Long> { }
