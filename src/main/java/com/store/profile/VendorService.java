// profile/VendorService.java
package com.store.profile;

import com.store.profile.dto.VendorProfileRequest;

public interface VendorService {

    VendorProfile createOrUpdate(Long userId, VendorProfileRequest request);

    VendorProfile getByUserId(Long userId);

    VendorProfile updateKycStatus(Long userId, KycStatusEnum status);
}