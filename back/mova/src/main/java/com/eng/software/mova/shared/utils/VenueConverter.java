package com.eng.software.mova.shared.utils;

import com.eng.software.mova.application.dto.venue.VenueResponseDTO;
import com.eng.software.mova.domain.model.Venue;
import com.eng.software.mova.infrastructure.persistence.entity.VenueEntity;
import org.springframework.stereotype.Component;

@Component
public class VenueConverter {

    public static Venue entityToDomain(VenueEntity entity) {
        if (entity == null) return null;
        return Venue.builder()
                .id(entity.getId())
                .name(entity.getName())
                .number(entity.getNumber())
                .city(entity.getCity())
                .street(entity.getStreet())
                .neighborhood(entity.getNeighborhood())
                .landmark(entity.getLandmark())
                .hasParkingLot(entity.isHasParkingLot())
                .hasAccessibility(entity.isHasAccessibility())
                .hasBathroom(entity.isHasBathroom())
                .hasFoodsAndDrinks(entity.isHasFoodsAndDrinks())
                .build();
    }

    public static VenueEntity domainToEntity(Venue domain) {
        if (domain == null) return null;
        return VenueEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .number(domain.getNumber())
                .city(domain.getCity())
                .street(domain.getStreet())
                .neighborhood(domain.getNeighborhood())
                .landmark(domain.getLandmark())
                .hasParkingLot(domain.isHasParkingLot())
                .hasAccessibility(domain.isHasAccessibility())
                .hasBathroom(domain.isHasBathroom())
                .hasFoodsAndDrinks(domain.isHasFoodsAndDrinks())
                .build();
    }

    public static VenueResponseDTO domainToResponse(Venue domain) {
        if (domain == null) return null;
        return new VenueResponseDTO(
                domain.getId(), domain.getName(), domain.getNumber(), domain.getCity(),
                domain.getStreet(), domain.getNeighborhood(), domain.getLandmark(),
                domain.isHasParkingLot(), domain.isHasAccessibility(),
                domain.isHasBathroom(), domain.isHasFoodsAndDrinks()
        );
    }
}
