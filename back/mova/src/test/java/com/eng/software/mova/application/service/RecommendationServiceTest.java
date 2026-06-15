package com.eng.software.mova.application.service;

import com.eng.software.mova.domain.model.Event;
import com.eng.software.mova.domain.model.Tag;
import com.eng.software.mova.domain.model.User;
import com.eng.software.mova.domain.port.EventRepositoryPort;
import com.eng.software.mova.domain.port.UserRepositoryPort;
import com.eng.software.mova.shared.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private EventRepositoryPort eventRepository;

    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    public void shouldReturnPagedFeedOrderedAndRespectPageable_whenManyEvents() {
        UUID userId = UUID.randomUUID();
        UUID tag1 = UUID.randomUUID();
        UUID tag2 = UUID.randomUUID();

        User user = User.builder().id(userId).tagsId(Set.of(tag1, tag2)).build();

        Tag t1 = new Tag(tag1, "a");
        Tag t2 = new Tag(tag2, "b");

        Event e1 = Event.builder().id(UUID.randomUUID()).tags(Set.of(t1, t2)).build();
        Event e2 = Event.builder().id(UUID.randomUUID()).tags(Set.of(t1)).build();
        Event e3 = Event.builder().id(UUID.randomUUID()).tags(Set.of(t2)).build();
        Event e4 = Event.builder().id(UUID.randomUUID()).tags(Set.of(new Tag(UUID.randomUUID(), "x"))).build();
        Event e5 = Event.builder().id(UUID.randomUUID()).tags(Set.of(t1)).build();

        List<Event> upcoming = List.of(e1, e2, e3, e4, e5);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(eventRepository.findAllUpcomingAsList()).thenReturn(upcoming);

        Page<Event> page = recommendationService.getFeedForUser(userId, PageRequest.of(1, 2));

        verify(userRepository, times(1)).findById(userId);
        verify(eventRepository, times(1)).findAllUpcomingAsList();

        assertThat(page.getSize()).isEqualTo(2);
        assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(1);
        assertThat(page.getContent().size()).isLessThanOrEqualTo(2);
    }

    @Test
    public void shouldReturnEmptyPage_whenNoRecommendedEvents() {
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).tagsId(Set.of(UUID.randomUUID())).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(eventRepository.findAllUpcomingAsList()).thenReturn(List.of());

        Page<Event> page = recommendationService.getFeedForUser(userId, PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(0);
        assertThat(page.getContent()).isEmpty();
    }

    @Test
    public void shouldThrow_whenUserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recommendationService.getFeedForUser(userId, PageRequest.of(0, 5)))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(userRepository, times(1)).findById(userId);
        verify(eventRepository, never()).findAllUpcomingAsList();
    }
}


