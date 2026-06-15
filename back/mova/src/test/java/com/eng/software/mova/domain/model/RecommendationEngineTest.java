package com.eng.software.mova.domain.model;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class RecommendationEngineTest {

    @Test
    public void shouldOrderByScoreAndFilterZero_whenEventsHaveDifferentCommonTags() {
        UUID tagId1 = UUID.randomUUID();
        UUID tagId2 = UUID.randomUUID();

        Tag tag1 = new Tag(tagId1, "tag1");
        Tag tag2 = new Tag(tagId2, "tag2");

        User user = User.builder()
                .id(UUID.randomUUID())
                .tagsId(Set.of(tagId1, tagId2))
                .build();

        Event eventWithTwoTags = Event.builder()
                .id(UUID.randomUUID())
                .tags(Set.of(tag1, tag2))
                .build();

        Event eventWithOneTag = Event.builder()
                .id(UUID.randomUUID())
                .tags(Set.of(tag1))
                .build();

        Event eventWithNoCommonTags = Event.builder()
                .id(UUID.randomUUID())
                .tags(Set.of(new Tag(UUID.randomUUID(), "other")))
                .build();

        List<Event> input = List.of(eventWithOneTag, eventWithNoCommonTags, eventWithTwoTags);

        List<Event> feed = RecommendationEngine.generateFeed(user, input);

        assertThat(feed).hasSize(2);

        assertThat(feed.get(0).getId()).isEqualTo(eventWithTwoTags.getId());

        assertThat(feed.get(1).getId()).isEqualTo(eventWithOneTag.getId());
    }

    @Test
    public void shouldReturnEmptyList_whenNoAvailableEvents() {
        User user = User.builder().id(UUID.randomUUID()).tagsId(Set.of()).build();

        List<Event> feed = RecommendationEngine.generateFeed(user, List.of());

        assertThat(feed).isEmpty();
    }

    @Test
    public void shouldFilterOutEventsWithZeroScore_whenNoCommonTags() {
        User user = User.builder().id(UUID.randomUUID()).tagsId(Set.of(UUID.randomUUID())).build();

        Event event = Event.builder()
                .id(UUID.randomUUID())
                .tags(Set.of(new Tag(UUID.randomUUID(), "x")))
                .build();

        List<Event> feed = RecommendationEngine.generateFeed(user, List.of(event));

        assertThat(feed).isEmpty();
    }
}

