package com.zpi.availabilityservice.sharedGroupAvailability;

import com.zpi.availabilityservice.aspects.AuthorizeCoordinatorShared;
import com.zpi.availabilityservice.availability.Availability;
import com.zpi.availabilityservice.availability.AvailabilityRepository;
import com.zpi.availabilityservice.dto.SelectedAvailabilityDto;
import com.zpi.availabilityservice.proxies.TripGroupProxy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.zpi.availabilityservice.exceptions.ExceptionInfo.SHARED_AVAILABILITY_NOT_FOUND;
import static java.time.temporal.ChronoUnit.DAYS;

@Service
@RequiredArgsConstructor
public class SharedGroupAvailabilityService {

    private final SharedGroupAvailabilityRepository sharedGroupAvailabilityRepository;
    private Integer minimalNumberOfDays;
    private Integer minimalNumberOfParticipants;
    private final TripGroupProxy tripGroupProxy;
    private final AvailabilityRepository availabilityRepository;
    private static final String INNER_COMMUNICATION = "microserviceCommunication";

    @Transactional
    public void generateSharedGroupAvailability(Long groupId) {
        var getAvailabilityConstraints = tripGroupProxy.getAvailabilityConstraints(INNER_COMMUNICATION,groupId);
        minimalNumberOfDays = getAvailabilityConstraints.numberOfDays();
        minimalNumberOfParticipants = getAvailabilityConstraints.numberOfParticipants();

        processGeneration(groupId, getAvailabilityConstraints.selectedSharedAvailability());
    }

    private void processGeneration(Long groupId, Long selectedSharedAvailability) {
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

        if(selectedSharedAvailability == null){
            sharedGroupAvailabilityRepository.deleteAllByGroupId(groupId);
        }
        else {
            var allToDelete = sharedGroupAvailabilityRepository.getAllExceptSetSharedAvailability(groupId, selectedSharedAvailability);
            sharedGroupAvailabilityRepository.deleteAll(allToDelete);
        }
        sharedGroupAvailabilityRepository.saveAll(filteredAvailabilities);
    }

    @Transactional
    public void generateSharedGroupAvailability(Long groupId, Integer givenMinimalNumberOfDays, Integer givenMinimalNumberOfParticipants) {
        var getAvailabilityConstraints = tripGroupProxy.getAvailabilityConstraints(INNER_COMMUNICATION,groupId);
        minimalNumberOfDays = getAvailabilityConstraints.numberOfDays();
        minimalNumberOfParticipants = getAvailabilityConstraints.numberOfParticipants();
        if(givenMinimalNumberOfDays != null) {
            minimalNumberOfDays = givenMinimalNumberOfDays;
        }
        if(givenMinimalNumberOfParticipants != null) {
            minimalNumberOfParticipants = givenMinimalNumberOfParticipants;
        }

        processGeneration(groupId, getAvailabilityConstraints.selectedSharedAvailability());
    }

    public List<SharedGroupAvailability> filterAvailabilities(List<SharedGroupAvailability> availabilities) {
        var initialGrouping = availabilities.stream()
                .collect(Collectors.groupingBy(av -> av.getUsersList().size()))
                .values().stream()
                .map(f -> f.stream()
                        .sorted(Comparator.comparingInt(SharedGroupAvailability::getNumberOfDays).reversed()).toList())
                .flatMap(s -> handleSameNumberOfDays(s).stream())
                .toList();


        return initialGrouping.stream()
                .collect(Collectors.groupingBy(SharedGroupAvailability::getNumberOfDays))
                .values().stream()
                .map(f -> f.stream()
                        .sorted(Comparator.comparingInt(SharedGroupAvailability::getNumberOfUsers).reversed()).toList())
                .flatMap(s -> handleSameNumberOfUsers(s).stream())
                .toList();
    }

    private List<SharedGroupAvailability> handleSameNumberOfDays(List<SharedGroupAvailability> initialList) {
        var returnList = new ArrayList<SharedGroupAvailability>();
        if (!initialList.isEmpty()) {
            var biggestDayNumber = initialList.get(0).getNumberOfDays();
            for (var sga : initialList) {
                if (sga.getNumberOfDays().equals(biggestDayNumber)) {
                    returnList.add(sga);
                }
            }
        }
        return returnList;
        }

    private List<SharedGroupAvailability> handleSameNumberOfUsers(List<SharedGroupAvailability> initialList) {
        var returnList = new ArrayList<SharedGroupAvailability>();
        if (!initialList.isEmpty()) {
            var biggestNumberOfUsers = initialList.get(0).getUsersList().size();
            for (var sga : initialList) {
                if (sga.getUsersList().size() == biggestNumberOfUsers) {
                    returnList.add(sga);
                }
            }
        }
        return returnList;
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

    @AuthorizeCoordinatorShared
    public void acceptSharedGroupAvailability(Long sharedGroupAvailabilityId) {
        var sharedAvailability = sharedGroupAvailabilityRepository.findById(sharedGroupAvailabilityId).orElseThrow(() -> new EntityNotFoundException(SHARED_AVAILABILITY_NOT_FOUND));
        tripGroupProxy.setSelectedAvailability(INNER_COMMUNICATION, new SelectedAvailabilityDto(sharedAvailability.getGroupId(), sharedGroupAvailabilityId, sharedAvailability.getDateFrom(), sharedAvailability.getDateTo()));
    }

    @Transactional
    public SharedGroupAvailability createSharedGroupAvailability(LocalDate dateFrom, LocalDate dateTo, Long groupId) {
        sharedGroupAvailabilityRepository.deleteAllByGroupId(groupId);
        int daysBetween = (int) DAYS.between(dateFrom, dateTo);
        var sharedGroupAvailability = new SharedGroupAvailability(groupId, Collections.emptyList(), dateFrom, dateTo, daysBetween);
        var savedAvailability = sharedGroupAvailabilityRepository.save(sharedGroupAvailability);
        tripGroupProxy.setSelectedAvailability(INNER_COMMUNICATION, new SelectedAvailabilityDto(groupId, savedAvailability.getSharedGroupAvailabilityId(), savedAvailability.getDateFrom(), savedAvailability.getDateTo()));
        return savedAvailability;

    }

    public SharedGroupAvailability getSharedGroupAvailability(Long sharedGroupAvailabilityId) {
        return sharedGroupAvailabilityRepository.findById(sharedGroupAvailabilityId).orElseThrow(() -> new EntityNotFoundException(SHARED_AVAILABILITY_NOT_FOUND));
    }
}
