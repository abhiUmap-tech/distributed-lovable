package com.projects.accountservice.service.impl;

import com.projects.accountservice.dto.subscription.SubscriptionResponse;
import com.projects.accountservice.entity.Plan;
import com.projects.accountservice.entity.User;
import com.projects.accountservice.entity.UserSubscription;
import com.projects.accountservice.mapper.SubscriptionMapper;
import com.projects.accountservice.repository.PlanRepository;
import com.projects.accountservice.repository.SubscriptionRepository;
import com.projects.accountservice.repository.UserRepository;
import com.projects.accountservice.service.SubscriptionService;
import com.projects.commonlib.dto.PlanDto;
import com.projects.commonlib.enums.SubscriptionStatus;
import com.projects.commonlib.error.ResourceNotFoundException;
import com.projects.commonlib.security.AuthUtil;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SubscriptionServiceImpl implements SubscriptionService {

    AuthUtil authUtil;
    SubscriptionRepository subscriptionRepository;
    SubscriptionMapper subscriptionMapper;
    UserRepository userRepository;
    PlanRepository planRepository;


    @Override
    public SubscriptionResponse getCurrentSubscription() {
        var userId = authUtil.getCurrentUserId();

        var currentSubscription = subscriptionRepository.findByIdAndSubscriptionStatusIn(userId, Set.of(
                        SubscriptionStatus.ACTIVE, SubscriptionStatus.PAST_DUE,
                        SubscriptionStatus.TRAILING))
                .orElse((new UserSubscription()));

        return subscriptionMapper.toSubscriptionResponse(currentSubscription);
    }

    @Override
    public void activateSubscription(Long userId, Long planId, String subscriptionId, String customerId) {
        var exists = subscriptionRepository.existsByStripeSubscriptionId(subscriptionId);
        if (exists) return;

        var user = getUserById(userId);
        var plan = getPlan(planId);

        var subscription = UserSubscription.builder()
                .plan(plan)
                .user(user)
                .stripeSubscriptionId(subscriptionId)
                .subscriptionStatus(SubscriptionStatus.INCOMPLETE)
                .cancelAtPeriodEnd(false)
                .build();

        subscriptionRepository.save(subscription);
    }

    @Override
    @Transactional
    public void updateSubscription(String gatewaySubscriptionId, SubscriptionStatus status, Instant periodStart, Instant periodEnd, Boolean cancelAtPeriodEnd, Long planId) {
        var subscription = getSubscription(gatewaySubscriptionId);

        boolean hasSubscriptionUpdated = false;

        if (status != null && status!= subscription.getSubscriptionStatus()){
            subscription.setSubscriptionStatus(status);
            hasSubscriptionUpdated = true;
        }

        if(periodStart != null && periodStart.equals(subscription.getCurrentPeriodStart())){
            subscription.setCurrentPeriodStart(periodStart);
            hasSubscriptionUpdated = true;
        }

        if (periodEnd != null && periodEnd.equals(subscription.getCurrentPeriodEnd())){
            subscription.setCurrentPeriodEnd(periodEnd);
            hasSubscriptionUpdated = true;
        }

        if (cancelAtPeriodEnd != null && cancelAtPeriodEnd.equals(subscription.getCancelAtPeriodEnd())){
            subscription.setCancelAtPeriodEnd(cancelAtPeriodEnd);
            hasSubscriptionUpdated = true;
        }

        if (planId != null && !planId.equals(subscription.getPlan().getId())) {
            var newPlan = getPlan(planId);
            subscription.setPlan(newPlan);
        }

        if (hasSubscriptionUpdated){
            log.debug("Subscription has been updated: {}", gatewaySubscriptionId);
            subscriptionRepository.save(subscription);
        }

    }

    @Override
    public void cancelSubscription(String gatewaySubscriptionId) {
        var subscription = getSubscription(gatewaySubscriptionId);
        subscription.setSubscriptionStatus(SubscriptionStatus.CANCELLED);
        subscriptionRepository.save(subscription);

    }

    @Override
    public void renewSubscriptionPeriod(String gatewaySubscriptionId, Instant periodStart, Instant periodEnd) {
        var subscription = getSubscription(gatewaySubscriptionId);

        var newStart = periodStart != null ? periodEnd : subscription.getCurrentPeriodEnd();
        subscription.setCurrentPeriodStart(periodStart != null ? periodStart : subscription.getCurrentPeriodStart());
        subscription.setCurrentPeriodEnd(periodEnd != null ? periodEnd : subscription.getCurrentPeriodEnd());

        if (subscription.getSubscriptionStatus() == SubscriptionStatus.PAST_DUE || subscription.getSubscriptionStatus() == SubscriptionStatus.INCOMPLETE)
            subscription.setSubscriptionStatus(SubscriptionStatus.ACTIVE);

        subscriptionRepository.save(subscription);
    }


    @Override
    public void markSubscriptionPastDue(String gatewaySubscriptionId) {
        var subscription = getSubscription(gatewaySubscriptionId);

        if (subscription.getSubscriptionStatus() == SubscriptionStatus.PAST_DUE){
            log.debug("Subscription is already past due, gatewaySubscriptionId: {}", gatewaySubscriptionId);
            return;
        }

        subscription.setSubscriptionStatus(SubscriptionStatus.PAST_DUE);
        subscriptionRepository.save(subscription);

        //Notify user via email

    }

    @Override
    public PlanDto getCurrentSubscribedPlanByUser() {
        var userId = authUtil.getCurrentUserId();
        var subscriptionResponse = getCurrentSubscription();
        return subscriptionResponse.plan();
    }

    //Utility Methods

    private User getUserById(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));
    }

    private Plan getPlan(Long planId){
        return planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan", planId.toString()));
    }

    private UserSubscription getSubscription(String gatewaySubscriptionId) {
        return subscriptionRepository.findByStripeSubscriptionId(gatewaySubscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", gatewaySubscriptionId));
    }
}
