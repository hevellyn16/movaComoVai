import { EventResponseDTO, EventSearchFilters, EventCreateDTO, EventUpdateDTO, EventScheduleCreateDTO, EventScheduleUpdateDTO } from "@/types/event.types";
import { Page } from "@/types/utils";
import api from "./api";

export const eventService = {

  findAll: async (page = 0, size = 10): Promise<Page<EventResponseDTO>> => {
    const response = await api.get<Page<EventResponseDTO>>("/events", {
      params: { page, size },
    });
    return response.data;
  },

  findById: async (id: string): Promise<EventResponseDTO> => {
    const response = await api.get<EventResponseDTO>(`/events/${id}`);
    return response.data;
  },

  findToday: async (page = 0, size = 10): Promise<Page<EventResponseDTO>> => {
    const response = await api.get<Page<EventResponseDTO>>("/events/today", {
      params: { page, size },
    });
    return response.data;
  },

  findUpcoming: async (page = 0, size = 10): Promise<Page<EventResponseDTO>> => {
    const response = await api.get<Page<EventResponseDTO>>("/events/upcoming", {
      params: { page, size },
    });
    return response.data;
  },

  search: async (
    filters: EventSearchFilters,
    page = 0,
    size = 10
  ): Promise<Page<EventResponseDTO>> => {
    const response = await api.get<Page<EventResponseDTO>>("/events/search", {
      params: { ...filters, page, size },
    });
    return response.data;
  },

  likeEvent: async (eventId: string): Promise<void> => {
    await api.post(`/events/${eventId}/likes`);
  },

  unlikeEvent: async (eventId: string): Promise<void> => {
    await api.delete(`/events/${eventId}/likes`);
  },

  addFavorite: async (eventId: string): Promise<void> => {
    await api.post(`/events/${eventId}/favorites`);
  },

  removeFavorite: async (eventId: string): Promise<void> => {
    await api.delete(`/events/${eventId}/favorites`);
  },

  create: async (data: EventCreateDTO): Promise<EventResponseDTO> => {
    const response = await api.post<EventResponseDTO>("/events", data);
    return response.data;
  },

  update: async (id: string, data: EventUpdateDTO): Promise<EventResponseDTO> => {
    const response = await api.put<EventResponseDTO>(`/events/${id}`, data);
    return response.data;
  },

  delete: async (id: string): Promise<void> => {
    await api.delete(`/events/${id}`);
  },

  addTagsToEvent: async (eventId: string, tagIds: string[]): Promise<void> => {
    await api.post(`/events/${eventId}/tags`, { tagIds });
  },

  removeTagFromEvent: async (eventId: string, tagId: string): Promise<void> => {
    await api.delete(`/events/${eventId}/tags/${tagId}`);
  },

  addPictureToEvent: async (eventId: string, pictureUrl: string): Promise<void> => {
    await api.post(`/events/${eventId}/pictures`, { pictureUrl });
  },

  removePictureFromEvent: async (eventId: string, pictureId: string): Promise<void> => {
    await api.delete(`/events/${eventId}/pictures/${pictureId}`);
  },

  addScheduleToEvent: async (eventId: string, data: EventScheduleCreateDTO): Promise<void> => {
    await api.post(`/events/${eventId}/schedules`, data);
  },

  updateSchedule: async (
    eventId: string,
    scheduleId: string,
    data: EventScheduleUpdateDTO
  ): Promise<void> => {
    await api.put(`/events/${eventId}/schedules/${scheduleId}`, data);
  },

  removeScheduleFromEvent: async (eventId: string, scheduleId: string): Promise<void> => {
    await api.delete(`/events/${eventId}/schedules/${scheduleId}`);
  },
};