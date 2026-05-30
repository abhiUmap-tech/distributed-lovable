
package com.projects.accountservice.mapper;

import com.projects.accountservice.dto.subscription.SubscriptionResponse;
import com.projects.accountservice.entity.Plan;
import com.projects.accountservice.entity.UserSubscription;
import com.projects.commonlib.dto.PlanDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    SubscriptionResponse toSubscriptionResponse(UserSubscription userSubscription);

    PlanDto toPlanResponse(Plan plan);

}
