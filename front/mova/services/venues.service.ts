import { Page } from "@/types/utils";
import { VenueResponseDTO, VenueSearchFilters, VenueCreateDTO, VenueUpdateDTO } from "@/types/venue.types";
import api from "./api";

export const venueService = {

  findAll: async (page = 0, size = 10): Promise<Page<VenueResponseDTO>> => {
    const response = await api.get<Page<VenueResponseDTO>>("/venues", {
      params: { page, size },
    });
    return response.data;
  },

  findById: async (id: string): Promise<VenueResponseDTO> => {
    const response = await api.get<VenueResponseDTO>(`/venues/${id}`);
    return response.data;
  },

  search: async (
    filters: VenueSearchFilters,
    page = 0,
    size = 10
  ): Promise<Page<VenueResponseDTO>> => {
    const response = await api.get<Page<VenueResponseDTO>>("/venues/search", {
      params: { ...filters, page, size },
    });
    return response.data;
  },

  create: async (data: VenueCreateDTO): Promise<VenueResponseDTO> => {
    const response = await api.post<VenueResponseDTO>("/venues", data);
    return response.data;
  },

  update: async (id: string, data: VenueUpdateDTO): Promise<VenueResponseDTO> => {
    const response = await api.put<VenueResponseDTO>(`/venues/${id}`, data);
    return response.data;
  },

  delete: async (id: string): Promise<void> => {
    await api.delete(`/venues/${id}`);
  },
};