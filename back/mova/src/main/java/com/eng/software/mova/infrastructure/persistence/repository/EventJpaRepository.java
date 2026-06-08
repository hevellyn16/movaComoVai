package com.eng.software.mova.infrastructure.persistence.repository;

import com.eng.software.mova.infrastructure.persistence.entity.EventEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public interface EventJpaRepository extends JpaRepository<EventEntity, UUID> {

    Page<EventEntity> findByStartsAtBetween(LocalDateTime startOfDay, LocalDateTime endOfDay, Pageable pageable);

    Page<EventEntity> findByStartsAtGreaterThanEqual(LocalDateTime now, Pageable pageable);

    @Query("SELECT e FROM EventEntity e LEFT JOIN e.venue v WHERE " +
            "(:q IS NULL OR LOWER(e.eventName) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%', :q, '%'))) AND " +
            "(:dateFrom IS NULL OR e.startsAt >= :dateFrom) AND " +
            "(:dateTo IS NULL OR e.startsAt <= :dateTo) AND " +
            "(:priceMin IS NULL OR e.price >= :priceMin) AND " +
            "(:priceMax IS NULL OR e.price <= :priceMax) AND " +
            "(:neighborhood IS NULL OR LOWER(v.neighborhood) LIKE LOWER(CONCAT('%', :neighborhood, '%')))")
    Page<EventEntity> searchEvents(
            @Param("q") String q,
            @Param("dateFrom") LocalDateTime dateFrom,
            @Param("dateTo") LocalDateTime dateTo,
            @Param("priceMin") BigDecimal priceMin,
            @Param("priceMax") BigDecimal priceMax,
            @Param("neighborhood") String neighborhood,
            Pageable pageable);
}
