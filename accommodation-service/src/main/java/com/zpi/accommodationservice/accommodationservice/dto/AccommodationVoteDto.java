package com.zpi.accommodationservice.accommodationservice.dto;

import javax.validation.constraints.NotNull;

public record AccommodationVoteDto(@NotNull Long userId,
                                   @NotNull Long accommodationId,
                                   @NotNull Long groupId) {

}
