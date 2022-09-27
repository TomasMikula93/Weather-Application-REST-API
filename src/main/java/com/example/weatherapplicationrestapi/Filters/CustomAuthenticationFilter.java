package com.example.weatherapplicationrestapi.Filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    //firstly attempt to authenticate player
    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username, password;
        var requestMap = new ObjectMapper().readValue(request.getInputStream(), Map.class); //when using JSON format, Spring Boot will use an ObjectMapper instance to serialize responses and deserialize requests
        username = requestMap.get("username").toString();
        password = requestMap.get("password").toString();

        try {
            if (password.isBlank() || username.isBlank()  || (password.length() < 8)) {
                throw new RuntimeException("Field username and/or field password was empty!");

            }
        } catch (RuntimeException exception) {
            log.info("Field username and/or field password was empty!");
            response.setHeader("error", exception.getMessage());
            response.setStatus(BAD_REQUEST.value());
            Map<String, String> error = new HashMap<>();
            error.put("error", exception.getMessage());
            response.setContentType(APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(), error);
        }
        log.info("Username is: {}", username); log.info("Password is: {}", password);

        // authentication will be processed internally in the Spring Security and
        // result in two possible scenarios: successfulAuthentication or unsuccessfulAuthentication
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManager.authenticate(authenticationToken);
    }

    //then handle successfulAuthentication or unsuccessfulAuthentication
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        //org.springframework.security.core.userdetails.User
        User user = (User) authResult.getPrincipal(); //  In the case of an authentication request with username and password, this would be the username
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
        log.info("Successful login, username: {}, password: {}", user.getUsername(), user.getPassword());


        String token = JWT.create()
                .withSubject(user.getUsername())
                .withIssuer(request.getRequestURL().toString())
                .withClaim(
                        "task",
                        user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);
        Map<String, String> access_token = new HashMap<>();
        access_token.put("status", "ok");
        access_token.put("token", token);
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), access_token);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info(failed.getMessage());
        response.setHeader("error", failed.getMessage());
        response.setStatus(UNAUTHORIZED.value());
        Map<String, String> error = new HashMap<>();
        error.put("error", "Account isn't verified by email or username/password was incorrect!");
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), error);
    }
}