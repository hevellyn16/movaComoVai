package com.eng.software.mova.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "venues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VenueEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 50)
    private String number;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 255)
    private String street;

    @Column(nullable = false, length = 100)
    private String neighborhood;

    @Column(length = 255)
    private String landmark;

    @Column(name = "has_parking_lot", nullable = false)
    @Builder.Default
    private boolean hasParkingLot = false;

    @Column(name = "has_accessibility", nullable = false)
    @Builder.Default
    private boolean hasAccessibility = false;

    @Column(name = "has_bathroom", nullable = false)
    @Builder.Default
    private boolean hasBathroom = false;

    @Column(name = "has_foods_and_drinks", nullable = false)
    @Builder.Default
    private boolean hasFoodsAndDrinks = false;

    @OneToMany(mappedBy = "venue", fetch = FetchType.LAZY)
    @Builder.Default
    @EqualsAndHashCode.Exclude
    private Set<EventEntity> events = new HashSet<>();
}
