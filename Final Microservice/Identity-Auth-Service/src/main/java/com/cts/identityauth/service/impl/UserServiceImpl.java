package com.cts.identityauth.service.impl;

import com.cts.identityauth.dto.request.UserRequestDTO;
import com.cts.identityauth.dto.response.UserResponseDTO;
import com.cts.identityauth.entity.Role;
import com.cts.identityauth.entity.User;
import com.cts.identityauth.exception.ResourceNotFoundException;
import com.cts.identityauth.repository.RoleRepository;
import com.cts.identityauth.repository.UserRepository;
import com.cts.identityauth.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final BCryptPasswordEncoder encoder;

    public UserServiceImpl(UserRepository userRepo,
                           RoleRepository roleRepo,
                           BCryptPasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.encoder = encoder;
    }

    @Override
    public UserResponseDTO createUser(UserRequestDTO dto) {
        Role role = roleRepo.findById(dto.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + dto.getRoleId()));

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setRole(role);
        // Admin-created users skip both gates so the admin can sign someone in immediately.
        user.setEmailVerified(true);
        user.setApproved(true);

        return toResponse(userRepo.save(user));
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepo.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    public UserResponseDTO updateUser(Long id, UserRequestDTO dto) {
        User user = findOrThrow(id);

        Role role = roleRepo.findById(dto.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + dto.getRoleId()));

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setRole(role);

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(encoder.encode(dto.getPassword()));
        }

        return toResponse(userRepo.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        userRepo.delete(findOrThrow(id));
    }

    @Override
    public UserResponseDTO setApproved(Long id, boolean approved) {
        User user = findOrThrow(id);
        user.setApproved(approved);
        // Admin approval implies the email is trusted — bypass the email-verification
        // gate so the user can log in immediately after approval, even if they never
        // clicked the verification link.
        if (approved) {
            user.setEmailVerified(true);
            user.setEmailVerificationToken(null);
            user.setTokenExpiry(null);
        }
        return toResponse(userRepo.save(user));
    }

    // ── helpers ───────────────────────────────────────────
    private User findOrThrow(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private UserResponseDTO toResponse(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().getName());
        dto.setEmailVerified(user.isEmailVerified());
        dto.setApproved(user.isApproved());
        return dto;
    }
}