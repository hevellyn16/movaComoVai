package com.eng.software.mova.domain.port;

import com.eng.software.mova.domain.model.Event;
import com.eng.software.mova.infrastructure.persistence.entity.EventEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface EventRepositoryPort {
    Event save(Event event);
    Optional<Event> findById(UUID id);
    Page<Event> findAll(Pageable pageable);
    void deleteById(UUID id);

    Page<Event> findTodayEvents(LocalDateTime startOfDay, LocalDateTime endOfDay, Pageable pageable);
    Page<Event> findUpcomingEvents(LocalDateTime now, Pageable pageable);
    Page<Event> search(String q, LocalDateTime dateFrom, LocalDateTime dateTo, BigDecimal priceMin, BigDecimal priceMax, String neighborhood, Pageable pageable);
}