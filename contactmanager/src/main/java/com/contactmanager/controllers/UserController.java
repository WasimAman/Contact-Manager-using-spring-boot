package com.contactmanager.controllers;

import java.io.IOException;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.contactmanager.entities.Contact;
import com.contactmanager.entities.UserData;
import com.contactmanager.model.Msg;
import com.contactmanager.services.ContactService;
import com.contactmanager.services.FileService;
import com.contactmanager.services.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private FileService fileService;

    private void addUserToModel(Principal principal, Model model) {
        UserData user = userService.getUser(principal.getName());
        model.addAttribute("user", user);
    }

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model) {
        addUserToModel(principal, model);
        return "user/dashboard";
    }

    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable Integer page, Principal principal, Model model) {
        addUserToModel(principal, model);
        Pageable pageable = PageRequest.of(page, 2);
        UserData user = userService.getUser(principal.getName());
        Page<Contact> contacts = contactService.getContacts(user.getId(), pageable);
        model.addAttribute("contacts", contacts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPage", contacts.getTotalPages());
        // System.out.println("Size of contacts:- "+contacts.getSize());
        return "user/show-contacts";
    }

    @GetMapping("/delete/{contactId}/{page}")
    public String deleteContact(@PathVariable int contactId, @PathVariable int page) throws IOException {
        Contact contact = contactService.getContactById(contactId);
        if (contact.getProfileImg() != null) {
            fileService.deleteFile(contact.getProfileImg());
        }
        page = getTotalPages(page, contactId);
        contactService.deleteById(contact.getId());
        return "redirect:/user/show-contacts/" + page;
    }

    public int getTotalPages(int page, int contactId) {
        Contact contact = contactService.getContactById(contactId);
        Pageable pageable = PageRequest.of(page, 2);
        Page<Contact> contacts = contactService.getContacts(contact.getUser().getId(), pageable);
        int totalContacts = contacts.getNumberOfElements();
        System.out.println("TotalContact:- " + totalContacts);
        if (totalContacts == 1) {
            return page - 1 == -1 ? 0 : page - 1;
        }
        return page;
    }

    @GetMapping("/edit/{id}/{page}")
    public String editContact(@PathVariable String id, @PathVariable int page, Model model) {
        Contact contact = contactService.getContactById(Integer.parseInt(id));
        model.addAttribute("contact", contact);
        model.addAttribute("user", contact.getUser());
        model.addAttribute("page", page);
        return "/user/edit-contact";
    }

    @PostMapping("/edit-contact/{id}/{page}")
    public String editContact(@PathVariable String id, @PathVariable int page, @ModelAttribute Contact contact,
            @RequestParam(value = "profile-img", required = false) MultipartFile file,
            HttpSession session) {
        Contact existingContact = contactService.getContactById(Integer.parseInt(id));
        try {
            if (file != null && !file.isEmpty()) {
                if (existingContact.getProfileImg() != null) {
                    fileService.deleteFile(existingContact.getProfileImg());
                }
                String fileName = fileService.uploadFile(file);
                contact.setProfileImg(fileName);
            } else {
                contact.setProfileImg(existingContact.getProfileImg());
            }
            contact.setUser(existingContact.getUser());
            contactService.updateContact(contact);
        } catch (IOException e) {
            session.setAttribute("msg", new Msg("Failed to upload image: " + e.getMessage(), "error"));
            return "redirect:/user/edit/" + contact.getId();
        }

        return "redirect:/user/show-contacts/" + page;
    }

    @GetMapping("/add-contacts")
    public String addContacts(Principal principal, Model model, HttpSession session) {
        addUserToModel(principal, model);
        Msg msg = (Msg) session.getAttribute("msg");
        if (msg != null) {
            model.addAttribute("msg", msg);
            session.removeAttribute("msg");
        }
        model.addAttribute("contact", new Contact());
        return "user/add-contacts";
    }

    @PostMapping("/adding-contacts")
    public String addContacts(@ModelAttribute Contact contact,
            @RequestParam("profile-img") MultipartFile file,
            HttpSession session,
            Principal principal) {
        UserData user = userService.getUser(principal.getName());

        contact.setUser(user);

        try {
            String fileName = fileService.uploadFile(file);
            contact.setProfileImg(fileName);
        } catch (IOException e) {
            session.setAttribute("msg", new Msg("Failed to upload image: " + e.getMessage(), "error"));
            return "redirect:/user/add-contacts";
        }

        contactService.saveContact(contact);

        session.setAttribute("msg", new Msg("Contact added successfully!", "success"));

        return "redirect:/user/add-contacts";
    }

    @GetMapping("/user-profile/{id}/{page}")
    public String userProfile(@PathVariable String id, @PathVariable int page, Principal principal, Model model) {
        addUserToModel(principal, model);
        model.addAttribute("contact", contactService.getContactById(Integer.parseInt(id)));
        model.addAttribute("page", page);
        return "user/user-profile";
    }

    @GetMapping("/settings")
    public String settings(Principal principal, Model model) {
        addUserToModel(principal, model);
        return "user/settings";
    }

    @GetMapping("/profile")
    public String profile(Principal principal, Model model) {
        addUserToModel(principal, model);
        return "user/profile";
    }

    @PostMapping("/update-profile/{userId}")
    public String updateProfile(
            @PathVariable("userId") int userId,
            @RequestParam("profileImage") MultipartFile file,
            RedirectAttributes redirectAttributes) {

        System.out.println("We Are Here...");

        try {
            UserData user = userService.getUserById(userId);

            // Check if file is not empty before uploading
            if (!file.isEmpty()) {

                // Delete old profile picture if it exists
                if (user.getProfile() != null) {
                    fileService.deleteFile(user.getProfile());
                }
                
                String fileName = fileService.uploadFile(file);
                System.out.println("Uploaded File: " + fileName);

                // Update the user's profile image in the database
                user.setProfile(fileName);
                user.setAgreed(true);
                userService.updateUser(user);

                // Add success message to be displayed after redirection
                redirectAttributes.addFlashAttribute("msg", new Msg("Profile Updated", "success"));
            } else {
                redirectAttributes.addFlashAttribute("msg", new Msg("No file selected for upload", "error"));
            }

        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("msg", new Msg("Failed to update profile", "error"));
            e.printStackTrace();
        }

        return "redirect:/user/profile";
    }

}
