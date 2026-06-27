package com.eng.software.mova.domain.model;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Venue {

    @EqualsAndHashCode.Include
    private UUID id;
    private String name;
    private String number;
    private String city;
    private String street;
    private String neighborhood;
    private String landmark;
    private boolean hasParkingLot;
    private boolean hasAccessibility;
    private boolean hasBathroom;
    private boolean hasFoodsAndDrinks;

}
