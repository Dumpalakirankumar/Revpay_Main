package com.revpay.service.impl;

import com.revpay.entity.Loan;
import com.revpay.entity.User;
import com.revpay.repository.LoanRepository;
import com.revpay.service.interfaces.LoanService;
import com.revpay.service.interfaces.NotificationService;
import com.revpay.service.interfaces.UserService;
import com.revpay.service.interfaces.WalletService;
import com.revpay.util.EmiCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LoanServiceImpl implements LoanService {

    @Autowired private LoanRepository loanRepository;
    @Autowired private UserService userService;
    @Autowired private WalletService walletService;
    @Autowired private NotificationService notificationService;

    private final double INTEREST = 12.0; // fixed interest

    @Override
    public void applyLoan(Double amount, int months) {

        User user = userService.getCurrentUser();

        Loan loan = new Loan();
        loan.setUser(user);
        loan.setPrincipalAmount(amount);
        loan.setInterestRate(INTEREST);
        loan.setTenureMonths(months);

        double emi = EmiCalculator.calculate(amount, INTEREST, months);

        loan.setEmiAmount(emi);
        loan.setRemainingAmount(emi * months);
        loan.setStatus("PENDING");
        loan.setCreatedAt(LocalDateTime.now());

        loanRepository.save(loan);
    }

    @Override
    public void approveLoan(Long loanId) {
    	User current = userService.getCurrentUser();

    	if (!current.getRole().getRoleName().equals("ADMIN"))
    	    throw new RuntimeException("Only admin can approve loans");

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        if (!loan.getStatus().equals("PENDING"))
            throw new RuntimeException("Loan already processed");

        loan.setStatus("APPROVED");
        loanRepository.save(loan);

        // ⭐ credit borrower wallet
        walletService.creditUser(
                loan.getUser(),
                loan.getPrincipalAmount(),
                "Loan credited"
           
        );
        
        notificationService.notify(loan.getUser(),
                "Loan Approved",
                "₹" + loan.getPrincipalAmount() + " credited to wallet");
    }

    @Override
    public void repayEmi(Long loanId) {

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        walletService.debitUser(
                loan.getUser(),
                loan.getEmiAmount(),
                "Loan EMI Payment"
        );

        loan.setRemainingAmount(
                loan.getRemainingAmount() - loan.getEmiAmount()
        );

        if (loan.getRemainingAmount() <= 0)
            loan.setStatus("CLOSED");

        loanRepository.save(loan);
        
        notificationService.notify(loan.getUser(),
                "EMI Paid",
                "EMI ₹" + loan.getEmiAmount() + " deducted");
    }

    @Override
    public List<Loan> myLoans() {
        return loanRepository.findByUser(userService.getCurrentUser());
    }
    
}