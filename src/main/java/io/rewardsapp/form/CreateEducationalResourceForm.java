package io.rewardsapp.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreateEducationalResourceForm(
        @NotNull(message = "Please provide a title for the new educational resource.") String title,

        @NotNull(message = "Please specify the content type of the new educational resource.")
        String contentType,

        @NotNull(message = "Please provide the content for the new center.")
        @Min(value = 10, message = "The content for the educational resource should be at least 10 characters long.")
        String content,

        @NotNull(message = "Please specify the categories of the new educational resource.")
        @NotEmpty(message = "Please provide at least one category accepted by the new center.")
        String[] categories
) {}