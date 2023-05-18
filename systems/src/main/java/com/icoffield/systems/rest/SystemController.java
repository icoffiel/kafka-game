package com.icoffield.systems.rest;

import com.icoffield.systems.business.SystemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/systems")
public class SystemController {
    private final SystemService systemService;

    public SystemController(SystemService systemService) {
        this.systemService = systemService;
    }

    @GetMapping("/")
    public List<SystemResponse> getSystems() {
        return systemService.getSystems();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SystemResponse> getSystem(@PathVariable Long id) {
        return systemService
                .getSystem(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/")
    public SystemResponse save(@Valid @RequestBody AddSystemRequest addSystemRequest) {
        return systemService.save(addSystemRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        systemService.delete(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}

