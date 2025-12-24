package com.saiadmin.SaiMessAdminPanel.controller;

import com.saiadmin.SaiMessAdminPanel.entity.User;
import com.saiadmin.SaiMessAdminPanel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @GetMapping("/users")
    public String usersPage(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("newUser", new User());
        return "users";
    }

    // ➕ Add new user
    @PostMapping("/users/add")
    public String addUser(@ModelAttribute User newUser) {
        userRepository.save(newUser);
        return "redirect:/users";
    }

    // ❌ Delete user
    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/users";
    }
}
