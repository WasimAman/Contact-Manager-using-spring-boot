package com.contactmanager.security;

import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import com.contactmanager.model.Msg;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import org.springframework.security.core.AuthenticationException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
    AuthenticationException exception)
            throws IOException, ServletException {
        HttpSession session = request.getSession();
        session.setAttribute("msg", new Msg("Invalid username or password", "error"));
        response.sendRedirect("/login");
    }
}
