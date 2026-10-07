package com.gym.repository;

import com.gym.entity.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, Integer> {
    Optional<Role> findByName(String name);
}
