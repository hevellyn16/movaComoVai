import { tagService } from "@/services/tags.service";
import { TagResponseDTO, TagCreateDTO, TagUpdateDTO } from "@/types/tag.types";
import { useState, useCallback } from "react";

export const useTags = () => {
  const [tags, setTags] = useState<TagResponseDTO[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Função auxiliar para gerenciar loading e erros
  const handleRequest = async <T>(request: () => Promise<T>): Promise<T> => {
    setIsLoading(true);
    setError(null);
    try {
      const result = await request();
      return result;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || "Ocorreu um erro ao processar a tag.";
      setError(errorMessage);
      throw err;
    } finally {
      setIsLoading(false);
    }
  };

  const fetchAllTags = useCallback(async () => {
    return handleRequest(async () => {
      const data = await tagService.findAll();
      setTags(data);
      return data;
    });
  }, []);

  const fetchTagById = async (id: string) => {
    return handleRequest(() => tagService.findById(id));
  };

  const createTag = async (data: TagCreateDTO) => {
    return handleRequest(async () => {
      const newTag = await tagService.create(data);
      setTags((prev) => [...prev, newTag]);
      return newTag;
    });
  };

  const updateTag = async (id: string, data: TagUpdateDTO) => {
    return handleRequest(async () => {
      const updatedTag = await tagService.update(id, data);
      setTags((prev) => prev.map((tag) => (tag.id === id ? updatedTag : tag)));
      return updatedTag;
    });
  };

  const deleteTag = async (id: string) => {
    return handleRequest(async () => {
      await tagService.delete(id);
      setTags((prev) => prev.filter((tag) => tag.id !== id));
    });
  };

  return {
    tags,
    isLoading,
    error,
    clearError: () => setError(null),
    fetchAllTags,
    fetchTagById,
    createTag,
    updateTag,
    deleteTag,
  };
};