import { eventService } from "@/services/events.service";
import { EventResponseDTO, EventSearchFilters, EventCreateDTO, EventUpdateDTO, Event } from "@/types/event.types";
import { useState, useCallback } from "react";
import { useAuth } from "./useAuth";

const mapDtoToEvent = (dto: EventResponseDTO, currentUserId?: string): Event => {
  return {
    id: dto.id,
    userId: dto.creatorId,
    eventName: dto.eventName,
    description: dto.description,
    contentRating: dto.contentRating,
    price: dto.price,
    startsAt: dto.startsAt,
    endsAt: dto.endsAt,
    venueId: dto.venueId,
    createdAt: dto.startsAt, 
    
    tags: dto.tags?.map((t) => ({ id: t, tagname: t })) || [],
    
    venue: dto.venue || {
      id: dto.venueId,
      name: dto.venueName,
      number: "",
      city: "",
      street: "",
      neighborhood: "",
      hasParkingLot: false,
      hasAccessibility: false,
      hasBathroom: false,
      hasFoodsAndDrinks: false,
    },
    
    likesCount: dto.likedByUserIds?.length || 0,
    isLiked: currentUserId ? dto.likedByUserIds?.includes(currentUserId) : false,
    
    // Valores default para campos não mapeados no DTO padrão
    isFavorited: currentUserId ? dto.favoritedByUserIds?.includes(currentUserId) : false,
    pictures: dto.pictures || [],
  };
};

export const useEvents = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { user } = useAuth(); 

  const handleRequest = async <T>(request: () => Promise<T>): Promise<T> => {
    setIsLoading(true);
    setError(null);
    try {
      return await request();
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || "Ocorreu um erro na requisição de eventos.";
      setError(errorMessage);
      throw err;
    } finally {
      setIsLoading(false);
    }
  };

  const fetchAllEvents = async (page = 0, size = 10): Promise<{ content: Event[]; totalPages: number }> => {
    return handleRequest(async () => {
      const data = await eventService.findAll(page, size);
      return {
        content: data.content.map((dto) => mapDtoToEvent(dto, user?.id)),
        totalPages: data.totalPages,
      };
    });
  };

  const fetchEventById = async (id: string): Promise<Event> => {
    return handleRequest(async () => {
      const dto = await eventService.findById(id);
      return mapDtoToEvent(dto, user?.id);
    });
  };

  const fetchTodayEvents = async (page = 0, size = 10): Promise<{ content: Event[]; totalPages: number }> => {
    return handleRequest(async () => {
      const data = await eventService.findToday(page, size);
      return {
        content: data.content.map((dto) => mapDtoToEvent(dto, user?.id)),
        totalPages: data.totalPages,
      };
    });
  };

  const fetchUpcomingEvents = async (page = 0, size = 10): Promise<{ content: Event[]; totalPages: number }> => {
    return handleRequest(async () => {
      const data = await eventService.findUpcoming(page, size);
      return {
        content: data.content.map((dto) => mapDtoToEvent(dto, user?.id)),
        totalPages: data.totalPages,
      };
    });
  };

  const searchEvents = async (filters: EventSearchFilters, page = 0, size = 10): Promise<{ content: Event[]; totalPages: number }> => {
    return handleRequest(async () => {
      const data = await eventService.search(filters, page, size);
      return {
        content: data.content.map((dto) => mapDtoToEvent(dto, user?.id)),
        totalPages: data.totalPages,
      };
    });
  };

  const toggleLike = async (eventId: string, currentlyLiked: boolean) => {
    return handleRequest(async () => {
      if (currentlyLiked) {
        await eventService.unlikeEvent(eventId);
      } else {
        await eventService.likeEvent(eventId);
      }
    });
  };

  const toggleFavorite = async (eventId: string, currentlyFavorited: boolean) => {
    return handleRequest(async () => {
      if (currentlyFavorited) {
        await eventService.removeFavorite(eventId);
      } else {
        await eventService.addFavorite(eventId);
      }
    });
  };

  const createEvent = async (data: EventCreateDTO): Promise<Event> => {
    return handleRequest(async () => {
      const newEventDto = await eventService.create(data);
      return mapDtoToEvent(newEventDto, user?.id);
    });
  };

  const updateEvent = async (id: string, data: EventUpdateDTO): Promise<Event> => {
    return handleRequest(async () => {
      const updatedDto = await eventService.update(id, data);
      return mapDtoToEvent(updatedDto, user?.id);
    });
  };

  const deleteEvent = async (id: string) => {
    return handleRequest(() => eventService.delete(id));
  };

  const uploadPicture = async (eventId: string, file: File) => {
    return handleRequest(() => eventService.uploadPicture(eventId, file));
  };

  const deletePicture = async (pictureId: string) => {
    return handleRequest(() => eventService.deletePicture(pictureId));
  };

  return {
    isLoading,
    error,
    clearError: () => setError(null),
    
    // Consultas
    fetchAllEvents,
    fetchEventById,
    fetchTodayEvents,
    fetchUpcomingEvents,
    searchEvents,
    
    // Interações
    toggleLike,
    toggleFavorite,
    
    // Gerenciamento
    createEvent,
    updateEvent,
    deleteEvent,
    uploadPicture,
    deletePicture,
  };
};