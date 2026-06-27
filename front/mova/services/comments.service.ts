import { CommentResponseDTO, CommentCreateDTO } from "@/types/comment.types";
import { Page } from "@/types/utils";
import api from "./api"; // Ajuste o caminho da sua instância do Axios

export const commentService = {
  findById: async (id: string): Promise<CommentResponseDTO> => {
    const response = await api.get<CommentResponseDTO>(`/comments/${id}`);
    return response.data;
  },

  findByEventId: async (eventId: string, page = 0, size = 20): Promise<Page<CommentResponseDTO>> => {
    const response = await api.get<Page<CommentResponseDTO>>(`/comments/events/${eventId}`, {
      params: { page, size },
    });
    return response.data;
  },

  create: async (eventId: string, data: CommentCreateDTO): Promise<CommentResponseDTO> => {
    const response = await api.post<CommentResponseDTO>(`/comments/events/${eventId}`, data);
    return response.data;
  },

  update: async (commentId: string, data: CommentCreateDTO): Promise<CommentResponseDTO> => {
    const response = await api.put<CommentResponseDTO>(`/comments/${commentId}`, data);
    return response.data;
  },

  delete: async (commentId: string): Promise<void> => {
    await api.delete(`/comments/${commentId}`);
  },

  likeComment: async (commentId: string): Promise<void> => {
    await api.post(`/comments/${commentId}/likes`);
  },

  unlikeComment: async (commentId: string): Promise<void> => {
    await api.delete(`/comments/${commentId}/likes`);
  },

  getAnswers: async (commentId: string): Promise<Page<any>> => {
    const response = await api.get<Page<any>>(`/answers/comments/${commentId}`);
    return response.data;
  },

  createAnswer: async (commentId: string, data: { answer: string }): Promise<any> => {
    const response = await api.post<any>(`/answers/${commentId}`, data);
    return response.data;
  },
};