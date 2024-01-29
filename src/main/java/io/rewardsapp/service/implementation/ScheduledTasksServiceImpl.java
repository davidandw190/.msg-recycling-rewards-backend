package io.rewardsapp.service.implementation;

import io.rewardsapp.dto.UserDTO;
import io.rewardsapp.service.EmailService;
import io.rewardsapp.service.RewardPointsService;
import io.rewardsapp.service.ScheduledTasksService;
import io.rewardsapp.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@EnableScheduling
@RequiredArgsConstructor
public class ScheduledTasksServiceImpl implements ScheduledTasksService {

    public static final int EMAIL_THREAD_POOL_SIZE = 15;

    private final UserService userService;

    private final RewardPointsService rewardPointsService;

    private final EmailService emailService;

    private final ExecutorService emailThreadPool = Executors.newFixedThreadPool(EMAIL_THREAD_POOL_SIZE);


    /**
     * Sends email notifications to inactive users (if they have notifications enabled).
     * Inactive users are identified based on their activity one week prior to the current date.
     */
    @Override
    @Scheduled(cron = "0 0 0 * * MON")  // to run every Monday at midnight
    public void sendEmailToInactiveUsers() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        List<UserDTO> inactiveUsers = userService.findInactiveUsers(oneWeekAgo);

        CompletableFuture<?>[] futures = inactiveUsers.stream()
                .filter(UserDTO::notificationsEnabled)
                .map(user -> CompletableFuture.runAsync(
                        () -> emailService.sendInactiveUserEmail(user.firstName(), user.lastName()),
                        emailThreadPool))
                .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(futures).join();
    }

    /**
     * Restores reward points for recyclers. It retrieves a list of recycler IDs and processes
     * them in batches, sending emails and restoring points accordingly.
     */
    @Override
    @Transactional
    @Scheduled(cron = "0 0 0 1 * *")  // to run every 1st day of each month at midnight
    public void restoreRecyclersRewardPoints() {
        log.info("Starting restoreRecyclersRewardPoints task...");

        List<Long> recyclersIds = rewardPointsService.getRecyclersIds();
        int batchSize = 2000;

        for (int i = 0; i < recyclersIds.size(); i += batchSize) {
            List<Long> batchIds = recyclersIds.subList(i, Math.min(i + batchSize, recyclersIds.size()));

            processBatch(batchIds);
        }

        log.info("Completed restoreRecyclersRewardPoints task.");
    }

    /**
     * Processes a batch of recycler IDs for the purpose of restoring reward points.
     * For each user ID, it sends an email with the earned points and then performs
     * the actual reward points restoration.
     *
     * @param batchIds the list of user IDs to process
     */
    private void processBatch(List<Long> batchIds) {
        try {
            List<Long> earnedPoints = rewardPointsService.getRewardPointsAmount(batchIds);

            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (int j = 0; j < batchIds.size(); j++) {
                Long userId = batchIds.get(j);
                Long points = earnedPoints.get(j);

                CompletableFuture<Void> emailFuture = CompletableFuture.runAsync(
                        () -> sendRewardPointsEmail(userId, points),
                        emailThreadPool);
                futures.add(emailFuture);
            }

            // to wait for all emails in the batch to be sent before proceeding
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            // to perform the actual reward points restoration in a batch
            rewardPointsService.restoreRecyclersRewardPoints(batchIds);
        } catch (Exception e) {
            log.error("Error processing batch", e);
        }
    }

    /**
     * Checks if the user has enabled notifications and, if yes, then sends
     * an email with the monthly reward points.
     *
     * @param userId        the ID of the user to which the email should be sent
     * @param earnedPoints  the amount of reward points earned last month
     */
    private void sendRewardPointsEmail(Long userId, Long earnedPoints) {
        try {
            UserDTO user = userService.getUser(userId);

            if (user.notificationsEnabled()) {
                emailService.sendMonthlyRewardPointsEmail(user.email(), user.firstName(), earnedPoints);
            }

        } catch (Exception e) {
            log.error("Error sending reward points email for user with ID " + userId, e);
        }
    }
}
