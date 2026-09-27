package com.store.profile;

import com.store.common.exception.ResourceNotFoundException;
import com.store.profile.dto.VendorProfileRequest;
import com.store.user.User;
import com.store.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VendorServiceImpl implements VendorService {

    private final VendorProfileRepository vendorProfileRepository;
    private final UserRepository userRepository;

    public VendorServiceImpl(VendorProfileRepository vendorProfileRepository,
                             UserRepository userRepository) {
        this.vendorProfileRepository = vendorProfileRepository;
        this.userRepository = userRepository;
    }

    @Override
    public VendorProfile createOrUpdate(Long userId, VendorProfileRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        VendorProfile profile = vendorProfileRepository.findById(userId)
            .orElseGet(() -> {
                VendorProfile vp = new VendorProfile();
                vp.setUser(user);
                vp.setKycStatus(KycStatusEnum.PENDING);
                return vp;
            });

        profile.setBusinessName(request.businessName());
        profile.setTaxId(request.taxId());
        profile.setCommissionPct(request.commissionPct());
        profile.setPayoutAccount(request.payoutAccount());

        return vendorProfileRepository.save(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public VendorProfile getByUserId(Long userId) {
        return vendorProfileRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Vendor profile not found for user: " + userId));
    }

    @Override
    public VendorProfile updateKycStatus(Long userId, KycStatusEnum status) {
        VendorProfile profile = getByUserId(userId);
        profile.setKycStatus(status);
        return vendorProfileRepository.save(profile);
    }
}