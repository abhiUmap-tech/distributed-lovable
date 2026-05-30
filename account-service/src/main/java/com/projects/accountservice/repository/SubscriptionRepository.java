package com.projects.accountservice.repository;


import com.projects.accountservice.entity.UserSubscription;
import com.projects.commonlib.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface SubscriptionRepository  extends JpaRepository<UserSubscription, Long> {
    Optional<UserSubscription> findByIdAndSubscriptionStatusIn(Long user_id, Set<SubscriptionStatus> subscriptionStatusSet);  // ← Updated

    boolean existsByStripeSubscriptionId(String subscriptionId);


    Optional<UserSubscription> findByStripeSubscriptionId(String gatewaySubscriptionId);
}
