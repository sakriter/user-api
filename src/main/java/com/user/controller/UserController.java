package com.user.controller;

import com.user.model.User;
import com.user.request.LoginRequest;
import com.user.request.UserRequest;
import com.user.response.JwtResponse;
import com.user.response.MessageResponse;
import com.user.security.JwtUtils;
import com.user.service.JwtUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping(path = "/api")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping(path = "/auth")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) throws Exception {
        try {
            logger.info("username ---> "+loginRequest.getUsername());
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.ok(new MessageResponse("001", "Incorrect username or password."));
        }
        String jwt = jwtUtils.generateToken(loginRequest.getUsername());
        return ResponseEntity.ok(new JwtResponse(jwt));
    }

    @PostMapping(path = "/registerUser")
    public ResponseEntity<?> registerUser(@RequestBody UserRequest userRequest) {
        Pattern phonePattern = Pattern.compile("^\\d{10}$");
        Pattern strPattern = Pattern.compile("[a-zA-Z0-9]{4,8}$");

        if (userRequest.getUsername() == null || userRequest.getUsername().isEmpty()){
            return ResponseEntity.ok(new MessageResponse("002", "Username is required."));
        } else if (!strPattern.matcher(userRequest.getUsername()).matches()) {
            return ResponseEntity.ok(new MessageResponse("003", "Username must be a-zA-Z0-9 and have 4-8 characters long."));
        }

        if (userRequest.getPassword() == null || userRequest.getPassword().isEmpty()){
            return ResponseEntity.ok(new MessageResponse("004", "Password is required."));
        } else if (!strPattern.matcher(userRequest.getPassword()).matches()) {
            return ResponseEntity.ok(new MessageResponse("005", "Password must be a-zA-Z0-9 and have 4-8 characters long."));
        }

        if (userRequest.getPhone() == null || userRequest.getPhone().isEmpty()){
            return ResponseEntity.ok(new MessageResponse("006", "Phone is required."));
        } else if (!phonePattern.matcher(userRequest.getPhone()).matches()) {
            return ResponseEntity.ok(new MessageResponse("007", "Phone must be number 10 digits."));
        }

        String memberType = getMemberType(userRequest.getSalary());
        logger.info("memberType ---> "+memberType);
        if (memberType == null){
            return ResponseEntity.ok(new MessageResponse("008", "Salary less than 15000 can not register."));
        }

        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setPassword(userRequest.getPassword());
        user.setAddress(userRequest.getAddress());
        user.setPhone(userRequest.getPhone());
        user.setSalary(userRequest.getSalary());
        user.setMembertype(memberType);

        Date date = Calendar.getInstance().getTime();
        String phone4digit = userRequest.getPhone().substring(userRequest.getPhone().length() - 4);
        logger.info("date ---> "+date);

        user.setRefcode(getDateWithoutTimeUsingFormat(date) + phone4digit);
        user.setRegisterdate(new java.sql.Date(date.getTime()));
        jwtUserDetailsService.createUser(user);

        return ResponseEntity.ok(new MessageResponse("000", "User registered successfully!"));
    }

    @GetMapping(path = "/getUserInfo", params = "username")
    public ResponseEntity<?> getUserInfo(@RequestParam String username) {
        logger.info("getUserInfo ---> "+username);
        User user = jwtUserDetailsService.findByUsername(username);
        return ResponseEntity.ok(user);
    }

    public String getDateWithoutTimeUsingFormat(Date inputDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        return formatter.format(inputDate);
    }

    public String getMemberType(Integer salary) {
        if (salary < 15000) {
            return null;
        } else if (salary < 30000) {
            return "Silver";
        } else if (salary >= 30000 && salary <= 50000) {
            return "Gold";
        } else if (salary > 50000) {
            return "Platinum";
        }
        return null;
    }
}
