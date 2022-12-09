package com.zpi.tripgroupservice.invitation;

import com.zpi.tripgroupservice.aspects.AuthorizeCoordinator;
import com.zpi.tripgroupservice.commons.Role;
import com.zpi.tripgroupservice.exception.ExceptionInfo;
import com.zpi.tripgroupservice.trip_group.TripGroup;
import com.zpi.tripgroupservice.trip_group.TripGroupService;
import com.zpi.tripgroupservice.user_group.UserGroup;
import com.zpi.tripgroupservice.user_group.UserGroupKey;
import com.zpi.tripgroupservice.user_group.UserGroupService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.nio.ByteBuffer;
import java.security.SecureRandom;

import static com.zpi.tripgroupservice.commons.Utils.DEFAULT_VOTES_LIMIT;

@RequiredArgsConstructor
@Service
public class InvitationService {
    private final InvitationRepository invitationRepository;
    private final UserGroupService userGroupService;
    private final TripGroupService tripGroupService;
    @Value("${app.salt-length}")
    private int saltLength;

    @Value("${app.invitation-prefix}")
    private String invitationPrefix;

    @Transactional
    @AuthorizeCoordinator
    public String createInvitation(Long groupId) {
        if (groupId < 0) {
            throw new IllegalArgumentException(ExceptionInfo.INVALID_USER_ID_GROUP_ID);
        }

        TripGroup tripGroup = tripGroupService.getTripGroupById(groupId);

        var invitationToken = generateInvitationToken(tripGroup);

        var invitation = new Invitation(invitationToken, tripGroup);
        invitationRepository.save(invitation);

        return getInvitationLink(invitationToken);
    }

    private String generateInvitationToken(TripGroup tripGroup) {
        var salt = getRandomNonce(saltLength);

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
        return invitationPrefix + invitationToken;
    }

    @Transactional
    public UserGroup acceptInvitation(Long userId, String token) {
        var groupID = invitationRepository.findById(token)
                                          .orElseThrow(() -> new IllegalArgumentException(
                                                  ExceptionInfo.INVALID_INVITATION_TOKEN))
                                          .getTripGroup()
                                          .getGroupId();

        var key = new UserGroupKey(userId, groupID);
        if(userGroupService.exists(key))
            throw new IllegalArgumentException(ExceptionInfo.USER_ALREADY_MEMBER);

        return userGroupService.createUserGroup(key, Role.PARTICIPANT, DEFAULT_VOTES_LIMIT);
    }
}
