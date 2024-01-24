package io.rewardsapp.service.implementation;

import io.rewardsapp.repository.CategoryRepository;
import io.rewardsapp.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public List<String> getAvailableCategoryNames() {
        return categoryRepository.getAllCategoryNames();
    }
}
