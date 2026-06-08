package com.eng.software.mova.application.dto.venue;

import java.util.UUID;

public record VenueResponseDTO(
        UUID id,
        String name,
        String number,
        String city,
        String street,
        String neighborhood,
        String landmark,
        boolean hasParkingLot,
        boolean hasAccessibility,
        boolean hasBathroom,
        boolean hasFoodsAndDrinks
) {
}
