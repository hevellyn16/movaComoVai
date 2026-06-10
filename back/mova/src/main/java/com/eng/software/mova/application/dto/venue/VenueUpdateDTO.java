package com.eng.software.mova.application.dto.venue;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para atualização de um local. Todos os campos são opcionais.")
public record VenueUpdateDTO(
        @Schema(description = "Nome do local", example = "Allianz Parque")
        String name,

        @Schema(description = "Número", example = "200")
        String number,

        @Schema(description = "Cidade", example = "São Paulo")
        String city,

        @Schema(description = "Rua / Endereço", example = "Av. Francisco Matarazzo")
        String street,

        @Schema(description = "Bairro", example = "Água Branca")
        String neighborhood,

        @Schema(description = "Ponto de referência", example = "Próximo à estação Palmeiras-Barra Funda")
        String landmark,

        @Schema(description = "Possui estacionamento", example = "true")
        Boolean hasParkingLot,

        @Schema(description = "Possui acessibilidade", example = "true")
        Boolean hasAccessibility,

        @Schema(description = "Possui banheiros", example = "true")
        Boolean hasBathroom,

        @Schema(description = "Possui venda de comidas e bebidas", example = "true")
        Boolean hasFoodsAndDrinks
) {
}
