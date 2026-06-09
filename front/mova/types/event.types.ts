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

export interface Venue {
  id: string;
  name: string;
  number: string;
  city: string;
  street: string;
  neighborhood: string;
  landmark?: string;
  hasParkingLot: boolean;
  hasAccessibility: boolean;
  hasBathroom: boolean;
  hasFoodsAndDrinks: boolean;
}

export interface User {
  id: string;
  name: string;
  email: string;
  userType: "ADMIN" | "COMMON";
  createdAt: string;
  updatedAt?: string;
}

export interface Comment {
  id: string;
  userId: string;
  eventId: string;
  comment: string;
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