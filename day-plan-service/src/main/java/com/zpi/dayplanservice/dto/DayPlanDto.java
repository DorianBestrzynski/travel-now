package com.zpi.dayplanservice.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public record DayPlanDto(
    @NotNull
    @JsonProperty("groupId")
    Long groupId,
    @NotNull
    @JsonProperty("date")
    LocalDate date,
    @NotEmpty
    @Length(max = 100)
    @JsonProperty("name")
    String name)
{
}
