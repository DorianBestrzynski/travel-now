package com.zpi.tripgroupservice.tripgroupservice.trip_group;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zpi.tripgroupservice.tripgroupservice.commons.GroupStage;
import com.zpi.tripgroupservice.tripgroupservice.dto.TripGroupDto;
import com.zpi.tripgroupservice.tripgroupservice.exception.ApiPermissionException;
import com.zpi.tripgroupservice.tripgroupservice.exception.ApiRequestException;
import com.zpi.tripgroupservice.tripgroupservice.user_group.UserGroupService;
import lombok.RequiredArgsConstructor;
import org.dozer.DozerBeanMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static com.zpi.tripgroupservice.tripgroupservice.exception.ExceptionInfo.INVALID_USER_ID;
import static com.zpi.tripgroupservice.tripgroupservice.exception.ExceptionInfo.NO_GROUPS_FOR_USER;

@Service
@RequiredArgsConstructor
public class TripGroupService {

    private final TripGroupRepository tripGroupRepository;

    private final UserGroupService userGroupService;


    public List<TripGroup> getAllGroupsForUser(Long userId){
        if(userId == null){
            throw new IllegalArgumentException(INVALID_USER_ID);
        }
        return tripGroupRepository.findAllGroupsForUser(userId).orElseThrow(() -> new ApiRequestException(NO_GROUPS_FOR_USER));
    }

    @Transactional
    public TripGroup createGroup(Long userId, TripGroupDto groupDto) {
            var tripGroup = new TripGroup(groupDto.name(),groupDto.currency(),groupDto.description(), groupDto.votesLimit(), groupDto.startLocation());
            tripGroupRepository.save(tripGroup);
            userGroupService.createUserGroup(userId, tripGroup.getGroupId(), groupDto.votesLimit());
            return tripGroup;


    }
    @Transactional
    public void deleteGroup(Long groupId, Long userId) {
        if(groupId == null || userId == null){
            throw new IllegalArgumentException(INVALID_GROUP_ID + INVALID_USER_ID);
        }
        if(userGroupService.isUserCoordinator(userId, groupId)){
            tripGroupRepository.deleteById(groupId);
            userGroupService.deletionGroupCleanUp(groupId);
        }
        else throw new ApiPermissionException(DELETING_PERMISSION_VIOLATION);

    }


   @Transactional
    public TripGroup updateGroup(Long groupId, Long userId, TripGroupDto tripGroupDto) {
        if(userGroupService.isUserCoordinator(userId, groupId)) {
            var tripGroup = tripGroupRepository.findById(groupId).orElseThrow(() -> new ApiRequestException(GROUP_NOT_FOUND));
            // TODO Znalezc jakis sensowny mapper do tego
            if (tripGroupDto.name() != null) {
                tripGroup.setName(tripGroupDto.name());
            }
            if (tripGroupDto.currency() != null) {
                tripGroup.setCurrency(tripGroupDto.currency());
            }

            if (tripGroupDto.description() != null) {
                tripGroup.setDescription(tripGroupDto.description());
            }

            if (tripGroupDto.votesLimit() != null) {
                tripGroup.setVotesLimit(tripGroupDto.votesLimit());
            }

            if (tripGroupDto.startLocation() != null) {
                tripGroup.setStartLocation(tripGroupDto.startLocation());
            }
            tripGroupRepository.save(tripGroup);

            return tripGroup;
        }

        else throw new ApiPermissionException(EDITING_PERMISSION_VIOLATION);


    }
}
