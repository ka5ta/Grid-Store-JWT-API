package com.shop.APIJWTStore.failureHandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {


    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        String message = "Access Denied. You are not authorized to see content.";
        JsonErrorResponseHandler.jsonErrorResponseIssuer(response, message, HttpServletResponse.SC_FORBIDDEN);
        log.error("Access to resources was denied: " + request.getServletPath());

    }
}

