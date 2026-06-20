package com.eng.software.mova.application.service;

import com.eng.software.mova.application.dto.venue.VenueCreateDTO;
import com.eng.software.mova.application.dto.venue.VenueUpdateDTO;
import com.eng.software.mova.domain.model.Venue;
import com.eng.software.mova.domain.port.VenueRepositoryPort;
import com.eng.software.mova.shared.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VenueService {
    private final VenueRepositoryPort repository;

    @Transactional
    public Venue create(VenueCreateDTO dto) {
        Venue venue = Venue.builder()
                .name(dto.name())
                .number(dto.number())
                .city(dto.city())
                .street(dto.street())
                .neighborhood(dto.neighborhood())
                .landmark(dto.landmark())
                .hasParkingLot(dto.hasParkingLot())
                .hasAccessibility(dto.hasAccessibility())
                .hasBathroom(dto.hasBathroom())
                .hasFoodsAndDrinks(dto.hasFoodsAndDrinks())
                .build();
        return repository.save(venue);
    }

    public Page<Venue> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Venue findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found with id: " + id));
    }

    public Page<Venue> search(String q, Pageable pageable) {
        return repository.search(q, pageable);
    }

    public Page<Venue> search(String name, String city, String neighborhood, Pageable pageable) {
        return repository.search(name, pageable);
    }

    @Transactional
    public Venue update(UUID id, VenueUpdateDTO dto) {
        Venue existingVenue = findById(id);

        if (dto.name() != null) existingVenue.setName(dto.name());
        if (dto.number() != null) existingVenue.setNumber(dto.number());
        if (dto.city() != null) existingVenue.setCity(dto.city());
        if (dto.street() != null) existingVenue.setStreet(dto.street());
        if (dto.neighborhood() != null) existingVenue.setNeighborhood(dto.neighborhood());
        if (dto.landmark() != null) existingVenue.setLandmark(dto.landmark());
        if (dto.hasParkingLot() != null) existingVenue.setHasParkingLot(dto.hasParkingLot());
        if (dto.hasAccessibility() != null) existingVenue.setHasAccessibility(dto.hasAccessibility());
        if (dto.hasBathroom() != null) existingVenue.setHasBathroom(dto.hasBathroom());
        if (dto.hasFoodsAndDrinks() != null) existingVenue.setHasFoodsAndDrinks(dto.hasFoodsAndDrinks());

        return repository.save(existingVenue);
    }

    @Transactional
    public void delete(UUID id) {
        // Verifica se existe antes de deletar
        findById(id);
        repository.deleteById(id);
    }

}
