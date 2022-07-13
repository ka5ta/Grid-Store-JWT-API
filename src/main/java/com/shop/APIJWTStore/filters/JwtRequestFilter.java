package com.shop.APIJWTStore.filters;

import com.shop.APIJWTStore.failureHandler.JsonErrorResponseHandler;
import com.shop.APIJWTStore.service.JwtUserDetailsService;
import com.shop.APIJWTStore.utils.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;

import io.jsonwebtoken.MalformedJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUserDetailsService jwtUserDetailsService;

    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public JwtRequestFilter(@Lazy JwtUserDetailsService jwtUserDetailsService, JwtTokenUtil jwtTokenUtil) {
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //To skip OncePerRequestFilter for register and authenticate
        if(shouldSkipFilter(request.getServletPath())){
            filterChain.doFilter(request, response);
            return;
        }

        String email = null;
        String jwtToken = null;

        try {
            // get token from header
            logger.info(request.getServletPath());
            jwtToken = jwtTokenUtil.getToken(request);
            logger.info("AuthToken: " + jwtToken);

            logger.info(request.getServletPath());

            //Get email from token
            email = jwtTokenUtil.getEmailFromToken(jwtToken);
            logger.info("User email: " + email);

       } catch (IllegalArgumentException e) {
            logger.warn(e.getMessage());
            JsonErrorResponseHandler.jsonErrorResponseIssuer(response, "Access Denied. No Token ...", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        } catch (ExpiredJwtException | MalformedJwtException  e){
            logger.warn("JWT Token is invalid (expired or malformed) " + e.getMessage());
            JsonErrorResponseHandler.jsonErrorResponseIssuer(response, "Access Denied. Token is Expired or invalid.", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }


        // Validates the token.
        // Do not override another form of authentication.
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        if (email != null && currentAuth == null) {
            // get user
            UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(email);

            if (jwtTokenUtil.isValidToken(jwtToken, userDetails)) {
                // create authentication
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        jwtTokenUtil.createAuthentication(userDetails, request);

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            } else {
                logger.error("Token is invalid. Please verify.");
            }
        }
        filterChain.doFilter(request, response);
    }

    public boolean shouldSkipFilter(String servletPath){
        Set<String> setOfLinksToSkip = new HashSet<>();
        setOfLinksToSkip.add("/h2-console/");
        setOfLinksToSkip.add("/account/api/auth/register");
        setOfLinksToSkip.add("/account/api/authenticate");

        if(setOfLinksToSkip.contains(servletPath) ) {
            logger.info("The endpoint: " + servletPath + " was skipped.");
            return true;
        }
        return false;
    }
}
