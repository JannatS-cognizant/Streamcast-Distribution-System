package com.Cts.service;

import com.Cts.security.JwtUtil;
import com.Cts.dto.request.LoginRequest;
import com.Cts.dto.request.UserRequestDTO;
import com.Cts.dto.response.LoginResponseDTO;
import com.Cts.dto.response.UserResponseDTO;
import com.Cts.entity.*;
import com.Cts.mapper.request.UserMapper;
import com.Cts.repository.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final BCryptPasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepo, RoleRepository roleRepo,
                           BCryptPasswordEncoder encoder, JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public UserResponseDTO register(UserRequestDTO dto) {

        Role role = roleRepo.findById(dto.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        User user = new User(
                null,
                dto.getName(),
                dto.getEmail(),
                encoder.encode(dto.getPassword()),
                false,
                role
        );

        return UserMapper.toResponse(userRepo.save(user));
    }

    @Override
    public LoginResponseDTO login(LoginRequest req) {

        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email"));

        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        LoginResponseDTO res = new LoginResponseDTO();
        res.setToken(jwtUtil.generateToken(user.getEmail()));
        res.setUser(UserMapper.toResponse(user));

        return res;
    }
}