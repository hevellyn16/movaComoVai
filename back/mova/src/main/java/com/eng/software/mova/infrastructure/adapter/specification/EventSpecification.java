package com.eng.software.mova.infrastructure.adapter.specification;

import com.eng.software.mova.infrastructure.persistence.entity.EventEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventSpecification {

    public static Specification<EventEntity> searchFilters(
            String q, LocalDateTime dateFrom, LocalDateTime dateTo,
            BigDecimal priceMin, BigDecimal priceMax, String neighborhood) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (q != null && !q.isBlank()) {
                String likePattern = "%" + q.toLowerCase() + "%";
                Predicate nameMatch = cb.like(cb.lower(root.get("eventName")), likePattern);
                Predicate descMatch = cb.like(cb.lower(root.get("description")), likePattern);
                predicates.add(cb.or(nameMatch, descMatch));
            }

            if (dateFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("startsAt"), dateFrom));
            }
            if (dateTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("startsAt"), dateTo));
            }

            if (priceMin != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), priceMin));
            }
            if (priceMax != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), priceMax));
            }

            if (neighborhood != null && !neighborhood.isBlank()) {
                Join<Object, Object> venueJoin = root.join("venue", JoinType.LEFT);
                predicates.add(cb.like(cb.lower(venueJoin.get("neighborhood")), "%" + neighborhood.toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
