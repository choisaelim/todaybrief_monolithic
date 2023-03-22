package com.example.user.service;

import com.example.user.jpa.MapEntity;

public interface MapService {
    Iterable<MapEntity> getMapbyUserId(String userId);
}
