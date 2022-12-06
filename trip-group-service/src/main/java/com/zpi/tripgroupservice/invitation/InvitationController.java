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
    public ResponseEntity<String> createInvitation(@RequestParam(name = "group") Long groupId) {
        var invitationLink = invitationService.createInvitation(groupId);

        return ResponseEntity.created(URI.create(invitationLink))
                             .header("Access-Control-Expose-Headers","Location")
                             .build();
    }

    @PutMapping()
    public ResponseEntity<UserGroup> acceptInvitation(@RequestParam(name = "token") String token,
                                                      @RequestParam(name = "user") Long userId) {
        var result = invitationService.acceptInvitation(userId, token);

        return ResponseEntity.ok(result);
    }
}
