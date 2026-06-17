export interface User {
  id: string;
  name: string;
  email: string;
  userType: "ADMIN" | "COMMON";
  createdAt: string;
  updatedAt?: string;
}