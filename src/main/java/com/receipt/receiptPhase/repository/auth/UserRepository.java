package com.receipt.receiptPhase.repository.auth;

import com.receipt.receiptPhase.dto.auth.UserDTO;
import com.receipt.receiptPhase.model.auth.UserModal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserModal, String> {

    @Query("SELECT new com.receipt.receiptPhase.dto.auth.UserDTO(ud.userId, ud.userName, ud.fullName) " +
            "FROM UserModal u JOIN UserDetailsModal ud ON u.userId = ud.userId " +
            "WHERE UPPER(u.isValid) = 'Y'")
    List<UserDTO> findActiveUserDetails();
}