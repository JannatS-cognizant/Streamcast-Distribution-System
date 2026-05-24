package com.cts.identityauth.service.impl;

import com.cts.identityauth.auth.JwtUtil;
import com.cts.identityauth.dto.request.LoginRequest;
import com.cts.identityauth.dto.request.UserRequestDTO;
import com.cts.identityauth.dto.response.LoginResponseDTO;
import com.cts.identityauth.dto.response.UserResponseDTO;
import com.cts.identityauth.entity.Role;
import com.cts.identityauth.entity.User;
import com.cts.identityauth.exception.BadRequestException;
import com.cts.identityauth.exception.ResourceNotFoundException;
import com.cts.identityauth.repository.RoleRepository;
import com.cts.identityauth.repository.UserRepository;
import com.cts.identityauth.service.AuthService;
import com.cts.identityauth.service.EmailService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final BCryptPasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    public AuthServiceImpl(UserRepository userRepo,
                           RoleRepository roleRepo,
                           BCryptPasswordEncoder encoder,
                           JwtUtil jwtUtil,
                           EmailService emailService) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
    }

    @Override
    public UserResponseDTO register(UserRequestDTO dto) {
        if (userRepo.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("Email already registered: " + dto.getEmail());
        }

        Role role = roleRepo.findById(dto.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + dto.getRoleId()));

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setRole(role);
        user.setEmailVerified(false);
        user.setApproved(false); // self-registrations require admin approval

        String token = UUID.randomUUID().toString();
        user.setEmailVerificationToken(token);
        user.setTokenExpiry(LocalDateTime.now().plusHours(24));

        User saved = userRepo.save(user);

        emailService.sendEmail(
                saved.getEmail(),
                "Verify your StreamCast email",
                "Click to verify: http://localhost:8081/auth/verify?token=" + token
        );

        return toUserResponse(saved);
    }

    @Override
    public LoginResponseDTO login(LoginRequest req) {
        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if (!user.isEmailVerified()) {
            throw new BadRequestException("Please verify your email before logging in");
        }

        if (!user.isApproved()) {
            throw new BadRequestException("Account pending admin approval");
        }

        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid email or password");
        }

        // Normalise role for JWT claim: "Content Owner" → "CONTENT_OWNER"
        String roleForToken = user.getRole().getName()
                .toUpperCase()
                .replace(" ", "_");

        LoginResponseDTO response = new LoginResponseDTO();
        response.setToken(jwtUtil.generateToken(user.getEmail(), roleForToken));
        response.setUser(toUserResponse(user));
        return response;
    }

    /**
     * Admin-only login. Validates credentials and additionally enforces that the
     * authenticated user holds the ADMIN role. Non-admin accounts are rejected
     * even when their email/password are correct, so this endpoint can back a
     * dedicated administrator console.
     */
    @Override
    public LoginResponseDTO adminLogin(LoginRequest req) {
        LoginResponseDTO response = login(req);
        String role = response.getUser().getRole();
        String normalised = role == null ? "" : role.toUpperCase().replace(" ", "_");
        if (!"ADMIN".equals(normalised)) {
            throw new BadRequestException("This account is not authorised for the admin console");
        }
        return response;
    }

    @Override
    public void verifyEmail(String token) {
        User user = userRepo.findByEmailVerificationToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid verification token"));

        if (user.getTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Verification token has expired. Please register again.");
        }

        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        user.setTokenExpiry(null);
        userRepo.save(user);
    }

    @Override
    public void forgotPassword(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No account found with email: " + email));

        String token = UUID.randomUUID().toString();
        user.setResetPasswordToken(token);
        user.setTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepo.save(user);

        emailService.sendEmail(
                email,
                "Reset your StreamCast password",
                "Reset link: http://localhost:8081/auth/reset-password?token=" + token
        );
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        User user = userRepo.findByResetPasswordToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid or expired reset token"));

        if (user.getTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Reset token has expired. Please request a new one.");
        }

        user.setPassword(encoder.encode(newPassword));
        user.setResetPasswordToken(null);
        user.setTokenExpiry(null);
        userRepo.save(user);
    }

    @Override
    public void sendUsername(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No account found with email: " + email));

        emailService.sendEmail(
                email,
                "Your StreamCast username",
                "Your username is: " + user.getName()
        );
    }

    // ── helper ────────────────────────────────────────────
    private UserResponseDTO toUserResponse(User user) {
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