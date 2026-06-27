package com.eng.software.mova.domain.model;

public record EventScore(Event event, int score) implements Comparable<EventScore> {

    @Override
    public int compareTo(EventScore other) {
        return Integer.compare(other.score, this.score);
    }
}
