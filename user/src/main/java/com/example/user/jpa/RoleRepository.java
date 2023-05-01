package com.example.user.jpa;

import java.util.List;
import com.example.user.dto.ERole;

import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<RoleEntity, Long> {
    List<RoleEntity> findByName(ERole name);
}
