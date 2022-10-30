package com.zpi.availabilityservice.sharedGroupAvailability;

import com.zpi.availabilityservice.availability.Availability;
import com.zpi.availabilityservice.availability.AvailabilityRepository;
import com.zpi.availabilityservice.proxies.TripGroupProxy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SharedGroupAvailabilityService {

    private final SharedGroupAvailabilityRepository sharedGroupAvailabilityRepository;
    private Integer minimalNumberOfDays;
    private Integer minimalNumberOfParticipants;
    private final TripGroupProxy tripGroupProxy;
    private final AvailabilityRepository availabilityRepository;

    @Transactional
    public void generateSharedGroupAvailability(Long groupId) {
        var getAvailabilityConstraints = tripGroupProxy.getAvailabilityConstraints(groupId);
        minimalNumberOfDays = getAvailabilityConstraints.numberOfDays();
        minimalNumberOfParticipants = getAvailabilityConstraints.numberOfParticipants();

        var allAvailabilitiesInGroup = availabilityRepository.findAvailabilitiesByGroupId(groupId);

        var firstDate = LocalDate.MAX;
        var lastDate = LocalDate.MIN;
        for (var availability: allAvailabilitiesInGroup) {
            if (availability.getDateFrom().isBefore(firstDate))
                firstDate = availability.getDateFrom();

            if (availability.getDateTo().isAfter(lastDate))
                lastDate = availability.getDateTo();
        }

        var userToDatesMap = createUserToDateMap(firstDate, lastDate, allAvailabilitiesInGroup);

        var bestAvailabilities = findLongestSubset(userToDatesMap, groupId);

        var filteredAvailabilities = filterAvailabilities(bestAvailabilities);

        sharedGroupAvailabilityRepository.deleteAllByGroupId(groupId);
        sharedGroupAvailabilityRepository.saveAll(filteredAvailabilities);
    }

    public List<SharedGroupAvailability> filterAvailabilities(List<SharedGroupAvailability> availabilities) {
        var initialGrouping = availabilities.stream()
                .collect(Collectors.groupingBy(av -> av.getUsersList().size()))
                .values().stream()
                .map(f -> f.stream()
                        .sorted(Comparator.comparingInt(SharedGroupAvailability::getNumberOfDays).reversed()).toList())
                .map(v -> v.get(0))
                .toList();

        return initialGrouping.stream()
                .collect(Collectors.groupingBy(SharedGroupAvailability::getNumberOfDays))
                .values().stream()
                .map(f -> f.stream()
                        .sorted(Comparator.comparingInt(SharedGroupAvailability::getNumberOfUsers).reversed()).toList())
                .map(v -> v.get(0))
                .toList();
    }

    private List<SharedGroupAvailability> findLongestSubset(Map<LocalDate, List<Long>> userToDatesMap, Long groupId) {
        List<Long> previousSubset;
        LocalDate previousDate;
        int consecutiveDaysCounter = 0;
        List<SharedGroupAvailability> resultList = new ArrayList<>();

        for (var dates: userToDatesMap.keySet()) {
            previousDate = dates;
            previousSubset = userToDatesMap.get(dates);
            consecutiveDaysCounter++;
            for (var entrySet : userToDatesMap.entrySet()) {
                var checkedDate = entrySet.getKey();
                if (checkedDate.isBefore(previousDate.plusDays(1))) {
                    continue;
                }
                var usersForDate = new ArrayList<>(entrySet.getValue());
                usersForDate.retainAll(previousSubset);
                if(!isEnoughUsers(usersForDate) || !areDatesConsecutive(previousDate, checkedDate)){
                    break;
                }
                previousSubset = usersForDate;
                previousDate = checkedDate;
                consecutiveDaysCounter++;
                if(isDurationLongEnough(consecutiveDaysCounter)) {
                    resultList.add(new SharedGroupAvailability(groupId, usersForDate, dates, checkedDate, consecutiveDaysCounter));
                }
            }
            consecutiveDaysCounter = 0;
        }
        return  resultList;
    }

    private boolean isEnoughUsers(List<Long> users) {
        return users.size() >= minimalNumberOfParticipants;
    }

    private boolean isDurationLongEnough(int consecutiveDaysCounter) {
        return consecutiveDaysCounter >= minimalNumberOfDays;
    }

    private boolean areDatesConsecutive( LocalDate previousDate, LocalDate currentDate) {
        return previousDate.plusDays(1).equals(currentDate);
    }

    private Map<LocalDate, List<Long>> createUserToDateMap(LocalDate currentDate, LocalDate lastDate, List<Availability> allAvailabilitiesInGroup) {
        Map<LocalDate, List<Long>> userToDatesMap = new TreeMap<>();

        while (currentDate.isBefore(lastDate.plusDays(1))) {
            for (var availability: allAvailabilitiesInGroup) {
                var fromDate = availability.getDateFrom();
                var toDate = availability.getDateTo();
                if (currentDate.isAfter(fromDate.minusDays(1)) && currentDate.isBefore(toDate.plusDays(1))) {
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
            if(userToDatesMap.containsKey(currentDate) && userToDatesMap.get(currentDate).size() < minimalNumberOfParticipants) {
                userToDatesMap.remove(currentDate);
            }
            currentDate = currentDate.plusDays(1);
        }
        return userToDatesMap;
    }

    public List<SharedGroupAvailability> getGroupSharedAvailabilities(Long groupId) {
        return sharedGroupAvailabilityRepository.findAllByGroupId(groupId);
    }
}
