export interface User {
  id: string;
  name: string;
  email: string;
  userType: "ADMIN" | "COMMON";
  createdAt: string;
  updatedAt?: string;
}

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