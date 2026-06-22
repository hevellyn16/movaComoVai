import { User } from "@/types/event.types";

export interface Comment {
	id: string;
	userId: string;
	eventId: string;
	comment: string;
	createdAt: string;
	updatedAt?: string;
	// Relações opcionais vindas do backend
	user?: User;
	likesCount?: number;
	isLiked?: boolean;
	answers?: Answer[];
}

export interface Answer {
	id: string;
	commentId: string;
	userId: string;
	answer: string;
	createdAt: string;
	updatedAt?: string;
	// Relação opcional
	user?: User;
}

export interface CommentResponseDTO {
  id: string;
  content: string;
  userId: string;
  eventId: string;
  createdAt: string;
  updatedAt: string;
}

export interface CommentCreateDTO {
  content: string;
}