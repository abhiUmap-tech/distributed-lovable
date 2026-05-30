package com.projects.intelligenceservice.services.impl;


import com.projects.commonlib.security.AuthUtil;
import com.projects.intelligenceservice.client.AccountClient;
import com.projects.intelligenceservice.entity.UsageLog;
import com.projects.intelligenceservice.repository.UsageLogRepository;
import com.projects.intelligenceservice.services.UsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UsageServiceImpl implements UsageService {

    private final UsageLogRepository usageLogRepository;
    private final AuthUtil authUtil;
    private final AccountClient accountClient;

// Create something : 5000 1 FEB 2026
// Add dark theme: 5000 + 2000 = 7000
    @Override
    public void recordTokenUsage(Long userId, int actualTokens) {
        var today = LocalDate.now();
        var todayLog = usageLogRepository.findByUserIdAndDate(userId, today)
                .orElseGet(() -> createNewDailyLog(userId, today));

        todayLog.setTokensUsed(todayLog.getTokensUsed() + actualTokens);
        usageLogRepository.save(todayLog);
    }


    @Override
    public void checkDailyTokensUsage() {
        var userId = authUtil.getCurrentUserId();
        var plan = accountClient.getCurrentSubscribedPlanByUser();

        var today = LocalDate.now();
        var todayLog = usageLogRepository.findByUserIdAndDate(userId, today)
                .orElseGet(() -> createNewDailyLog(userId, today));

        if (plan.unlimitedAi()) return;

        var currentUsage = todayLog.getTokensUsed();
        var limit = plan.maxProjects();

        if (currentUsage >= limit)
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Daily limit reached, Upgrade now");


    }

    private UsageLog createNewDailyLog(Long userId, LocalDate date) {
        UsageLog newLog = UsageLog.builder()
                .userId(userId)
                .date(date)
                .tokensUsed(0)
                .build();
        return usageLogRepository.save(newLog);
    }
}
