package io.rewardsapp.service.implementation;

import io.rewardsapp.dto.UserDTO;
import io.rewardsapp.service.EmailService;
import io.rewardsapp.service.ScheduledTasksService;
import io.rewardsapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

@Service
@RequiredArgsConstructor
public class ScheduledTasksServiceImpl implements ScheduledTasksService {

    private final UserService userService;

    private final EmailService emailService;


    @Override
    @Scheduled(cron = "0 0 0 * * MON")  // to run every Monday at midnight
    public void sendEmailToInactiveUsers() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        List<UserDTO> inactiveUsers = userService.findInactiveUsers(oneWeekAgo);

        ForkJoinPool forkJoinPool = new ForkJoinPool();

        try {
            forkJoinPool.submit(() ->
                    inactiveUsers.parallelStream()
                            .forEach(user -> emailService.sendInactiveUserEmail(user.firstName(), user.lastName()))
            ).join();

        } finally {
            forkJoinPool.shutdown();
        }
    }
}
