package com.projects.accountservice.dto.subscription;

import com.projects.commonlib.dto.PlanDto;

import java.time.Instant;

public record SubscriptionResponse (

        PlanDto plan,
        String status,
        Instant periodEnd,
        Long tokensUsedThisCycle

){
}
