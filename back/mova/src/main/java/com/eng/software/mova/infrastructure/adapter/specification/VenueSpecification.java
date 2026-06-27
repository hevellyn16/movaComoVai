package com.eng.software.mova.infrastructure.adapter.specification;

import com.eng.software.mova.infrastructure.persistence.entity.VenueEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class VenueSpecification {

    public static Specification<VenueEntity> searchByText(String q) {
        return (root, query, cb) -> {
            if (q == null || q.isBlank()) {
                return cb.conjunction();
            }

            String pattern = "%" + q.toLowerCase() + "%";

            Predicate nameMatch = cb.like(cb.lower(root.get("name")), pattern);
            Predicate neighborhoodMatch = cb.like(cb.lower(root.get("neighborhood")), pattern);
            Predicate cityMatch = cb.like(cb.lower(root.get("city")), pattern);

            return cb.or(nameMatch, neighborhoodMatch, cityMatch);
        };
    }
}