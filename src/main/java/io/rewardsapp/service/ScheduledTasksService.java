package io.rewardsapp.service;

import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;

public interface ScheduledTasksService {

    @Scheduled(cron = "0 0 0 * * MON") // to run every Monday at midnight
    void sendEmailToInactiveUsers();

    @Transactional
    @Scheduled(cron = "0 0 0 1 * *")  // to run every 1st day of each month at midnight
    void restoreRecyclersRewardPoints();
}
