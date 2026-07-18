    package com.receipt.receiptPhase.controller;

    import com.receipt.receiptPhase.service.UpdateChequeService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.util.Map;

    @RestController
    @RequestMapping("/api/cheque")
    public class        UpdateChequeController {
        @Autowired
        private UpdateChequeService chequeService;

        @GetMapping("/search")
        public ResponseEntity<?> getReceiptByCheque(@RequestParam String chequeNo) {
            Map<String, Object> result = chequeService.findByChequeNo(chequeNo);

            if (result == null || result.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "No records found"));
            }
            return ResponseEntity.ok(result);
        }

        @PostMapping("/update")
        public ResponseEntity<Map<String, String>> updateCheque(@RequestBody Map<String, String> payload) {
            try {
                chequeService.updateChequeNo(
                        payload.get("originalChequeNo"),
                        payload.get("newChequeNo"),
                        payload.get("transactionNo"),
                        payload.get("remark"),
                        payload.get("userId")
                );
                return ResponseEntity.ok(Map.of("message", "Cheque Number Updated Successfully"));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message", "Update Failed: " + e.getMessage()));
            }
        }
    }
