package com.receipt.receiptPhase.service.auth;

import com.receipt.receiptPhase.dto.auth.UserDTO;
import com.receipt.receiptPhase.model.auth.UserModal;
import com.receipt.receiptPhase.repository.auth.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<UserDTO> getActiveUsers() {
        List<UserModal> users = userRepository.findAll();

        // Filter: Only users with isValid == "Y" (Case-insensitive check)
        return users.stream()
                .filter(user -> "Y".equalsIgnoreCase(user.getIsValid()))
                .map(user -> new UserDTO(
                        user.getUserId(),
                        user.getEmail(),
                        user.getName(),
                        user.getIsValid()
                ))
                .collect(Collectors.toList());
    }
}