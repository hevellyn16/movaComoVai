package com.eng.software.mova.domain.port;

import com.eng.software.mova.domain.model.Tag;
import com.eng.software.mova.domain.model.Venue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface VenueRepositoryPort {
    Venue save(Venue venue);
    Optional<Venue> findById(UUID id);
    Page<Venue> findAll(Pageable pageable);
    Page<Venue> search(String q, Pageable pageable);
    void deleteById(UUID id);
}
