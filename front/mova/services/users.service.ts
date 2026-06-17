import api from "./api";

export interface UserResponseDTO {
  id: string;
  name: string;
  username: string;
  email: string;
  avatarUrl: string | null;
  bio: string | null;
  location: string | null;
  isPrivate: boolean;
  userType: "ADMIN" | "COMMON";
  createdAt: string;
  updatedAt: string | null;
  isActive: boolean;
}

export interface UserCreateDTO {
  name: string;
  username: string;
  email: string;
  password: string;
}

export interface UserUpdateDTO {
  name?: string;
  username?: string;
  email?: string;
  bio?: string;
  location?: string;
  isPrivate?: boolean;
}

export interface UserPublicProfileDTO {
  id: string;
  name: string;
  username: string;
  avatarUrl: string | null;
  bio: string | null;
  location: string | null;
  createdAt: string;
}

export interface PasswordRequestDTO {
  password: string;
  confirmPassword: string;
}

export interface Page<T> {
  content: T[];
  pageable: any;
  totalElements: number;
  totalPages: number;
  last: boolean;
  size: number;
  number: number;
  first: boolean;
  numberOfElements: number;
  empty: boolean;
}

export const userService = {

  /** Cadastra um novo usuário */
  create: async (data: UserCreateDTO): Promise<UserResponseDTO> => {
    const response = await api.post<UserResponseDTO>("/users", data);
    return response.data;
  },

  /** Solicita redefinição de senha (envia e-mail) */
  forgotPassword: async (email: string): Promise<void> => {
    await api.post("/users/forgot-password", { email });
  },

  /** Redefine a senha com o token recebido no e-mail */
  resetPassword: async (token: string, data: PasswordRequestDTO): Promise<void> => {
    await api.put(`/users/reset-password?token=${token}`, data);
  },

  /** Busca o perfil público de um usuário pelo username */
  getPublicProfile: async (username: string): Promise<UserPublicProfileDTO> => {
    const response = await api.get<UserPublicProfileDTO>(`/users/profile/${username}`);
    return response.data;
  },

  /** Atualiza os dados do próprio usuário autenticado */
  updateMe: async (data: UserUpdateDTO): Promise<UserResponseDTO> => {
    const response = await api.put<UserResponseDTO>("/users", data);
    return response.data;
  },

  /** Deleta logicamente a própria conta */
  deleteMe: async (): Promise<void> => {
    await api.delete("/users");
  },

  /** Adiciona tags de interesse ao próprio perfil */
  addTags: async (tagIds: string[]): Promise<void> => {
    await api.post("/users/me/tags", { tagIds });
  },

  /** Remove uma tag de interesse do próprio perfil */
  removeTag: async (tagId: string): Promise<void> => {
    await api.delete(`/users/me/tags/${tagId}`);
  },

  /** Faz upload do avatar do usuário autenticado */
  uploadAvatar: async (file: File): Promise<{ avatarUrl: string }> => {
    const formData = new FormData();
    formData.append("file", file);

    const response = await api.post<{ avatarUrl: string }>("/users/me/avatar", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
    return response.data;
  },

  /** Busca usuário por ID */
  findById: async (id: string): Promise<UserResponseDTO> => {
    const response = await api.get<UserResponseDTO>(`/users/${id}`);
    return response.data;
  },

  /** Busca usuário por e-mail */
  findByEmail: async (email: string): Promise<UserResponseDTO> => {
    const response = await api.get<UserResponseDTO>(`/users/email/${email}`);
    return response.data;
  },

  /** Lista todos os usuários de forma paginada */
  findAll: async (page = 0, size = 10): Promise<Page<UserResponseDTO>> => {
    const response = await api.get<Page<UserResponseDTO>>(`/users`, {
      params: { page, size },
    });
    return response.data;
  },

  /** Promove um usuário comum para ADMIN */
  updateToAdmin: async (id: string): Promise<UserResponseDTO> => {
    const response = await api.put<UserResponseDTO>(`/users/${id}/admin`);
    return response.data;
  },
};