package com.icoffield.systems.db;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemEntityRepository extends ListCrudRepository<SystemEntity, Long> { }