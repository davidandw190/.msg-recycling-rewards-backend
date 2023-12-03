package io.rewardsapp.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

@Data
@SuperBuilder
@RequiredArgsConstructor
@JsonInclude(NON_DEFAULT)
public class Role {
    private Long id;
    private String name;
    private String permission;
}