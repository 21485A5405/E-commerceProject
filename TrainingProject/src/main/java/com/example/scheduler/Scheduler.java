package com.example.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.model.UserToken;
import com.example.repo.UserTokenRepo;

import jakarta.transaction.Transactional;

@Component
public class Scheduler {

	private final UserTokenRepo userTokenRepo;

    public Scheduler(UserTokenRepo userTokenRepo) {
        this.userTokenRepo = userTokenRepo;
    }

    @Scheduled(fixedRate = 1440 * 60 * 1000)
    @Transactional
    public void clearExpiredTokens() {
        LocalDateTime expiry = LocalDateTime.now().minusMinutes(1440);
        if (expiry!= null) {

            List<UserToken> expiredUsers = userTokenRepo.findExpiredTokens(expiry);

	        for (UserToken user : expiredUsers) {
	           userTokenRepo.deleteById(user.getTokenId());
	        }
        }
     }
}