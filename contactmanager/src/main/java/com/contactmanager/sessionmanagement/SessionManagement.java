package com.contactmanager.sessionmanagement;

import org.springframework.stereotype.Component;

import com.contactmanager.model.Msg;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Component
public class SessionManagement {

    public void addSession(HttpServletRequest request, String name, Msg msg) {
        HttpSession session = request.getSession();
        session.setAttribute(name, msg);
    }

    public void removeSession(HttpServletRequest request, String name) {
        HttpSession session = request.getSession();
        session.removeAttribute(name);
    }
}