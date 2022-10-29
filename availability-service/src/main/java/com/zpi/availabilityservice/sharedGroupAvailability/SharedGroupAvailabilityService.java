package com.zpi.availabilityservice.sharedGroupAvailability;

import com.zpi.availabilityservice.availability.Availability;
import com.zpi.availabilityservice.availability.AvailabilityRepository;
import com.zpi.availabilityservice.proxies.TripGroupProxy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;


@Service
@RequiredArgsConstructor
public class SharedGroupAvailabilityService {

    private final SharedGroupAvailabilityRepository sharedGroupAvailabilityRepository;
    private Integer minimalNumberOfDays;
    private Integer minimalNumberOfParticipants;
    private final TripGroupProxy tripGroupProxy;
    private final AvailabilityRepository availabilityRepository;



    public void generateSharedGroupAvailability(Long groupId) {
        var getAvailabilityConstraints = tripGroupProxy.getAvailabilityConstraints(groupId);
        this.minimalNumberOfDays = getAvailabilityConstraints.numberOfDays();
        this.minimalNumberOfParticipants = getAvailabilityConstraints.numberOfParticipants();

        var allAvailabilitiesInGroup = availabilityRepository.findAvailabilitiesByGroupId(groupId);

        var firstDate = allAvailabilitiesInGroup.stream()
                .map(Availability::getDateFrom)
                .sorted()
                .findFirst()
                .orElseThrow();
        var lastDate = allAvailabilitiesInGroup.stream()
                .sorted(Comparator.comparing(Availability::getDateTo).reversed())
                .map(Availability::getDateTo)
                .findFirst()
                .orElseThrow();

        var userToDatesMap = createUserToDateMap(firstDate, lastDate, allAvailabilitiesInGroup);

        findLongestSubset(userToDatesMap);



    }

    private void findLongestSubset(Map<LocalDate, List<Long>> userToDatesMap) {
        List<Long> previousSubset = new ArrayList<>();
        LocalDate previousDate;
        int consecutiveDaysCounter = 0;
        boolean firstDate = true;
        for(var entrySet: userToDatesMap.entrySet()){
            var usersForDate = new ArrayList<>(entrySet.getValue());
            var date = entrySet.getKey();
            if(firstDate){
                previousSubset = usersForDate;
                previousDate = date;
                firstDate = false;
                consecutiveDaysCounter++;
            }
            else {
                usersForDate.retainAll(previousSubset);
                previousSubset = usersForDate;
            }
        }
    }

    private Map<LocalDate, List<Long>> createUserToDateMap(LocalDate currentDate, LocalDate lastDate, List<Availability> allAvailabilitiesInGroup) {
        Map<LocalDate, List<Long>> userToDatesMap = new HashMap<>();

        while(currentDate.isBefore(lastDate.plusDays(1))) {
            for (var availability: allAvailabilitiesInGroup) {
                var fromDate = availability.getDateFrom();
                var toDate = availability.getDateTo();
                if (currentDate.isAfter(fromDate.minusDays(1)) && currentDate.isBefore(toDate.plusDays(1))){
                    if (userToDatesMap.containsKey(currentDate)) {
                        var userList = userToDatesMap.get(currentDate);
                        var newUserList = new ArrayList<>(userList);
                        newUserList.add(availability.getUserId());
                        userToDatesMap.put(currentDate, newUserList);
                    }
                    else {
                        userToDatesMap.put(currentDate, List.of(availability.getUserId()));
                    }
                }
            }
            if(userToDatesMap.containsKey(currentDate) && userToDatesMap.get(currentDate).size() < minimalNumberOfParticipants){
                userToDatesMap.remove(currentDate);
            }
            currentDate = currentDate.plusDays(1);
        }
        return userToDatesMap;
    }
}
