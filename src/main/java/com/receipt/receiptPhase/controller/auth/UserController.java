package com.receipt.receiptPhase.controller.auth;

import com.receipt.receiptPhase.dto.auth.UserDTO;
import com.receipt.receiptPhase.service.auth.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/getUsers")
    public ResponseEntity<Map<String, Object>> getActiveUsers(
            @RequestParam(value = "search", required = false, defaultValue = "") String search) {

        List<UserDTO> userList = userService.getActiveUsers(search);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("data", userList);

        return ResponseEntity.ok(response);
    }
}