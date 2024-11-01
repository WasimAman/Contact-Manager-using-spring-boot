package com.contactmanager.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.contactmanager.entities.UserData;

@Repository
public interface UserRepository extends JpaRepository<UserData, Integer> {
    Optional<UserData> findByEmail(String email);
}
