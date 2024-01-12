package io.rewardsapp.service.implementation;

import io.rewardsapp.repository.TipRepository;
import io.rewardsapp.service.TipsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TipsServiceImpl implements TipsService {
    private final TipRepository tipRepository;

    @Override
    public String getRandomRecyclingTip() {
        if (tipRepository.count() > 0) {
            return tipRepository.getContentByTipId(tipRepository.getRandomTipId());
        } else {
            return "Find more in our Eco Learn section";
        }
    }
}
