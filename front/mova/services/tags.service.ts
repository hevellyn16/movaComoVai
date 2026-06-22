import { TagResponseDTO, TagCreateDTO, TagUpdateDTO } from "@/types/tag.types";
import api from "./api"; 

export const tagService = {

  /** Lista todas as tags disponíveis no sistema */
  findAll: async (): Promise<TagResponseDTO[]> => {
    const response = await api.get<TagResponseDTO[]>("/tags");
    return response.data;
  },

  /** Busca os dados de uma tag específica pelo seu UUID */
  findById: async (id: string): Promise<TagResponseDTO> => {
    const response = await api.get<TagResponseDTO>(`/tags/${id}`);
    return response.data;
  },

  /** Cadastra uma nova tag no sistema */
  create: async (data: TagCreateDTO): Promise<TagResponseDTO> => {
    const response = await api.post<TagResponseDTO>("/tags", data);
    return response.data;
  },

  /** Atualiza o nome de uma tag existente */
  update: async (id: string, data: TagUpdateDTO): Promise<TagResponseDTO> => {
    const response = await api.put<TagResponseDTO>(`/tags/${id}`, data);
    return response.data;
  },

  /** Remove uma tag permanentemente do sistema */
  delete: async (id: string): Promise<void> => {
    await api.delete(`/tags/${id}`);
  },
};