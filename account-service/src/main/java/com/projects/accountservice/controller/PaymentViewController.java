package com.projects.accountservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
public class PaymentViewController {

    @GetMapping("/payment-success")
    public String paymentSuccess(
            @RequestParam(value = "session_id", required = false) String sessionId,
            Model model) {
        log.info("Payment success page loaded for session: {}", sessionId);
        model.addAttribute("sessionId", sessionId);
        return "payment-success"; // resolves to templates/payment-success.html
    }

    @GetMapping("/payment-cancel")
    public String paymentCancel(Model model) {
        log.info("Payment cancel page loaded");
        return "payment-cancel"; // resolves to templates/payment-cancel.html
    }
}
