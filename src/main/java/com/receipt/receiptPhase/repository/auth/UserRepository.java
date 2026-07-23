package com.receipt.receiptPhase.repository.auth;

import com.receipt.receiptPhase.model.auth.UserModal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserModal, String> {
}