package com.eng.software.mova.application.dto.venue;

import jakarta.validation.constraints.NotBlank;

public record VenueCreateDTO(
        @NotBlank String name,
        @NotBlank String number,
        @NotBlank String city,
        @NotBlank String street,
        @NotBlank String neighborhood,
        String landmark,
        boolean hasParkingLot,
        boolean hasAccessibility,
        boolean hasBathroom,
        boolean hasFoodsAndDrinks
) {
}
