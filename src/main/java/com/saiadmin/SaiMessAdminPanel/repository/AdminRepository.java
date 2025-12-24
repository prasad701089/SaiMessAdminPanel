package com.saiadmin.SaiMessAdminPanel.repository;
import com.saiadmin.SaiMessAdminPanel.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Admin findByAdminId(String adminId);
}
