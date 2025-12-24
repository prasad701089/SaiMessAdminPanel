package com.saiadmin.SaiMessAdminPanel.config;



import com.saiadmin.SaiMessAdminPanel.entity.Admin;
import com.saiadmin.SaiMessAdminPanel.repository.AdminRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final AdminRepository repo;

    public DataLoader(AdminRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) {
        if (repo.findByAdminId("admin") == null) {
            Admin admin = new Admin();
            admin.setAdminId("admin");
            admin.setPassword("admin123");
            repo.save(admin);
            System.out.println("DEFAULT ADMIN CREATED: admin / admin123");
        }
    }
}
