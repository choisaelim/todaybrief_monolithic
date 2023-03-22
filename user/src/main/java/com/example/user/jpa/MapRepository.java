package com.example.user.jpa;

import org.springframework.data.repository.CrudRepository;

public interface MapRepository extends CrudRepository<MapEntity, Long> {
    Iterable<MapEntity> findByUserId(String userId);
}
