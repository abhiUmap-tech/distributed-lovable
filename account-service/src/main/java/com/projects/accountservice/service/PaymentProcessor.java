package com.projects.accountservice.service;
import com.projects.accountservice.dto.subscription.CheckoutRequest;
import com.projects.accountservice.dto.subscription.CheckoutResponse;
import com.projects.accountservice.dto.subscription.PortalResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.StripeObject;

import java.util.Map;


public interface PaymentProcessor {

    CheckoutResponse createCheckoutSessionUrl(CheckoutRequest checkoutRequest) throws StripeException;

    PortalResponse openCustomerPortal();

    void handleWebhookEvent(String type, StripeObject stripeObject, Map<String, String> metaData);
}
