import { venueService } from "@/services/venues.service";
import { Page } from "@/types/utils";
import { Venue, VenueSearchFilters, VenueCreateDTO, VenueUpdateDTO } from "@/types/venue.types";
import { useState, useCallback } from "react";

export const useVenues = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleRequest = async <T>(request: () => Promise<T>): Promise<T> => {
    setIsLoading(true);
    setError(null);
    try {
      return await request();
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || "Ocorreu um erro ao processar o local.";
      setError(errorMessage);
      throw err;
    } finally {
      setIsLoading(false);
    }
  };

  const fetchAllVenues = useCallback(async (page = 0, size = 100): Promise<Page<Venue>> => {
    return handleRequest(() => venueService.findAll(page, size));
  }, []);

  const fetchVenueById = async (id: string): Promise<Venue> => {
    return handleRequest(() => venueService.findById(id));
  };

  const searchVenues = async (filters: VenueSearchFilters, page = 0, size = 10): Promise<Page<Venue>> => {
    return handleRequest(() => venueService.search(filters, page, size));
  };

  const createVenue = async (data: VenueCreateDTO): Promise<Venue> => {
    return handleRequest(() => venueService.create(data));
  };

  const updateVenue = async (id: string, data: VenueUpdateDTO): Promise<Venue> => {
    return handleRequest(() => venueService.update(id, data));
  };

  const deleteVenue = async (id: string): Promise<void> => {
    return handleRequest(() => venueService.delete(id));
  };

  return {
    isLoading,
    error,
    clearError: () => setError(null),
    fetchAllVenues,
    fetchVenueById,
    searchVenues,
    createVenue,
    updateVenue,
    deleteVenue,
  };
};