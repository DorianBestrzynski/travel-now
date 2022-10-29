package com.zpi.availabilityservice.sharedGroupAvailability;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDate {
    private LocalDate date;
    private List<Long> users;
}
