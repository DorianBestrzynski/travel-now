package com.zpi.tripgroupservice.invitation;

import com.zpi.tripgroupservice.user_group.UserGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/invitation")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    @GetMapping()
    public ResponseEntity<String> createInvitation(@RequestParam(name = "user", required = true) Long userId,
                                                   @RequestParam(name = "group", required = true) Long groupId) {
        var invitationLink = invitationService.createInvitation(userId, groupId);

        return ResponseEntity.created(URI.create(invitationLink)).build();
    }

    @PutMapping()
    public ResponseEntity<UserGroup> acceptInvitation(@RequestParam(name = "token", required = true) String token,
                                                      @RequestParam(name = "user", required = true) Long userId) {
        var result = invitationService.acceptInvitation(userId, token);

        return ResponseEntity.ok(result);
    }
}
