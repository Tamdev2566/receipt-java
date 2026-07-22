package com.receipt.receiptPhase.service;


import com.receipt.receiptPhase.dto.ChequeRequest;
import com.receipt.receiptPhase.model.ChequeReaderModel;
import com.receipt.receiptPhase.repository.ChequeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ChequeReaderService {

    @Autowired
    private ChequeRepository repository;

    public String processAndSaveCheque(ChequeRequest request) throws Exception {

        if (request.getFullChequeNo() == null || request.getFullChequeNo().trim().isEmpty()) {
            throw new IllegalArgumentException("Please Enter Full Cheque No.");
        }
        if (request.getChequeNo() == null || request.getChequeNo().trim().isEmpty()) {
            throw new IllegalArgumentException("Please Enter Cheque No.");
        }

        String fullCheque = request.getFullChequeNo().trim();
        String chequeNoInput = request.getChequeNo().trim();
        boolean autoRead = false;

        if (fullCheque.startsWith("T") && fullCheque.contains("-")) {
            String[] strArray = fullCheque.split("U");
            if (strArray.length > 2) {
                String strBankCode = strArray[2].trim().substring(0, 4);
                String scannedChequeNo = strArray[1];

                String bankName = repository.getBankNameByCode(strBankCode);
                if (bankName != null && !bankName.isEmpty()) {
                    chequeNoInput = bankName + " " + scannedChequeNo;
                    if (scannedChequeNo.length() == 6) {
                        autoRead = true;
                    }
                }
            }
        }

        if (!chequeNoInput.contains(" ")) {
            throw new IllegalArgumentException("Invalid Cheque Number!!");
        }

        String[] parts = chequeNoInput.split(" ");
        String bankNamePart = parts[0].trim();
        String numberPart = parts[1];


        boolean bankExists = repository.checkBankExistsByName(bankNamePart);
        if (!bankExists) {
            throw new IllegalArgumentException("Invalid Bank Code / Bank Name!!");
        }


        if (numberPart.length() != 6 || !numberPart.matches("^[0-9]+$") || !bankNamePart.matches("^[A-Za-z]+$")) {
            throw new IllegalArgumentException("Invalid Cheque Number format!!");
        }


        if (repository.checkDuplicateCheque(chequeNoInput, fullCheque)) {
            throw new RuntimeException("Duplicate Cheque No.");
        }


        String bound = "O";
        String userGroup = repository.getUserGroup(request.getUid());
        if ("INW".equals(userGroup)) {
            bound = "I";
        }
        if (request.getBoundOption() != null) {
            if ("I".equalsIgnoreCase(request.getBoundOption())) bound = "I";
            else if ("O".equalsIgnoreCase(request.getBoundOption())) bound = "O";
            else if ("IO".equalsIgnoreCase(request.getBoundOption())) bound = "IO";
        }


        String savedBankName = chequeNoInput.substring(0, chequeNoInput.indexOf(" "));


        ChequeReaderModel cheque = new ChequeReaderModel();
        cheque.setBound(bound);
        cheque.setChequeNo(chequeNoInput);
        cheque.setBankName(savedBankName);
        cheque.setScanUserId(request.getUid());
        cheque.setLastModified(LocalDateTime.now());
        cheque.setCreateTime(LocalDateTime.now()); // Can be mapped from UI if passed
        cheque.setAutoRead(autoRead);
        cheque.setFullChequeNo(fullCheque);

        repository.saveCheque(cheque);

        return "Cheque No : " + chequeNoInput + " are saved Successfully!";
    }
}