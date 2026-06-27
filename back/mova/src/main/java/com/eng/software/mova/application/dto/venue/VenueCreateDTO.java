package com.eng.software.mova.application.dto.venue;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Dados para criação de um novo local (venue)")
public record VenueCreateDTO(
        @NotBlank
        @Schema(description = "Nome do local", example = "Parque Ibirapuera", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        @NotBlank
        @Schema(description = "Número ou identificação do local", example = "S/N", requiredMode = Schema.RequiredMode.REQUIRED)
        String number,

        @NotBlank
        @Schema(description = "Cidade", example = "São Paulo", requiredMode = Schema.RequiredMode.REQUIRED)
        String city,

        @NotBlank
        @Schema(description = "Rua / Endereço", example = "Av. Pedro Álvares Cabral", requiredMode = Schema.RequiredMode.REQUIRED)
        String street,

        @NotBlank
        @Schema(description = "Bairro", example = "Vila Mariana", requiredMode = Schema.RequiredMode.REQUIRED)
        String neighborhood,

        @Schema(description = "Ponto de referência", example = "Próximo ao Obelisco")
        String landmark,

        @Schema(description = "Possui estacionamento", example = "true")
        boolean hasParkingLot,

        @Schema(description = "Possui acessibilidade (rampas, elevadores)", example = "true")
        boolean hasAccessibility,

        @Schema(description = "Possui banheiros", example = "true")
        boolean hasBathroom,

        @Schema(description = "Possui venda de comidas e bebidas", example = "true")
        boolean hasFoodsAndDrinks
) {
}
