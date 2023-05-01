package com.example.user.jpa;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.user.dto.UserDto;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
    UserEntity findByUserId(String userId);

    List<UserEntity> findByUsername(String userName);

    UserEntity findByEmail(String email);
}
