package com.revpay.controller.wallet;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revpay.entity.Wallet;
import com.revpay.service.interfaces.WalletService;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @GetMapping("/balance")
    public ResponseEntity<?> getBalance() {

        Wallet wallet = walletService.getWalletOfCurrentUser();

        return ResponseEntity.ok(
                Map.of(
                        "balance", wallet.getBalance(),
                        "status", wallet.getWalletStatus()
                )
        );
    }
}
