import axios from "axios";

export const STORAGE_KEYS = {
  ACCESS_TOKEN: "jwt_session",
  USER_DATA: "user_data"
};

const api = axios.create({
  baseURL: "http://localhost:8080/",
  headers: {
    "Content-Type": "application/json",
  },
});

// 1. Interceptor de Requisição: Injeta o token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem(STORAGE_KEYS.ACCESS_TOKEN);
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error),
);

// 2. Interceptor de Resposta: Redireciona no 401
api.interceptors.response.use(
  (response) => response,
  (error) => {
    // Se o status for 401, o token expirou ou é inválido
    if (error.response?.status === 401) {
      localStorage.removeItem(STORAGE_KEYS.ACCESS_TOKEN);

      // Redireciona para o login para que o usuário se autentique novamente
      if (window.location.pathname !== "/login") {
        window.location.href = "/login";
      }
    }

    return Promise.reject(error);
  },
);

export default api;
