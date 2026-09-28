package com.store.organization;

import com.store.common.exception.BusinessException;
import com.store.common.exception.ResourceNotFoundException;
import com.store.user.User;
import com.store.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
public class MembershipServiceImpl implements MembershipService {

    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;

    public MembershipServiceImpl(MembershipRepository membershipRepository,
                                 UserRepository userRepository,
                                 OrganizationRepository organizationRepository) {
        this.membershipRepository = membershipRepository;
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
    }

    @Override
    public Membership addMember(Long userId, Long organizationId) {
        if (membershipRepository.existsByUserIdAndOrganizationId(userId, organizationId)) {
            throw new BusinessException("User is already a member of this organization");
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Organization org = organizationRepository.findById(organizationId)
            .orElseThrow(() -> new ResourceNotFoundException("Organization", organizationId));

        Membership m = new Membership();
        m.setUser(user);
        m.setOrganization(org);
        m.setState(MembershipStateEnum.ACTIVE);
        m.setJoinedAt(Instant.now());

        return membershipRepository.save(m);
    }

    @Override
    public Membership suspendMember(Long userId, Long organizationId) {
        Membership m = getMembership(userId, organizationId);
        m.setState(MembershipStateEnum.SUSPENDED);
        return membershipRepository.save(m);
    }

    @Override
    public Membership reactivateMember(Long userId, Long organizationId) {
        Membership m = getMembership(userId, organizationId);
        m.setState(MembershipStateEnum.ACTIVE);
        m.setLeftAt(null);
        return membershipRepository.save(m);
    }

    @Override
    public void removeMember(Long userId, Long organizationId) {
        Membership m = getMembership(userId, organizationId);
        m.setState(MembershipStateEnum.REMOVED);
        m.setLeftAt(Instant.now());
        membershipRepository.save(m);
    }

    @Override
    @Transactional(readOnly = true)
    public Membership getMembership(Long userId, Long organizationId) {
        return membershipRepository.findByUserIdAndOrganizationId(userId, organizationId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Membership not found for user=" + userId + ", org=" + organizationId));
    }
    
}