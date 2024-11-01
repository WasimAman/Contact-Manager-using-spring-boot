package com.contactmanager.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.contactmanager.model.Role;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user")
public class UserData implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotBlank(message = "Name cannot be empty")
    @Size(min = 8, max = 20, message = "Name must be between 8 to 20 characters")
    private String name;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Please enter a valid email")
    private String email; // Renamed from username

    @NotBlank(message = "Password cannot be empty")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,}$", message = "Password must have 1 digit, 1 uppercase, 1 lowercase, 1 special character")
    private String password; // Ensure this is hashed before saving

    @NotBlank(message = "Number cannot be empty")
    @Pattern(regexp = "^(?:\\+91[\\s-]?)?[789]\\d{9}$", message = "Enter valid mobile number")
    private String number;

    private String profile;
    @Enumerated(EnumType.STRING)
    private Role role;

    @Transient
    @AssertTrue(message = "Please accept terms & conditions")
    private boolean agreed;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Contact> contacts = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(() -> role.name());
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}