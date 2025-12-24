package com.saiadmin.SaiMessAdminPanel.repository;


import com.saiadmin.SaiMessAdminPanel.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {}
