package com.eng.software.mova.application.service;

import com.eng.software.mova.application.dto.venue.VenueCreateDTO;
import com.eng.software.mova.application.dto.venue.VenueUpdateDTO;
import com.eng.software.mova.domain.model.Venue;
import com.eng.software.mova.domain.port.VenueRepositoryPort;
import com.eng.software.mova.factory.VenueFactory;
import com.eng.software.mova.shared.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;

/**
 * Testes unitários do {@link VenueService}.
 * <p>
 * Utiliza Mockito com BDD style (given/when/then) e AssertJ para asserções fluidas.
 * Cada método público do service possui um {@link Nested} group próprio.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("VenueService — Testes Unitários")
class VenueServiceTest {

    @Mock
    private VenueRepositoryPort venueRepositoryPort;

    @InjectMocks
    private VenueService venueService;

    @Captor
    private ArgumentCaptor<Venue> venueCaptor;

    // ======================== create ========================

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("Deve criar um venue completo com sucesso")
        void shouldCreateFullVenueSuccessfully() {
            // given
            VenueCreateDTO dto = VenueFactory.createDefaultVenueCreateDTO();

            given(venueRepositoryPort.save(any(Venue.class))).willAnswer(inv -> {
                Venue saved = inv.getArgument(0);
                saved.setId(VenueFactory.DEFAULT_ID);
                return saved;
            });

            // when
            Venue result = venueService.create(dto);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(VenueFactory.DEFAULT_ID);
            assertThat(result.getName()).isEqualTo(dto.name());
            assertThat(result.getNumber()).isEqualTo(dto.number());
            assertThat(result.getCity()).isEqualTo(dto.city());
            assertThat(result.getStreet()).isEqualTo(dto.street());
            assertThat(result.getNeighborhood()).isEqualTo(dto.neighborhood());
            assertThat(result.getLandmark()).isEqualTo(dto.landmark());
            assertThat(result.isHasParkingLot()).isEqualTo(dto.hasParkingLot());
            assertThat(result.isHasAccessibility()).isEqualTo(dto.hasAccessibility());
            assertThat(result.isHasBathroom()).isEqualTo(dto.hasBathroom());
            assertThat(result.isHasFoodsAndDrinks()).isEqualTo(dto.hasFoodsAndDrinks());

            then(venueRepositoryPort).should().save(any(Venue.class));
        }

        @Test
        @DisplayName("Deve criar um venue mínimo (sem comodidades e sem landmark)")
        void shouldCreateMinimalVenueSuccessfully() {
            // given
            VenueCreateDTO dto = VenueFactory.createMinimalVenueCreateDTO();

            given(venueRepositoryPort.save(any(Venue.class))).willAnswer(inv -> {
                Venue saved = inv.getArgument(0);
                saved.setId(UUID.randomUUID());
                return saved;
            });

            // when
            Venue result = venueService.create(dto);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getLandmark()).isNull();
            assertThat(result.isHasParkingLot()).isFalse();
            assertThat(result.isHasAccessibility()).isFalse();
            assertThat(result.isHasBathroom()).isFalse();
            assertThat(result.isHasFoodsAndDrinks()).isFalse();
        }
    }

    // ======================== findAll ========================

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("Deve retornar página de venues")
        void shouldReturnPageOfVenues() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            Venue venue1 = VenueFactory.createDefaultVenue();
            Venue venue2 = VenueFactory.createSecondVenue();

            Page<Venue> venuePage = new PageImpl<>(List.of(venue1, venue2), pageable, 2);

            given(venueRepositoryPort.findAll(pageable)).willReturn(venuePage);

            // when
            Page<Venue> result = venueService.findAll(pageable);

            // then
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getTotalElements()).isEqualTo(2);

            then(venueRepositoryPort).should().findAll(pageable);
        }

        @Test
        @DisplayName("Deve retornar página vazia quando não há venues")
        void shouldReturnEmptyPageWhenNoVenues() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Venue> emptyPage = new PageImpl<>(List.of(), pageable, 0);

            given(venueRepositoryPort.findAll(pageable)).willReturn(emptyPage);

            // when
            Page<Venue> result = venueService.findAll(pageable);

            // then
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isZero();
        }
    }

    // ======================== findById ========================

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("Deve retornar o venue quando ele existe")
        void shouldReturnVenueWhenExists() {
            // given
            Venue venue = VenueFactory.createDefaultVenue();
            given(venueRepositoryPort.findById(VenueFactory.DEFAULT_ID)).willReturn(Optional.of(venue));

            // when
            Venue result = venueService.findById(VenueFactory.DEFAULT_ID);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(VenueFactory.DEFAULT_ID);
            assertThat(result.getName()).isEqualTo(VenueFactory.DEFAULT_NAME);

            then(venueRepositoryPort).should().findById(VenueFactory.DEFAULT_ID);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando o venue não existe")
        void shouldThrowExceptionWhenVenueNotFound() {
            // given
            UUID id = UUID.randomUUID();
            given(venueRepositoryPort.findById(id)).willReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> venueService.findById(id))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Venue not found with id: " + id);
        }
    }

    // ======================== search ========================

    @Nested
    @DisplayName("search")
    class Search {

        @Test
        @DisplayName("Deve buscar venues por filtros")
        void shouldSearchVenuesByFilters() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            Venue venue = VenueFactory.createDefaultVenue();
            Page<Venue> page = new PageImpl<>(List.of(venue), pageable, 1);

            String name = "Parque";
            String city = "São Paulo";
            String neighborhood = "Vila Mariana";

            given(venueRepositoryPort.search(eq(name), eq(pageable)))
                    .willReturn(page);

            // when
            Page<Venue> result = venueService.search(name, city, neighborhood, pageable);

            // then
            assertThat(result.getContent()).hasSize(1);
            then(venueRepositoryPort).should().search(eq(name), eq(pageable));
        }

        @Test
        @DisplayName("Deve buscar venues sem filtros (todos null)")
        void shouldSearchWithoutFilters() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Venue> emptyPage = new PageImpl<>(List.of(), pageable, 0);

            given(venueRepositoryPort.search(null, pageable))
                    .willReturn(emptyPage);

            // when
            Page<Venue> result = venueService.search(null, null, null, pageable);

            // then
            assertThat(result.getContent()).isEmpty();
        }
    }

    // ======================== update ========================

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("Deve atualizar todos os campos fornecidos")
        void shouldUpdateAllProvidedFields() {
            // given
            Venue existing = VenueFactory.createDefaultVenue();
            VenueUpdateDTO dto = VenueFactory.createFullVenueUpdateDTO();

            given(venueRepositoryPort.findById(VenueFactory.DEFAULT_ID)).willReturn(Optional.of(existing));
            given(venueRepositoryPort.save(any(Venue.class))).willAnswer(inv -> inv.getArgument(0));

            // when
            Venue result = venueService.update(VenueFactory.DEFAULT_ID, dto);

            // then
            assertThat(result.getName()).isEqualTo(dto.name());
            assertThat(result.getNumber()).isEqualTo(dto.number());
            assertThat(result.getCity()).isEqualTo(dto.city());
            assertThat(result.getStreet()).isEqualTo(dto.street());
            assertThat(result.getNeighborhood()).isEqualTo(dto.neighborhood());
            assertThat(result.getLandmark()).isEqualTo(dto.landmark());
            assertThat(result.isHasParkingLot()).isEqualTo(dto.hasParkingLot());
            assertThat(result.isHasAccessibility()).isEqualTo(dto.hasAccessibility());
            assertThat(result.isHasBathroom()).isEqualTo(dto.hasBathroom());
            assertThat(result.isHasFoodsAndDrinks()).isEqualTo(dto.hasFoodsAndDrinks());

            then(venueRepositoryPort).should().save(any(Venue.class));
        }

        @Test
        @DisplayName("Deve atualizar apenas o nome")
        void shouldUpdateOnlyName() {
            // given
            Venue existing = VenueFactory.createDefaultVenue();
            VenueUpdateDTO dto = VenueFactory.createNameOnlyUpdateDTO("Novo Parque");

            given(venueRepositoryPort.findById(VenueFactory.DEFAULT_ID)).willReturn(Optional.of(existing));
            given(venueRepositoryPort.save(any(Venue.class))).willAnswer(inv -> inv.getArgument(0));

            // when
            Venue result = venueService.update(VenueFactory.DEFAULT_ID, dto);

            // then
            assertThat(result.getName()).isEqualTo("Novo Parque");
            // Demais campos devem permanecer originais
            assertThat(result.getCity()).isEqualTo(VenueFactory.DEFAULT_CITY);
        }

        @Test
        @DisplayName("Não deve alterar nada se o DTO estiver vazio")
        void shouldNotChangeAnythingIfDtoIsEmpty() {
            // given
            Venue existing = VenueFactory.createDefaultVenue();
            VenueUpdateDTO dto = VenueFactory.createEmptyUpdateDTO();

            given(venueRepositoryPort.findById(VenueFactory.DEFAULT_ID)).willReturn(Optional.of(existing));
            given(venueRepositoryPort.save(any(Venue.class))).willAnswer(inv -> inv.getArgument(0));

            // when
            Venue result = venueService.update(VenueFactory.DEFAULT_ID, dto);

            // then
            assertThat(result.getName()).isEqualTo(VenueFactory.DEFAULT_NAME);
            assertThat(result.getCity()).isEqualTo(VenueFactory.DEFAULT_CITY);
        }

        @Test
        @DisplayName("Deve atualizar apenas campos booleanos")
        void shouldUpdateOnlyBooleanFields() {
            // given
            Venue existing = VenueFactory.createMinimalVenue(); // tudo false
            VenueUpdateDTO dto = VenueFactory.createBooleanOnlyUpdateDTO(true, true, true, true);

            given(venueRepositoryPort.findById(VenueFactory.DEFAULT_ID)).willReturn(Optional.of(existing));
            given(venueRepositoryPort.save(any(Venue.class))).willAnswer(inv -> inv.getArgument(0));

            // when
            Venue result = venueService.update(VenueFactory.DEFAULT_ID, dto);

            // then
            assertThat(result.isHasParkingLot()).isTrue();
            assertThat(result.isHasAccessibility()).isTrue();
            assertThat(result.isHasBathroom()).isTrue();
            assertThat(result.isHasFoodsAndDrinks()).isTrue();
            // Demais campos não alteram
            assertThat(result.getName()).isEqualTo("Praça da Sé");
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando o venue não existe")
        void shouldThrowExceptionWhenVenueNotFound() {
            // given
            UUID id = UUID.randomUUID();
            VenueUpdateDTO dto = VenueFactory.createNameOnlyUpdateDTO("Teste");

            given(venueRepositoryPort.findById(id)).willReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> venueService.update(id, dto))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Venue not found with id: " + id);

            then(venueRepositoryPort).should(never()).save(any());
        }
    }

    // ======================== delete ========================

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("Deve deletar o venue quando ele existe")
        void shouldDeleteVenueWhenExists() {
            // given
            Venue venue = VenueFactory.createDefaultVenue();
            given(venueRepositoryPort.findById(VenueFactory.DEFAULT_ID)).willReturn(Optional.of(venue));

            // when
            venueService.delete(VenueFactory.DEFAULT_ID);

            // then
            then(venueRepositoryPort).should().deleteById(VenueFactory.DEFAULT_ID);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando o venue não existe")
        void shouldThrowExceptionWhenVenueNotFound() {
            // given
            UUID id = UUID.randomUUID();
            given(venueRepositoryPort.findById(id)).willReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> venueService.delete(id))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Venue not found with id: " + id);

            then(venueRepositoryPort).should(never()).deleteById(any());
        }
    }
}
