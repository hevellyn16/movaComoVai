import { useState } from "react";
import { 
  UserCreateDTO, 
  UserUpdateDTO, 
  PasswordRequestDTO,
  UserResponseDTO,
  UserPublicProfileDTO,
} from "../types/user.types";
import { useAuth } from "./useAuth";
import { userService } from "@/services/users.service";
import { Page } from "@/types/utils";

export const useUser = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  
  // Opcional: trazemos o contexto de auth caso precisemos atualizar a foto de perfil/nome no header
  // const { setUser, user } = useAuth(); 
  const handleRequest = async <T>(request: () => Promise<T>): Promise<T> => {
    setIsLoading(true);
    setError(null);
    try {
      const result = await request();
      return result;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || "Ocorreu um erro inesperado.";
      setError(errorMessage);
      throw err;
    } finally {
      setIsLoading(false);
    }
  };

  const register = async (data: UserCreateDTO) => {
    return handleRequest(() => userService.create(data));
  };

  const forgotPassword = async (email: string) => {
    return handleRequest(() => userService.forgotPassword(email));
  };

  const resetPassword = async (token: string, data: PasswordRequestDTO) => {
    return handleRequest(() => userService.resetPassword(token, data));
  };

  const getPublicProfile = async (username: string): Promise<UserPublicProfileDTO> => {
    return handleRequest(() => userService.getPublicProfile(username));
  };

  const updateProfile = async (data: UserUpdateDTO): Promise<UserResponseDTO> => {
    return handleRequest(async () => {
      const updatedUser = await userService.updateMe(data);
      return updatedUser;
    });
  };

  const uploadAvatar = async (file: File): Promise<{ avatarUrl: string }> => {
    return handleRequest(() => userService.uploadAvatar(file));
  };

  const deleteAccount = async () => {
    return handleRequest(() => userService.deleteMe());
  };

  const addTags = async (tagIds: string[]) => {
    return handleRequest(() => userService.addTags(tagIds));
  };

  const removeTag = async (tagId: string) => {
    return handleRequest(() => userService.removeTag(tagId));
  };

  const fetchUserById = async (id: string): Promise<UserResponseDTO> => {
    return handleRequest(() => userService.findById(id));
  };

  const fetchUserByEmail = async (email: string): Promise<UserResponseDTO> => {
    return handleRequest(() => userService.findByEmail(email));
  };

  const fetchAllUsers = async (page = 0, size = 10): Promise<Page<UserResponseDTO>> => {
    return handleRequest(() => userService.findAll(page, size));
  };

  const promoteToAdmin = async (id: string): Promise<UserResponseDTO> => {
    return handleRequest(() => userService.updateToAdmin(id));
  };

  return {
    isLoading,
    error,
    clearError: () => setError(null),
    register,
    forgotPassword,
    resetPassword,
    getPublicProfile,
    updateProfile,
    uploadAvatar,
    deleteAccount,
    addTags,
    removeTag,
    fetchUserById,
    fetchUserByEmail,
    fetchAllUsers,
    promoteToAdmin,
  };
};