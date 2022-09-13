package com.zpi.tripgroupservice.tripgroupservice.trip_group;

import com.zpi.tripgroupservice.tripgroupservice.dto.TripGroupDto;
import com.zpi.tripgroupservice.tripgroupservice.exception.ApiRequestException;
import com.zpi.tripgroupservice.tripgroupservice.user_group.UserGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static com.zpi.tripgroupservice.tripgroupservice.exception.ExceptionInfo.*;

@Service
@RequiredArgsConstructor
public class TripGroupService {

    private final TripGroupRepository tripGroupRepository;

    private final UserGroupService userGroupService;


    public List<TripGroup> getAllGroupsForUser(Long userId){
        if(userId == null){
            throw new IllegalArgumentException(INVALID_USER_ID);
        }
        List<TripGroup> tripGroups = tripGroupRepository.findAllGroupsForUser(userId);
        if(tripGroups.isEmpty()) throw new ApiRequestException(NO_GROUPS_FOR_USER);
        return tripGroups;

    }

    @Transactional
    public TripGroup createGroup(TripGroupDto groupDto) {
            var tripGroup = new TripGroup(groupDto.name(),groupDto.currency(),groupDto.description(), groupDto.votesLimit(), groupDto.startLocation(), groupDto.groupStage());
            tripGroupRepository.save(tripGroup);
            userGroupService.createUserGroup(groupDto.creatorId(), tripGroup.getGroupId(), groupDto.votesLimit());
            return tripGroup;


    }
}
