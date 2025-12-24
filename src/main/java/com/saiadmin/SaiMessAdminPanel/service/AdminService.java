package com.saiadmin.SaiMessAdminPanel.service;


import com.saiadmin.SaiMessAdminPanel.entity.Admin;
import com.saiadmin.SaiMessAdminPanel.repository.AdminRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final AdminRepository repo;

    public AdminService(AdminRepository repo) {
        this.repo = repo;
    }

    public boolean validateLogin(String adminId, String password) {
        Admin admin = repo.findByAdminId(adminId);
        return admin != null && admin.getPassword().equals(password);
    }
}

