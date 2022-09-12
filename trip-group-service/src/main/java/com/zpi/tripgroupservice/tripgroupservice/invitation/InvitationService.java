package com.zpi.tripgroupservice.tripgroupservice.invitation;

import com.zpi.tripgroupservice.tripgroupservice.commons.Role;
import com.zpi.tripgroupservice.tripgroupservice.trip_group.TripGroup;
import com.zpi.tripgroupservice.tripgroupservice.user_group.UserGroup;
import com.zpi.tripgroupservice.tripgroupservice.user_group.UserGroupKey;
import com.zpi.tripgroupservice.tripgroupservice.user_group.UserGroupRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;

import javax.transaction.Transactional;
import java.nio.ByteBuffer;
import java.security.SecureRandom;

import static com.zpi.tripgroupservice.tripgroupservice.exception.ExceptionInfo.*;

@RequiredArgsConstructor
public class InvitationService {
    private final InvitationRepository invitationRepository;
    private final UserGroupRepository userGroupRepository;
    private final TripGroupService tripGroupService;

    @Transactional
    public String createInvitation(Long userId, Long groupId) {
        if (userId < 0 || groupId < 0) {
            throw new IllegalArgumentException(NEGATIVE_USER_ID_GROUP_ID);
        }

        TripGroup tripGroup = tripGroupService.getTripGroupById(groupId);

        var key = new UserGroupKey(userId, groupId);
        var userGroup = userGroupRepository.findById(key)
                                           .orElseThrow(() -> new IllegalArgumentException(USER_NOT_A_MEMBER));

        if (userGroup.getRole() != Role.COORDINATOR)
            throw new IllegalArgumentException(USER_NOT_A_COORDINATOR);


        var invitationToken = generateInvitationToken(tripGroup);

        var invitation = new Invitation(invitationToken, tripGroup);
        invitationRepository.save(invitation);

        return getInvitationLink(invitationToken);
    }

    private String generateInvitationToken(TripGroup tripGroup) {
        var salt = getRandomNonce(16);

        var valueToHash = tripGroup.getName() + tripGroup.getGroupId() + tripGroup.getDescription();
        var valueToHashBytes = valueToHash.getBytes();

        var input = ByteBuffer.allocate(salt.length + valueToHashBytes.length)
                              .put(salt)
                              .put(valueToHashBytes)
                              .array();

        return new DigestUtils("SHA3-256").digestAsHex(input);
    }

    private byte[] getRandomNonce(int numBytes) {
        byte[] nonce = new byte[numBytes];
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }

    private String getInvitationLink(String invitationToken) {
        return "http://localhost:8080/api/v1/invitation/?token=" + invitationToken;
    }

    @Transactional
    public UserGroup acceptInvitation(Long userId, String token) {
        var groupID = invitationRepository.findById(token)
                                          .orElseThrow(() -> new IllegalArgumentException("Invalid invitation token"))
                                          .getTripGroup()
                                          .getGroupId();

        var key = new UserGroupKey(userId, groupID);
        var userGroup = new UserGroup(key, Role.PARTICIPANT);
        return userGroupRepository.save(userGroup);
    }
}
