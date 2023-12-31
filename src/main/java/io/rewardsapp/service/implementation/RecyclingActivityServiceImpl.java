package io.rewardsapp.service.implementation;

import io.rewardsapp.domain.*;
import io.rewardsapp.exception.ApiException;
import io.rewardsapp.form.CreateRecyclingActivityForm;
import io.rewardsapp.repository.*;
import io.rewardsapp.service.RecyclingActivityService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static io.rewardsapp.utils.RewardPointsUtils.computeRewardPointsByUnitsRecycled;

@Service
@RequiredArgsConstructor
public class RecyclingActivityServiceImpl implements RecyclingActivityService {
    private final UserRecyclingActivityRepository activityRepository;
    private final RewardPointsRepository rewardPointsRepository;
    private final CenterRepository centerRepository;
    private final JpaUserRepository userRepository;
    private final MaterialsRepository materialsRepository;

    @Override
    public List<UserRecyclingActivity> getUserRecyclingActivitiesAtCenter(User user, RecyclingCenter center) {
        return activityRepository.findAllByUserAndRecyclingCenter(user, center);
    }

    @Override
    @Transactional
    public void createActivity(CreateRecyclingActivityForm form) {
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

        updateUserRewardPoints(user, form.amount(), material);
    }


    private void updateUserRewardPoints(User user, Long amountRecycledInUnits, RecyclableMaterial materialRecycled) {
        Long updatedRewardPoints = computeRewardPointsByUnitsRecycled(amountRecycledInUnits, materialRecycled.getRewardPoints());

        // Check if the user has existing reward points
        RewardPoints existingRewardPoints = rewardPointsRepository.findRewardPointsByUserId(user.getId());

        if (existingRewardPoints == null) {
            // If the user doesn't have reward points entry, create a new one
            RewardPoints newRewardPoints = RewardPoints.builder()
                    .user(user)
                    .totalPoints(updatedRewardPoints)
                    .lastUpdated(LocalDateTime.now())
                    .build();

            rewardPointsRepository.save(newRewardPoints);
        } else {
            // If the user has existing reward points entry, update it
            existingRewardPoints.setTotalPoints(existingRewardPoints.getTotalPoints() + updatedRewardPoints);
            existingRewardPoints.setLastUpdated(LocalDateTime.now());

            rewardPointsRepository.save(existingRewardPoints);
        }
    }

}
