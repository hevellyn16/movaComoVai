package com.eng.software.mova.infrastructure.persistence.repository;

import com.eng.software.mova.domain.model.User;
import com.eng.software.mova.domain.model.Venue;
import com.eng.software.mova.domain.port.UserRepositoryPort;
import com.eng.software.mova.domain.port.VenueRepositoryPort;
import com.eng.software.mova.infrastructure.persistence.entity.UserEntity;
import com.eng.software.mova.infrastructure.persistence.entity.VenueEntity;
import com.eng.software.mova.shared.utils.UserConverter;
import com.eng.software.mova.shared.utils.VenueConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class VenueRepository implements VenueRepositoryPort {
    private final VenueJpaRepository venueJpaRepository;

    @Override
    public Venue save(Venue venue) {
        VenueEntity savedEntity = venueJpaRepository.save(VenueConverter.domainToEntity(venue));
        return VenueConverter.entityToDomain(savedEntity);
    }

    @Override
    public Optional<Venue> findById(UUID id) {
        return venueJpaRepository.findById(id).map(VenueConverter::entityToDomain);
    }

    @Override
    public Page<Venue> findAll(Pageable pageable) {
        return venueJpaRepository.findAll(pageable).map(VenueConverter::entityToDomain);
    }

    @Override
    public Page<Venue> search(String name, String city, String neighborhood, Pageable pageable) {
        return venueJpaRepository.searchVenues(name, city, neighborhood, pageable)
                .map(VenueConverter::entityToDomain);
    }

    @Override
    public void deleteById(UUID id) {
        venueJpaRepository.deleteById(id);
    }

}
