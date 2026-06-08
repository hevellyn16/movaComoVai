package com.eng.software.mova.application.dto.venue;

public record VenueUpdateDTO(
        String name,
        String number,
        String city,
        String street,
        String neighborhood,
        String landmark,
        Boolean hasParkingLot,
        Boolean hasAccessibility,
        Boolean hasBathroom,
        Boolean hasFoodsAndDrinks
) {
}
