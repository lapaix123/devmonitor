package rw.gradtechgroup.devmonitor.service;

import rw.gradtechgroup.devmonitor.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User createUser(String name, String email, String password, User.Role role);
    Optional<User> findByEmail(String email);
    Optional<User> findById(UUID id);
    List<User> findAll();
    User updateUser(UUID id, String name, String email);
    void updateLastLogin(UUID userId);
    void deactivateUser(UUID id);
}