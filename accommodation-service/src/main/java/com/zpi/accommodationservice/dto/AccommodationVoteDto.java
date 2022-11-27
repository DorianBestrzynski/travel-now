package com.zpi.accommodationservice.dto;

import javax.validation.constraints.NotNull;

public record AccommodationVoteDto(@NotNull Long accommodationId,
                                   @NotNull Long groupId) {

}
