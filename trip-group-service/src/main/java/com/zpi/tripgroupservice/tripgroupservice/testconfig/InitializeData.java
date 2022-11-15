package com.zpi.tripgroupservice.tripgroupservice.testconfig;

import com.zpi.tripgroupservice.tripgroupservice.commons.Currency;
import com.zpi.tripgroupservice.tripgroupservice.commons.Role;
import com.zpi.tripgroupservice.tripgroupservice.invitation.Invitation;
import com.zpi.tripgroupservice.tripgroupservice.invitation.InvitationRepository;
import com.zpi.tripgroupservice.tripgroupservice.trip_group.TripGroup;
import com.zpi.tripgroupservice.tripgroupservice.trip_group.TripGroupRepository;
import com.zpi.tripgroupservice.tripgroupservice.user_group.UserGroup;
import com.zpi.tripgroupservice.tripgroupservice.user_group.UserGroupKey;
import com.zpi.tripgroupservice.tripgroupservice.user_group.UserGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InitializeData {
    @Value("${spring.profiles.active}")
    private String profile;

    private final TripGroupRepository tripGroupRepository;
    private final UserGroupRepository userGroupRepository;
    private final InvitationRepository invitationRepository;

    @PostConstruct
    public void addTripGroupsAndUserGroupsAndInvitations() {
        if (!profile.equals("test"))
            return;

        var tripGroups = List.of(
                new TripGroup("Test1", Currency.PLN, "Opis", 2, "Barcelona", "Barcelona", 3, 4),
                new TripGroup("Test2", Currency.PLN, "Opis2", 3, "Madryt", "Madryt", 3, 5),
                new TripGroup("Test3", Currency.USD, "Opis3", 4, "Wroclaw", "Wroclaw", 2, 5),
                new TripGroup("Test4", Currency.PLN, "Opis4", 5, "Huelva", "Huelva", 3, 4),
                new TripGroup("Test5", Currency.PLN, "Opis5", 6, "Pisa", "Pisa", 4, 6),
                new TripGroup("Finance Optimizer", Currency.PLN, "Grupa testujaca optymalizacje", 6, "Pisa", "Pisa", 2, 5)
        );
        tripGroupRepository.saveAll(tripGroups);

        userGroupRepository.saveAll(List.of(
                new UserGroup(new UserGroupKey(1L, tripGroups.get(0).getGroupId()), Role.COORDINATOR, 1),
                new UserGroup(new UserGroupKey(1L, tripGroups.get(1).getGroupId()), Role.COORDINATOR, 1),
                new UserGroup(new UserGroupKey(1L, tripGroups.get(2).getGroupId()), Role.COORDINATOR, 1),
                new UserGroup(new UserGroupKey(1L, tripGroups.get(3).getGroupId()), Role.COORDINATOR, 1),
                new UserGroup(new UserGroupKey(2L, tripGroups.get(4).getGroupId()), Role.COORDINATOR, 1),
                new UserGroup(new UserGroupKey(2L, tripGroups.get(1).getGroupId()), Role.PARTICIPANT, 1),
                new UserGroup(new UserGroupKey(1L, tripGroups.get(5).getGroupId()), Role.COORDINATOR, 1),
                new UserGroup(new UserGroupKey(2L, tripGroups.get(5).getGroupId()), Role.PARTICIPANT, 1),
                new UserGroup(new UserGroupKey(3L, tripGroups.get(5).getGroupId()), Role.PARTICIPANT, 1),
                new UserGroup(new UserGroupKey(4L, tripGroups.get(5).getGroupId()), Role.PARTICIPANT, 1)
        ));

        var invitations = List.of(new Invitation("tripGroup1", tripGroups.get(0)),
                new Invitation("tripGroup2", tripGroups.get(1)),
                new Invitation("tripGroup3", tripGroups.get(2)),
                new Invitation("tripGroup4", tripGroups.get(3)),
                new Invitation("tripGroup5", tripGroups.get(4)),
                new Invitation("tripGroup6", tripGroups.get(5))
        );

        invitationRepository.saveAll(invitations);
    }
}
