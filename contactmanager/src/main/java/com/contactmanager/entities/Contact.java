package com.contactmanager.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotBlank(message = "Contact name cannot be empty")
    private String name;

    @Email(message = "Please enter a valid email")
    private String email;

    @Pattern(regexp = "^(?:\\+91[\\s-]?)?[789]\\d{9}$", message = "Enter valid mobile number")
    private String number;

    private String nickname;
    private String work;
    private String profileImg;

    @Column(length = 100)
    private String about;

    @ManyToOne
    @JsonIgnore
    private UserData user;
}
