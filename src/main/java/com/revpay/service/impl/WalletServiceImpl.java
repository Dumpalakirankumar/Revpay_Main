package com.revpay.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.revpay.entity.User;
import com.revpay.entity.Wallet;
import com.revpay.repository.WalletRepository;
import com.revpay.security.CustomUserDetails;
import com.revpay.service.interfaces.UserService;
import com.revpay.service.interfaces.WalletService;

@Service
public class WalletServiceImpl implements WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Override
    public void createWalletForUser(User user) {

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(0.0);
        wallet.setWalletStatus("ACTIVE");
        wallet.setUpdatedAt(LocalDateTime.now());

        walletRepository.save(wallet);
    }

    @Override
    public Wallet getCurrentUserWallet() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

        return walletRepository.findByUser(userDetails.getUser())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
    }
    
    @Autowired
    private UserService userService;

    @Override
    public Wallet getWalletOfCurrentUser() {

        User user = userService.getCurrentUser();

        return walletRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
    }
}
