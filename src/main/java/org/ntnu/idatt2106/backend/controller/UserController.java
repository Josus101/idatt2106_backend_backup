package org.ntnu.idatt2106.backend.controller;
import org.ntnu.idatt2106.backend.dto.UserLoginDTO;
import org.ntnu.idatt2106.backend.dto.UserRegisterDTO;
import org.ntnu.idatt2106.backend.dto.UserTokenDTO;
import org.ntnu.idatt2106.backend.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
  @Autowired
  private LoginService loginService;

  @PostMapping("/register")
  public ResponseEntity<String> registerUser(
    @RequestBody UserRegisterDTO userRegister) {
    try {
      loginService.register(userRegister);
      return ResponseEntity.ok("User registered successfully");
    }
    catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user data");
    }
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(
      @RequestBody UserLoginDTO userLogin)
  {
    UserTokenDTO token;
    try {
      token = loginService.authenticate(userLogin.getEmail(), userLogin.getPassword());
    }
    catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid user data");
    }
    return ResponseEntity.ok(token);
  }
}

