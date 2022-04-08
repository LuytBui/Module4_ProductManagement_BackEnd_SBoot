package com.codegym.controller;

import com.codegym.model.auth.JwtResponse;
import com.codegym.model.auth.Role;
import com.codegym.model.auth.User;
import com.codegym.model.auth.UserRegisterForm;
import com.codegym.service.auth.JwtService;
import com.codegym.service.auth.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class AuthController {

    @Autowired
    IUserService userService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        // POST request với body chứa một User

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())); // tạo đối tượng Authentication
        SecurityContextHolder.getContext().setAuthentication(authentication);  // lưu đối tượng Authentication vào ContextHolder
        String jwt = jwtService.generateTokenLogin(authentication);  // tạo token từ đối tượng Authentication
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User currentUser = userService.findByUsername(user.getUsername()).get(); // lấy đối tượng User từ Database

        JwtResponse jwtResponse = new JwtResponse(currentUser.getId(), jwt, userDetails.getUsername(), userDetails.getAuthorities());
        return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegisterForm userRegisterForm){
        if (!userRegisterForm.isPasswordMatch())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        User user = new User(userRegisterForm.getUsername(), userRegisterForm.getPassword());
        List<Role> roles = new ArrayList<>();
        roles.add(new Role(2L, "ROLE_USER"));
        user.setRoles(roles);
        userService.save(user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }
}
