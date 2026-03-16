package rw.gradtechgroup.devmonitor.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.gradtechgroup.devmonitor.entity.User;
import rw.gradtechgroup.devmonitor.repository.UserRepository;
import rw.gradtechgroup.devmonitor.service.AuditService;
import rw.gradtechgroup.devmonitor.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuditService auditService;

    @Override
    public User createUser(String name, String email, String password, User.Role role) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role);
        user.setIsActive(true);
        user.setEmailVerified(false);
        user.setTwoFactorEnabled(false);

        User savedUser = userRepository.save(user);
        auditService.logAction(savedUser.getId(), "CREATE_USER", "User", savedUser.getId().toString(), null);
        
        return savedUser;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userRepository.findByIdAndIsActiveTrue(id);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(UUID id, String name, String email) {
        User user = userRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        user.setName(name);
        user.setEmail(email);

        User updatedUser = userRepository.save(user);
        auditService.logAction(user.getId(), "UPDATE_USER", "User", user.getId().toString(), null);
        
        return updatedUser;
    }

    @Override
    public void updateLastLogin(UUID userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);
        });
    }

    @Override
    public void deactivateUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setIsActive(false);
        userRepository.save(user);
        auditService.logAction(user.getId(), "DEACTIVATE_USER", "User", user.getId().toString(), null);
    }
}