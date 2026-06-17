package com.eng.software.mova.infrastructure.persistence.repository;

import com.eng.software.mova.domain.model.Venue;
import com.eng.software.mova.domain.port.VenueRepositoryPort;
import com.eng.software.mova.infrastructure.adapter.specification.VenueSpecification;
import com.eng.software.mova.infrastructure.persistence.entity.VenueEntity;
import com.eng.software.mova.shared.utils.VenueConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    public Page<Venue> search(String q, Pageable pageable) {
        Specification<VenueEntity> spec = VenueSpecification.searchByText(q);

        return venueJpaRepository.findAll(spec, pageable)
                .map(VenueConverter::entityToDomain);
    }

    @Override
    public void deleteById(UUID id) {
        venueJpaRepository.deleteById(id);
    }

}
