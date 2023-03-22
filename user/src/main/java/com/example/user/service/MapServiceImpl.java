package com.example.user.service;

import org.springframework.stereotype.Service;

import com.example.user.jpa.MapEntity;
import com.example.user.jpa.MapRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class MapServiceImpl implements MapService {
    private final MapRepository repository;

    @Override
    public Iterable<MapEntity> getMapbyUserId(String userId) {
        return repository.findByUserId(userId);
    }

}
