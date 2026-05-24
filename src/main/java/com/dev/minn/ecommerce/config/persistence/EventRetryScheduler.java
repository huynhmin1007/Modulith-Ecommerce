package com.dev.minn.ecommerce.config.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.IncompleteEventPublications;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventRetryScheduler {

    private static final int MAX_ATTEMPTS = 5;

    private static final Duration DELAY = Duration.ofSeconds(30);
    private static final Duration MAX_BACKOFF = Duration.ofMinutes(30);

    private final IncompleteEventPublications publications;

    @Scheduled(fixedDelay = 30000)
    public void retry() {
        publications.resubmitIncompletePublications(
                publications -> {
                    int attempts = publications.getCompletionAttempts();
                    UUID id = publications.getIdentifier();

                    if (attempts >= MAX_ATTEMPTS) {
                        log.warn("Publication {} exceed max attempts", id);
                        return false;
                    }

                    Instant lastAttempt = publications.getLastResubmissionDate();

                    Duration backoff = calculateBackoff(attempts);
                    Instant nextAttempt = lastAttempt.plus(backoff);

                    boolean shouldRetry = Instant.now().isAfter(nextAttempt);

                    if (shouldRetry) {
                        log.info("Retrying publication {} attempt={}, nextRetry={}", id, attempts, nextAttempt);
                    }

                    return shouldRetry;
                });
    }

    private Duration calculateBackoff(int attempts) {
        long seconds = (long) (DELAY.getSeconds() * Math.pow(2, attempts));
        return Duration.ofSeconds(
                Math.min(seconds, MAX_BACKOFF.getSeconds())
        );
    }
}
