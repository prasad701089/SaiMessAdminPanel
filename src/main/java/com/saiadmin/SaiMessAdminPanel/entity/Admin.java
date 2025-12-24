package com.saiadmin.SaiMessAdminPanel.entity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String adminId;   // username
    private String password;
}

