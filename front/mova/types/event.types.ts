import { User } from "./user.types";
import { Venue } from "./venue.types";

export interface Event {
  id: string;
  userId: string;
  eventName: string;
  description: string;
  contentRating: string;
  price: number;
  startsAt: string;
  endsAt: string;
  venueId: string;
  createdAt: string;
  updatedAt?: string;
  // Relações opcionais (vindas do backend junto com o evento)
  pictures?: EventPicture[];
  tags?: Tag[];
  venue?: Venue;
  likesCount?: number;
  isFavorited?: boolean;
  isLiked?: boolean;
}

export interface EventPicture {
  id: string;
  pictureUrl: string;
  eventId: string;
}

export interface Tag {
  id: string;
  tagname: string;
}

export interface Comment {
  id: string;
  userId: string;
  userName?: string;
  userAvatarUrl?: string;
  eventId: string;
  content: string;
  createdAt: string;
  updatedAt?: string;
  pictures?: CommentPicture[];
  user?: User;
}

export interface CommentPicture {
  id: string;
  commentId: string;
  pictureUrl: string;
}

export interface EventResponseDTO {
  id: string;
  eventName: string;
  description: string;
  contentRating: string;
  price: number;
  startsAt: string;
  endsAt: string;
  creatorId: string;
  venueId: string;
  venueName: string;
  venue?: Venue;
  tags: string[];
  schedules: EventScheduleResponseDTO[];
  pictures?: EventPicture[];
  likedByUserIds: string[]; 
  favoritedByUserIds: string[];
}

export interface EventScheduleResponseDTO {
  id: string;
  title: string;
  description: string;
  scheduleTime: string;
}

export interface EventCreateDTO {
  eventName: string;
  description?: string;
  contentRating: string;
  price: number;
  startsAt: string;
  endsAt: string; 
  venueId?: string;
  tagIds?: string[];
}

export interface EventUpdateDTO {
  eventName?: string;
  description?: string;
  contentRating?: string;
  price?: number;
  startsAt?: string;
  endsAt?: string;
  venueId?: string;
  tagIds?: string[];
}

export interface EventScheduleCreateDTO {
  title: string;
  description?: string;
  scheduleTime: string;
}

export interface EventScheduleUpdateDTO {
  title?: string;
  description?: string;
  scheduleTime?: string;
}

export interface EventSearchFilters {
  q?: string;
  dateFrom?: string; // ISO 8601 string
  dateTo?: string;   // ISO 8601 string
  priceMin?: number;
  priceMax?: number;
  neighborhood?: string;
}