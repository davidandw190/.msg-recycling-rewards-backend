package io.rewardsapp.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

/**
 * The Role class represents a role in the recycling rewards application.
 * It includes information such as role name and associated permissions.
 */
@Data
@SuperBuilder
@RequiredArgsConstructor
@JsonInclude(NON_DEFAULT)
public class Role {
    private Long id;
    private String name;
    private String permission;
}