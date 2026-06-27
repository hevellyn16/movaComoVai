package com.eng.software.mova.factory;

import com.eng.software.mova.application.dto.venue.VenueCreateDTO;
import com.eng.software.mova.application.dto.venue.VenueUpdateDTO;
import com.eng.software.mova.domain.model.Venue;

import java.util.UUID;

/**
 * Factory para criação de objetos de teste relacionados a Venue.
 * Centraliza a construção de entidades de domínio e DTOs,
 * garantindo dados consistentes e reutilizáveis em toda a suíte de testes.
 */
public final class VenueFactory {

    // ======================== IDs PADRÃO ========================

    public static final UUID DEFAULT_ID = UUID.fromString("b2c3d4e5-f6a7-8901-bcde-f12345678901");

    // ======================== CONSTANTES ========================

    public static final String DEFAULT_NAME = "Parque Ibirapuera";
    public static final String DEFAULT_NUMBER = "S/N";
    public static final String DEFAULT_CITY = "São Paulo";
    public static final String DEFAULT_STREET = "Av. Pedro Álvares Cabral";
    public static final String DEFAULT_NEIGHBORHOOD = "Vila Mariana";
    public static final String DEFAULT_LANDMARK = "Próximo ao Obelisco";

    private VenueFactory() {
        // Utility class — impede instanciação
    }

    // ======================== DOMAIN MODEL ========================

    /**
     * Cria um Venue de domínio completo com todas as comodidades habilitadas.
     */
    public static Venue createDefaultVenue() {
        return Venue.builder()
                .id(DEFAULT_ID)
                .name(DEFAULT_NAME)
                .number(DEFAULT_NUMBER)
                .city(DEFAULT_CITY)
                .street(DEFAULT_STREET)
                .neighborhood(DEFAULT_NEIGHBORHOOD)
                .landmark(DEFAULT_LANDMARK)
                .hasParkingLot(true)
                .hasAccessibility(true)
                .hasBathroom(true)
                .hasFoodsAndDrinks(true)
                .build();
    }

    /**
     * Cria um Venue mínimo — sem comodidades.
     */
    public static Venue createMinimalVenue() {
        return Venue.builder()
                .id(DEFAULT_ID)
                .name("Praça da Sé")
                .number("1")
                .city("São Paulo")
                .street("Praça da Sé")
                .neighborhood("Sé")
                .landmark(null)
                .hasParkingLot(false)
                .hasAccessibility(false)
                .hasBathroom(false)
                .hasFoodsAndDrinks(false)
                .build();
    }

    /**
     * Cria um segundo Venue com id diferente.
     */
    public static Venue createSecondVenue() {
        return Venue.builder()
                .id(UUID.randomUUID())
                .name("Allianz Parque")
                .number("200")
                .city("São Paulo")
                .street("Av. Francisco Matarazzo")
                .neighborhood("Água Branca")
                .landmark("Próximo à estação Palmeiras-Barra Funda")
                .hasParkingLot(true)
                .hasAccessibility(true)
                .hasBathroom(true)
                .hasFoodsAndDrinks(true)
                .build();
    }

    // ======================== CREATE DTO ========================

    /**
     * Cria um VenueCreateDTO padrão válido.
     */
    public static VenueCreateDTO createDefaultVenueCreateDTO() {
        return new VenueCreateDTO(
                DEFAULT_NAME,
                DEFAULT_NUMBER,
                DEFAULT_CITY,
                DEFAULT_STREET,
                DEFAULT_NEIGHBORHOOD,
                DEFAULT_LANDMARK,
                true,
                true,
                true,
                true
        );
    }

    /**
     * Cria um VenueCreateDTO mínimo — sem comodidades e sem landmark.
     */
    public static VenueCreateDTO createMinimalVenueCreateDTO() {
        return new VenueCreateDTO(
                "Praça da Sé",
                "1",
                "São Paulo",
                "Praça da Sé",
                "Sé",
                null,
                false,
                false,
                false,
                false
        );
    }

    // ======================== UPDATE DTO ========================

    /**
     * Cria um VenueUpdateDTO com todos os campos preenchidos.
     */
    public static VenueUpdateDTO createFullVenueUpdateDTO() {
        return new VenueUpdateDTO(
                "Allianz Parque",
                "200",
                "São Paulo",
                "Av. Francisco Matarazzo",
                "Água Branca",
                "Próximo à estação Palmeiras-Barra Funda",
                true,
                true,
                true,
                true
        );
    }

    /**
     * Cria um VenueUpdateDTO parcial — apenas nome.
     */
    public static VenueUpdateDTO createNameOnlyUpdateDTO(String name) {
        return new VenueUpdateDTO(name, null, null, null, null, null, null, null, null, null);
    }

    /**
     * Cria um VenueUpdateDTO vazio (nenhum campo alterado).
     */
    public static VenueUpdateDTO createEmptyUpdateDTO() {
        return new VenueUpdateDTO(null, null, null, null, null, null, null, null, null, null);
    }

    /**
     * Cria um VenueUpdateDTO apenas com alterações booleanas.
     */
    public static VenueUpdateDTO createBooleanOnlyUpdateDTO(
            Boolean hasParkingLot, Boolean hasAccessibility,
            Boolean hasBathroom, Boolean hasFoodsAndDrinks) {
        return new VenueUpdateDTO(null, null, null, null, null, null,
                hasParkingLot, hasAccessibility, hasBathroom, hasFoodsAndDrinks);
    }
}
