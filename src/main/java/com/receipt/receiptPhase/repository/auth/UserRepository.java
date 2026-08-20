package com.receipt.receiptPhase.repository.auth;

import com.receipt.receiptPhase.dto.auth.UserDTO;
import com.receipt.receiptPhase.model.auth.UserModal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserModal, String> {
  @Query(
    "SELECT new com.receipt.receiptPhase.dto.auth.UserDTO(ud.userId, ud.userName, ud.fullName) " +
      "FROM UserModal u JOIN UserDetailsModal ud ON u.userId = ud.userId " +
      "WHERE UPPER(u.isValid) = 'Y'"
  )
  List<UserDTO> findActiveUserDetails();
}
