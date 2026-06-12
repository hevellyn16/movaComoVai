package com.eng.software.mova.infrastructure.persistence.repository;

import com.eng.software.mova.domain.model.Event;
import com.eng.software.mova.domain.port.EventRepositoryPort;
import com.eng.software.mova.infrastructure.adapter.specification.EventSpecification;
import com.eng.software.mova.infrastructure.persistence.entity.EventEntity;
import com.eng.software.mova.shared.utils.EventConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class EventRepository implements EventRepositoryPort {

    private final EventJpaRepository jpaRepository;

    @Override
    public Event save(Event event) {
        return EventConverter.entityToDomain(jpaRepository.save(EventConverter.domainToEntity(event)));
    }

    @Override
    public Optional<Event> findById(UUID id) {
        return jpaRepository.findById(id).map(EventConverter::entityToDomain);
    }

    @Override
    public Page<Event> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable).map(EventConverter::entityToDomain);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<Event> findAllUpcomingAsList() {
        return jpaRepository.findByStartsAtGreaterThanEqual(LocalDateTime.now())
                .stream()
                .map(EventConverter::entityToDomain)
                .toList();
    }

    @Override
    public Page<Event> findTodayEvents(LocalDateTime startOfDay, LocalDateTime endOfDay, Pageable pageable) {
        return jpaRepository.findByStartsAtBetween(startOfDay, endOfDay, pageable).map(EventConverter::entityToDomain);
    }

    @Override
    public Page<Event> findUpcomingEvents(LocalDateTime now, Pageable pageable) {
        return jpaRepository.findByStartsAtGreaterThanEqual(now, pageable).map(EventConverter::entityToDomain);
    }

    @Override
    public Page<Event> search(String q, LocalDateTime dateFrom, LocalDateTime dateTo, BigDecimal priceMin,
                              BigDecimal priceMax, String neighborhood, Pageable pageable) {

        Specification<EventEntity> spec = EventSpecification.searchFilters(q, dateFrom, dateTo, priceMin, priceMax, neighborhood);

        return jpaRepository.findAll(spec, pageable).map(EventConverter::entityToDomain);
    }
}