package com.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.model.User;
import com.user.model.UserTest;
import com.user.request.LoginRequest;
import com.user.security.JwtUtils;
import com.user.service.JwtUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    final String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0MSIsImV4cCI6MTYzMDQzNzkwNiwiaWF0IjoxNjMwNDAxOTA2fQ.gd0Hh6J-KYN9rVqBD9oRMx2AEK8HiTF5LxXOsFn6e3Q";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUserDetailsService jwtUserDetailsService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtils jwtUtils;

    private static ObjectMapper mapper = new ObjectMapper();

    @Test
    public void authenticateUserTest() throws Exception {
        User user = UserTest.user();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(user.getUsername());
        loginRequest.setPassword(user.getPassword());

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        Mockito.when(jwtUtils.generateToken(loginRequest.getUsername())).thenReturn(token);

        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(token));
    }

    @Test
    public void authenticateUserTest_UserOrPassIncorrect() throws Exception {
        User user = UserTest.user();
        user.setPassword("test");

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(user.getUsername());
        loginRequest.setPassword(user.getPassword());

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
        Mockito.when(authenticationManager.authenticate(authentication)).thenThrow(BadCredentialsException.class);

        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errorcode").value("001"))
                .andExpect(jsonPath("$.message").value("Incorrect username or password."));
    }

    @Test
    public void registerUserTest_registerFail_usernameIsNull() throws Exception {
        User user = UserTest.user();
        user.setUsername(null);

        mockMvc.perform(post("/api/registerUser")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errorcode").value("002"))
                .andExpect(jsonPath("$.message").value("Username is required."));
    }

    @Test
    public void registerUserTest_registerFail_usernameAtLeast4() throws Exception {
        User user = UserTest.user();
        user.setUsername("t");

        mockMvc.perform(post("/api/registerUser")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errorcode").value("003"))
                .andExpect(jsonPath("$.message").value("Username must be a-zA-Z0-9 and have 4-8 characters long."));
    }

    @Test
    public void registerUserTest_registerFail_passwordIsNull() throws Exception {
        User user = UserTest.user();
        user.setPassword(null);

        mockMvc.perform(post("/api/registerUser")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errorcode").value("004"))
                .andExpect(jsonPath("$.message").value("Password is required."));
    }

    @Test
    public void registerUserTest_registerFail_passwordAtLeast4() throws Exception {
        User user = UserTest.user();
        user.setPassword("t");

        mockMvc.perform(post("/api/registerUser")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errorcode").value("005"))
                .andExpect(jsonPath("$.message").value("Password must be a-zA-Z0-9 and have 4-8 characters long."));
    }

    @Test
    public void registerUserTest_registerFail_phoneIsNull() throws Exception {
        User user = UserTest.user();
        user.setPhone(null);

        mockMvc.perform(post("/api/registerUser")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errorcode").value("006"))
                .andExpect(jsonPath("$.message").value("Phone is required."));
    }

    @Test
    public void registerUserTest_registerFail_phoneIsNot10Digits() throws Exception {
        User user = UserTest.user();
        user.setPhone("08499911");

        mockMvc.perform(post("/api/registerUser")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errorcode").value("007"))
                .andExpect(jsonPath("$.message").value("Phone must be number 10 digits."));
    }

    @Test
    public void registerUserTest_registerFail() throws Exception {
        User user = UserTest.userSalary10000();

        mockMvc.perform(post("/api/registerUser")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errorcode").value("008"))
                .andExpect(jsonPath("$.message").value("Salary less than 15000 can not register."));
    }

    @Test
    public void registerUserTest_registerSilver() throws Exception {
        User user = UserTest.userSalary15000();
        Mockito.when(Mockito.mock(UserController.class).getMemberType(user.getSalary())).thenReturn("Silver");

        mockMvc.perform(post("/api/registerUser")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errorcode").value("000"))
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }

    @Test
    public void registerUserTest_registerGold() throws Exception {
        User user = UserTest.userSalary35000();
        Mockito.when(Mockito.mock(UserController.class).getMemberType(user.getSalary())).thenReturn("Gold");

        mockMvc.perform(post("/api/registerUser")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errorcode").value("000"))
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }

    @Test
    public void registerUserTest_registerPlatinum() throws Exception {
        User user = UserTest.userSalary55000();
        Mockito.when(Mockito.mock(UserController.class).getMemberType(user.getSalary())).thenReturn("Platinum");

        mockMvc.perform(post("/api/registerUser")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errorcode").value("000"))
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }

    @Test
    public void getUserInfoTest_loadUserNotFound() throws Exception {
        User user = UserTest.userInfo();
        String username = user.getUsername();

        org.springframework.security.core.userdetails.User userDetails = new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), new ArrayList<>());

        Mockito.when(jwtUtils.extractUsername(token)).thenReturn(username);
        Mockito.when(jwtUserDetailsService.loadUserByUsername(username)).thenThrow(UsernameNotFoundException.class);

        mockMvc.perform(get("/api/getUserInfo?username="+username)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer "+token))
                .andExpect(status().isForbidden());
    }

    @Test
    public void getUserInfoTest_tokenHasExpired() throws Exception {
        User user = UserTest.userInfo();
        String username = user.getUsername();
        Mockito.when(jwtUtils.extractUsername(token)).thenThrow(ExpiredJwtException.class);

        mockMvc.perform(get("/api/getUserInfo?username="+username)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer "+token))
                .andExpect(status().isForbidden());
    }

    @Test
    public void getUserInfoTest() throws Exception {
        User user = UserTest.userInfo();
        String username = user.getUsername();

        org.springframework.security.core.userdetails.User userDetails = new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), new ArrayList<>());

        Mockito.when(jwtUtils.extractUsername(token)).thenReturn(username);
        Mockito.when(jwtUserDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        Mockito.when(jwtUtils.validateToken(token, userDetails)).thenReturn(true);
        Mockito.when(jwtUserDetailsService.findByUsername(username)).thenReturn(user);

        mockMvc.perform(get("/api/getUserInfo?username="+username)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer "+token))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo(mapper.writeValueAsString(user))));
    }
}
