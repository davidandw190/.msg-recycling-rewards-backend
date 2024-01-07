package io.rewardsapp.service.implementation;

import io.rewardsapp.domain.*;
import io.rewardsapp.exception.ApiException;
import io.rewardsapp.form.CreateRecyclingActivityForm;
import io.rewardsapp.repository.*;
import io.rewardsapp.service.RecyclingActivityService;
import io.rewardsapp.service.RewardPointsService;
import io.rewardsapp.service.VoucherService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecyclingActivityServiceImpl implements RecyclingActivityService {

    // Services
    private final RewardPointsService rewardPointsService;
    private final VoucherService voucherService;

    // Repositories
    private final UserRecyclingActivityRepository activityRepository;
    private final JpaUserRepository userRepository;
    private final CenterRepository centerRepository;
    private final MaterialsRepository materialsRepository;

    /**
     * Retrieves recycling activities of a user at a specific recycling center.
     *
     * @param user   The user for whom activities are retrieved.
     * @param center The recycling center where activities are retrieved.
     * @return A list of UserRecyclingActivity.
     */
    @Override
    public List<UserRecyclingActivity> getUserRecyclingActivitiesAtCenter(User user, RecyclingCenter center) {
        return activityRepository.findAllByUserAndRecyclingCenter(user, center);
    }

    /**
     * Creates a new recycling activity based on the provided form.
     *
     * @param form The form containing recycling activity details.
     * @return
     * @throws ApiException if the user, recycling center, or recyclable material is not found.
     */
    @Override
    @Transactional
    public boolean createActivity(CreateRecyclingActivityForm form) {
        User user = userRepository.findById(form.userId()).orElseThrow(
                () -> new ApiException("User not found")
        );

        RecyclingCenter recyclingCenter = centerRepository.findById(form.centerId())
                .orElseThrow(() -> new ApiException("Recycling center not found"));

        RecyclableMaterial material = materialsRepository.findById(form.materialId())
                .orElseThrow(() -> new ApiException("Recyclable material not found"));

        UserRecyclingActivity activity = UserRecyclingActivity.builder()
                .user(user)
                .recyclingCenter(recyclingCenter)
                .recycledMaterial(material)
                .amount(form.amount())
                .createdAt(LocalDateTime.now())
                .build();

        activityRepository.save(activity);

        long rewardPointsBeforeActivity = rewardPointsService.getRewardPointsAmount(form.userId());

        rewardPointsService.updateUserRewardPoints(user, form.amount(), material);

        return voucherService.checkForEarnedVouchers(user, rewardPointsBeforeActivity);
    }

}
