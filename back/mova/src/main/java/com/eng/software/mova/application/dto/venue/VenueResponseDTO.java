package com.eng.software.mova.application.dto.venue;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Dados de um local (resposta)")
public record VenueResponseDTO(
        @Schema(description = "UUID do local", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Nome do local", example = "Parque Ibirapuera")
        String name,

        @Schema(description = "Número", example = "S/N")
        String number,

        @Schema(description = "Cidade", example = "São Paulo")
        String city,

        @Schema(description = "Rua / Endereço", example = "Av. Pedro Álvares Cabral")
        String street,

        @Schema(description = "Bairro", example = "Vila Mariana")
        String neighborhood,

        @Schema(description = "Ponto de referência", example = "Próximo ao Obelisco")
        String landmark,

        @Schema(description = "Possui estacionamento", example = "true")
        boolean hasParkingLot,

        @Schema(description = "Possui acessibilidade", example = "true")
        boolean hasAccessibility,

        @Schema(description = "Possui banheiros", example = "true")
        boolean hasBathroom,

        @Schema(description = "Possui venda de comidas e bebidas", example = "true")
        boolean hasFoodsAndDrinks
) {
}
