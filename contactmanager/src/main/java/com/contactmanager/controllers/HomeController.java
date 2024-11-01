package com.contactmanager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.contactmanager.entities.UserData;
import com.contactmanager.model.Msg;
import com.contactmanager.model.Role;
import com.contactmanager.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/signup")
    public String signup(Model model, HttpSession session) {
        model.addAttribute("user", new UserData());
        Msg msg = (Msg)session.getAttribute("msg");
        if(msg!=null) {
            model.addAttribute("msg", msg);
            session.removeAttribute("msg");
        }
        return "signup";
    }

    @PostMapping("/register")
    public String registration(@Valid @ModelAttribute UserData user, BindingResult br, Model model,
            HttpSession session) {
        if (br.hasErrors()) {
            model.addAttribute("user", user);
            return "signup";
        }
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (userService.saveUser(user) == null) {
            session.setAttribute("msg", new Msg("Please try with another email", "error"));
        } else {
            session.setAttribute("msg", new Msg("Registration successful", "success"));
        }

        return "redirect:/signup";
    }

    @GetMapping("/login")
    public String login(HttpSession session,Model model) {
        Msg msg = (Msg)session.getAttribute("msg");
        if(msg!=null) {
            model.addAttribute("msg", msg);
            session.removeAttribute("msg");
        }
        return "login";
    }
}
