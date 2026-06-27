package com.eng.software.mova.domain.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class EventSchedule {
    @EqualsAndHashCode.Include
    private UUID id;
    private String title;
    private String description;
    private LocalDateTime scheduleTime;
}
