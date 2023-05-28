package com.icoffiel.notifications.db;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreferenceEntity, Long> {
    List<NotificationPreferenceEntity> findAllBySystemId(Long systemId);
}
