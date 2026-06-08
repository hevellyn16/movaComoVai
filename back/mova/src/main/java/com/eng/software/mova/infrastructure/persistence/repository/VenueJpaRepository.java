package com.eng.software.mova.infrastructure.persistence.repository;

import com.eng.software.mova.infrastructure.persistence.entity.VenueEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface VenueJpaRepository extends JpaRepository<VenueEntity, UUID> {

    @Query("SELECT v FROM VenueEntity v WHERE " +
            "(:name IS NULL OR LOWER(v.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:city IS NULL OR LOWER(v.city) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
            "(:neighborhood IS NULL OR LOWER(v.neighborhood) LIKE LOWER(CONCAT('%', :neighborhood, '%')))")
    Page<VenueEntity> searchVenues(
            @Param("name") String name,
            @Param("city") String city,
            @Param("neighborhood") String neighborhood,
            Pageable pageable);

}
