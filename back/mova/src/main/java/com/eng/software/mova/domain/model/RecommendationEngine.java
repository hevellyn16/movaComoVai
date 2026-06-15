package com.eng.software.mova.domain.model;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class RecommendationEngine {

    public static List<Event> generateFeed(User user, List<Event> availableEvents) {
        return availableEvents.stream()
                .map(event -> new EventScore(event, calculateScore(user, event)))
                .filter(eventScore -> eventScore.score() > 0)
                .sorted()
                .map(EventScore::event)
                .collect(Collectors.toList());
    }

    private static int calculateScore(User user, Event event) {
        int score = 0;

        Set<UUID> userTagIds = user.getTagsId();
        Set<Tag> eventTags = event.getTags();

        long commonTags = eventTags.stream()
                .filter(tag -> userTagIds.contains(tag.getId()))
                .count();

        score += (int) (commonTags * 15);

        if (event.getLikedByUsers() != null && event.getLikedByUsers().size() > 50) {
            score += 10;
        }

        return score;
    }
}