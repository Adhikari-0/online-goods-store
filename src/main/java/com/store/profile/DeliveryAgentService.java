// profile/DeliveryAgentService.java
package com.store.profile;

import com.store.profile.dto.DeliveryAgentProfileRequest;

public interface DeliveryAgentService {

    DeliveryAgentProfile createOrUpdate(Long userId, DeliveryAgentProfileRequest request);

    DeliveryAgentProfile getByUserId(Long userId);

    DeliveryAgentProfile updateLocation(Long userId, Double lat, Double lng);

    DeliveryAgentProfile updateStatus(Long userId, AgentStatusEnum status);

    DeliveryAgentProfile setAvailability(Long userId, boolean available);
}