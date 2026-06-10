package com.cts.identityauth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    private boolean emailVerified = false;

    // ddl-auto=update adds the column with DEFAULT TRUE so existing rows are not locked out;
    // new registrations explicitly set this to false in AuthServiceImpl.register().
    @ColumnDefault("true")
    @Column(nullable = false)
    private boolean approved = false;

    private String emailVerificationToken;

    private String resetPasswordToken;

    private LocalDateTime tokenExpiry;
}