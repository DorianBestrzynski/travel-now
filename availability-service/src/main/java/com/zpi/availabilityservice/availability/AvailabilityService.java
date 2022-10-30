package com.zpi.availabilityservice.availability;

import com.zpi.availabilityservice.dto.AvailabilityDto;
import com.zpi.availabilityservice.dto.UserDto;
import com.zpi.availabilityservice.events.publisher.GenerationAvailabilityPublisher;
import com.zpi.availabilityservice.exceptions.IllegalDatesException;
import com.zpi.availabilityservice.proxies.AppUserProxy;
import com.zpi.availabilityservice.sharedGroupAvailability.SharedGroupAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvailabilityService {
    private final AvailabilityRepository availabilityRepository;
    private final AppUserProxy appUserProxy;
    private final SharedGroupAvailabilityService sharedGroupAvailabilityService;
    private final GenerationAvailabilityPublisher generationAvailabilityPublisher;

    private boolean validateDates(LocalDate dateFrom, LocalDate dateTo) {

        return dateTo.isAfter(dateFrom);
    }

    @Transactional
    public void deleteOverlapping(List<Availability> overlappingAvailabilities) {
        availabilityRepository.deleteAll(overlappingAvailabilities);
    }

    private Availability findOverlappingAvailabilitiesAndMerge(Long userId, Long groupId, LocalDate dateFrom,
                                                               LocalDate dateTo) {
        var overlappingAvailabilities = availabilityRepository.findOverlapping(userId, groupId, dateFrom, dateTo);

        for (var availability : overlappingAvailabilities) {
            if (availability.getDateFrom()
                            .isBefore(dateFrom))
                dateFrom = availability.getDateFrom();

            if (availability.getDateTo()
                            .isAfter(dateTo))
                dateTo = availability.getDateTo();
        }

        deleteOverlapping(overlappingAvailabilities);

        return new Availability(userId, groupId, dateFrom, dateTo);
    }

    @Transactional
    public Availability addNewAvailability(AvailabilityDto availabilityDto) {
        var areDatesValid = validateDates(availabilityDto.dateFrom(), availabilityDto.dateTo());

        if (!areDatesValid)
            throw new IllegalDatesException("Date from must be before date to and both must be after today's date");

        var availability = findOverlappingAvailabilitiesAndMerge(availabilityDto.userId(),
                                                                 availabilityDto.groupId(),
                                                                 availabilityDto.dateFrom(),
                                                                 availabilityDto.dateTo());

        availabilityRepository.save(availability);
        generationAvailabilityPublisher.publishAvailabilityGenerationEvent(availabilityDto.groupId());
        return availability;
    }

    public List<Availability> getUserAvailabilitiesInTripGroup(Long userId, Long groupId) {
        if (userId == null || groupId == null || userId < 0 || groupId < 0)
            throw new IllegalArgumentException("User id or group id is invalid. Id must be positive number");

        return availabilityRepository.findAvailabilitiesByUserIdAndGroupId(userId, groupId);
    }

    public Map<Long, List<Availability>> getAvailabilitiesInTripGroup(Long groupId) {
        return availabilityRepository.findAvailabilitiesByGroupId(groupId)
                                     .stream()
                                     .collect(Collectors.groupingBy(Availability::getUserId));
    }

    public Map<UserDto, List<Availability>> getAvailabilitiesInTripGroupWithUserData(Long groupId) {
        if (groupId == null || groupId < 0)
            throw new IllegalArgumentException("Group id is invalid. Id must be positive number");

        var availabilities = availabilityRepository.findAvailabilitiesByGroupId(groupId);
        var availabilitiesMap = availabilities.stream()
                                              .collect(Collectors.groupingBy(Availability::getUserId));

        var users = appUserProxy.getUsersDtos(availabilitiesMap.keySet()
                                                               .stream()
                                                               .toList());
        var usersMap = users.stream()
                            .collect(Collectors.toMap(UserDto::userId, Function.identity()));

        var result = new HashMap<UserDto, List<Availability>>();
        for (var entry : availabilitiesMap.entrySet()) {
            var userDto = usersMap.get(entry.getKey());
            result.put(userDto, entry.getValue());
        }

        return result;
    }

    @Transactional
    public void deleteAvailability(Long availabilityId, Long groupId) {
        if (availabilityId == null || availabilityId < 0)
            throw new IllegalArgumentException("Availability id is invalid. Id must be positive number");

        availabilityRepository.deleteById(availabilityId);
        generationAvailabilityPublisher.publishAvailabilityGenerationEvent(groupId);
    }

    @Transactional
    public Availability changeAvailability(Long availabilityId, LocalDate newDateFrom, LocalDate newDateTo) {
        var availability = availabilityRepository.findById(availabilityId)
                                                 .orElseThrow(() -> new IllegalArgumentException(
                                                         "Availability with id " + availabilityId + " does not exist"));

        if (newDateFrom != null && newDateTo != null) {
            var areDatesValid = validateDates(newDateFrom, newDateTo);

            if (!areDatesValid)
                throw new IllegalDatesException("Date from must be before date to");

            availability.setDateFrom(newDateFrom);
            availability.setDateTo(newDateTo);
        } else if (newDateFrom != null) {
            var areDatesValid = validateDates(newDateFrom, availability.getDateTo());

            if (!areDatesValid)
                throw new IllegalDatesException("New Date from must be before date to");

            availability.setDateFrom(newDateFrom);
        } else if (newDateTo != null) {
            var areDatesValid = validateDates(availability.getDateFrom(), newDateTo);

            if (!areDatesValid)
                throw new IllegalDatesException("Date from must be before new date to");

            availability.setDateTo(newDateTo);
        }
        generationAvailabilityPublisher.publishAvailabilityGenerationEvent(availability.getGroupId());
        return availability;
    }

    public void trigger(Long groupId) {
        generationAvailabilityPublisher.publishAvailabilityGenerationEvent(groupId);
    }
}

