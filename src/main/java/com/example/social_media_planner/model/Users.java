package com.example.social_media_planner.model;
import com.example.social_media_planner.model.Users;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "app_users")
@Data
@NoArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    private String role = "USER";
}