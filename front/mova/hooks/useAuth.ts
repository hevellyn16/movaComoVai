"use client";

import React, { createContext, useContext, useState, useEffect, ReactNode } from "react";
import { STORAGE_KEYS } from "@/services/api";
import { authService, LoginCredentials } from "@/services/auth.service";
import { userService } from "@/services/users.service";
import { User } from "@/types/user.types";

interface AuthContextData {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (credentials: any) => Promise<void>;
  logout: () => void;
  updateUser: (updates: Partial<User>) => void;
}

const AuthContext = createContext<AuthContextData>({} as AuthContextData);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const initAuth = async () => {
      const token = localStorage.getItem(STORAGE_KEYS.ACCESS_TOKEN);
      const storedUser = localStorage.getItem(STORAGE_KEYS.USER_DATA);

      if (token && storedUser) {
        let parsedUser = JSON.parse(storedUser);
        setUser(parsedUser);
        
        try {
          const userData = await userService.getMe();
          parsedUser = {
            ...parsedUser,
            name: userData.username,
            avatarUrl: userData.avatarUrl,
          };
          setUser(parsedUser);
          localStorage.setItem(STORAGE_KEYS.USER_DATA, JSON.stringify(parsedUser));
        } catch (error) {
          console.error("Erro ao atualizar dados do usuário", error);
        }
      }
      
      setIsLoading(false);
    };

    initAuth();
  }, []);

  const login = async (credentials: LoginCredentials) => {
    try {
      const data = await authService.login(credentials);
      const { token, id, username, role, email } = data;
      localStorage.setItem(STORAGE_KEYS.ACCESS_TOKEN, token);
      
      let fullUserData;
      try {
          fullUserData = await userService.getMe();
      } catch (e) {
          console.error("Erro ao buscar me:", e);
      }

      const loggedUser: User = {
        id,
        name: fullUserData?.username || username,
        email,
        userType: role === "ROLE_ADMIN" ? "ADMIN" : "COMMON",
        avatarUrl: fullUserData?.avatarUrl || null,
        createdAt: fullUserData?.createdAt || new Date().toISOString(),
      };
      setUser(loggedUser);
      localStorage.setItem(STORAGE_KEYS.USER_DATA, JSON.stringify(loggedUser));

  } catch (error) {
    console.error("Erro ao fazer login:", error);
    throw error; 
  }
};

  const logout = () => {
    localStorage.removeItem(STORAGE_KEYS.ACCESS_TOKEN);
    localStorage.removeItem(STORAGE_KEYS.USER_DATA);
    setUser(null);
    window.location.href = "/login";
  };

  const updateUser = (updates: Partial<User>) => {
    setUser((prev) => {
      if (!prev) return prev;
      const updated = { ...prev, ...updates };
      localStorage.setItem(STORAGE_KEYS.USER_DATA, JSON.stringify(updated));
      return updated;
    });
  };

return React.createElement(
    AuthContext.Provider,
    {
      value: {
        user,
        isAuthenticated: !!user,
        isLoading,
        login,
        logout,
        updateUser,
      },
    },
    children
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error("useAuth deve ser usado dentro de um AuthProvider");
  }

  return {
    ...context,
    role: context.user?.userType || null,
    isAdmin: context.user?.userType === "ADMIN",
    isCommon: context.user?.userType === "COMMON",
  };
};