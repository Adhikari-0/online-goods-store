// profile/DeliveryAgentServiceImpl.java
package com.store.profile;

import com.store.common.exception.ResourceNotFoundException;
import com.store.profile.dto.DeliveryAgentProfileRequest;
import com.store.user.User;
import com.store.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
public class DeliveryAgentServiceImpl implements DeliveryAgentService {

    private final DeliveryAgentProfileRepository agentRepo;
    private final UserRepository userRepository;

    public DeliveryAgentServiceImpl(DeliveryAgentProfileRepository agentRepo,
                                    UserRepository userRepository) {
        this.agentRepo = agentRepo;
        this.userRepository = userRepository;
    }

    @Override
    public DeliveryAgentProfile createOrUpdate(Long userId, DeliveryAgentProfileRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        DeliveryAgentProfile profile = agentRepo.findById(userId)
            .orElseGet(() -> {
                DeliveryAgentProfile p = new DeliveryAgentProfile();
                p.setUser(user);
                p.setStatus(AgentStatusEnum.OFFLINE);
                return p;
            });

        profile.setLicenseNumber(request.licenseNumber());
        profile.setVehicleType(request.vehicleType());
        profile.setVehicleNumber(request.vehicleNumber());

        return agentRepo.save(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryAgentProfile getByUserId(Long userId) {
        return agentRepo.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Delivery agent profile not found for user: " + userId));
    }

    @Override
    public DeliveryAgentProfile updateLocation(Long userId, Double lat, Double lng) {
        DeliveryAgentProfile profile = getByUserId(userId);
        profile.setCurrentLatitude(lat);
        profile.setCurrentLongitude(lng);
        profile.setLastLocationAt(Instant.now());
        return agentRepo.save(profile);
    }

    @Override
    public DeliveryAgentProfile updateStatus(Long userId, AgentStatusEnum status) {
        DeliveryAgentProfile profile = getByUserId(userId);
        profile.setStatus(status);
        return agentRepo.save(profile);
    }

    @Override
    public DeliveryAgentProfile setAvailability(Long userId, boolean available) {
        DeliveryAgentProfile profile = getByUserId(userId);
        profile.setAvailable(available);
        return agentRepo.save(profile);
    }
}