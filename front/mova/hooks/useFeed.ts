import { recommendationService } from "@/services/feed.service";
import { EventResponseDTO } from "@/types/event.types";
import { Page } from "@/types/utils";
import { useState, useEffect, useCallback } from "react";

export const useFeed = () => {
  const [feed, setFeed] = useState<Page<EventResponseDTO> | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchFeed = useCallback(async (page = 0, size = 10) => {
    setIsLoading(true);
    setError(null);
    try {
      const data = await recommendationService.getFeed(page, size);
      setFeed(data);
    } catch (err: any) {
      setError(err.response?.data?.message || "Erro ao carregar o feed de eventos.");
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  }, []);

  return {
    feed,
    isLoading,
    error,
    fetchFeed,
  };
};