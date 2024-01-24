package io.rewardsapp.service.implementation;

import io.rewardsapp.repository.ContentTypeRepository;
import io.rewardsapp.service.ContentTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContentTypeServiceImpl implements ContentTypeService {
    private final ContentTypeRepository contentTypeRepository;

    @Override
    public List<String> getAvailableContentTypeNames() {
        return contentTypeRepository.getAllContentTypeNames();
    }


}
