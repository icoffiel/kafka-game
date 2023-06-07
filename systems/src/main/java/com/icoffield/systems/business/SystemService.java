package com.icoffield.systems.business;

import com.icoffield.systems.db.SystemEntity;
import com.icoffield.systems.db.SystemEntityRepository;
import com.icoffield.systems.rest.AddSystemRequest;
import com.icoffield.systems.rest.SystemResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class SystemService {
    private final SystemEntityRepository repository;

    public SystemService(SystemEntityRepository repository) {
        this.repository = repository;
    }

    public List<SystemResponse> getSystems() {
        return repository.findAll().stream()
                .map(systemEntity -> new SystemResponse(
                        systemEntity.getId(),
                        systemEntity.getName(),
                        systemEntity.getReleaseDate()
                ))
                .toList();
    }

    public Optional<SystemResponse> getSystem(Long id) {
        return repository.findById(id)
                .map(systemEntity -> new SystemResponse(
                        systemEntity.getId(),
                        systemEntity.getName(),
                        systemEntity.getReleaseDate()
                ));
    }

    public SystemResponse save(AddSystemRequest addSystemRequest) {
        // TODO - Adapter
        SystemEntity systemEntity = new SystemEntity();
        systemEntity.setName(addSystemRequest.name());
        systemEntity.setReleaseDate(addSystemRequest.releaseDate());

        SystemEntity savedSystemEntity = repository.save(systemEntity);

        return new SystemResponse(
                savedSystemEntity.getId(),
                savedSystemEntity.getName(),
                savedSystemEntity.getReleaseDate()
        );
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}

