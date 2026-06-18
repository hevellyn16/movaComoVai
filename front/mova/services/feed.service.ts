import { Page } from "@/types/utils";
import api from "./api";
import {EventResponseDTO, EventScheduleResponseDTO} from "@/types/event.types";

export const recommendationService = {
  getFeed: async (page = 0, size = 10): Promise<Page<EventResponseDTO>> => {
    const response = await api.get<Page<EventResponseDTO>>("/recommendations/feed", {
      params: { 
        page, 
        size 
      },
    });
    return response.data;
  },
};