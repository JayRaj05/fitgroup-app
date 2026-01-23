package com.fitgroup.backend.checkin.controller;

import com.fitgroup.backend.checkin.dto.DailyCheckinRequest;
import com.fitgroup.backend.checkin.entity.DailyCheckin;
import com.fitgroup.backend.checkin.service.DailyCheckinService;
import com.fitgroup.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/checkin")
@RequiredArgsConstructor
public class DailyCheckinController {

    private final DailyCheckinService checkinService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> checkIn(
            @RequestBody DailyCheckinRequest req,
            Principal principal
    ) {
        String email = principal.getName();

        Long userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        DailyCheckin saved = checkinService.checkIn(userId, req);

        return ResponseEntity.ok(saved);
    }

    @GetMapping("/calendar")
    public ResponseEntity<?> getCheckinCalendar(
            @RequestParam(defaultValue = "30") int days,
            Principal principal
    ) {
        String email = principal.getName();

        Long userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
        return ResponseEntity.ok(checkinService.getCalendar(userId, days));
    }
}
