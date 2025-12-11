package com.fitgroup.backend.challenge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fitgroup.backend.challenge.entity.Challenge;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
}

