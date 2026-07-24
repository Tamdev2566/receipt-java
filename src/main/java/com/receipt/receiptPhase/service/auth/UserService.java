package com.receipt.receiptPhase.service.auth;

import com.receipt.receiptPhase.dto.auth.UserDTO;
import com.receipt.receiptPhase.repository.auth.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<UserDTO> getActiveUsers(String search) {
        List<UserDTO> activeUsers = userRepository.findActiveUserDetails();

        String searchTerm = (search == null || search.equals("*")) ? "" : search.trim().toLowerCase();

        if (searchTerm.isEmpty()) {
            return activeUsers;
        }

        return activeUsers.stream()
                .filter(user ->
                        (user.getUserId() != null && user.getUserId().toLowerCase().contains(searchTerm)) ||
                                (user.getUserName() != null && user.getUserName().toLowerCase().contains(searchTerm)) ||
                                (user.getFullName() != null && user.getFullName().toLowerCase().contains(searchTerm)))
                .collect(Collectors.toList());
    }
}