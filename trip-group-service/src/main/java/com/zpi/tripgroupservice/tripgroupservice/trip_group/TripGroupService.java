package com.zpi.tripgroupservice.tripgroupservice.trip_group;

import com.zpi.tripgroupservice.tripgroupservice.exception.ApiRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.zpi.tripgroupservice.tripgroupservice.exception.ExceptionInfo.INVALID_USER_ID;
import static com.zpi.tripgroupservice.tripgroupservice.exception.ExceptionInfo.NO_GROUPS_FOR_USER;

@Service
@RequiredArgsConstructor
public class TripGroupService {

    private final TripGroupRepository tripGroupRepository;


    public List<TripGroup> getAllGroupsForUser(Long userId){
        if(userId == null){
            throw new IllegalArgumentException(INVALID_USER_ID);
        }
        return tripGroupRepository.findAllGroupsForUser(userId).orElseThrow(() -> new ApiRequestException(NO_GROUPS_FOR_USER));
    }

}
