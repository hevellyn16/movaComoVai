"use client";

import Link from "next/link";
import { useState, Suspense } from "react";
import { useUser } from "@/hooks/useUsers";
import { useSearchParams } from "next/navigation";

function ResetPasswordForm() {
  const searchParams = useSearchParams();
  const token = searchParams.get("token") || "";

  const { resetPassword, isLoading } = useUser();
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [isSuccess, setIsSuccess] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMessage("");

    if (!token) {
      setErrorMessage("Token de redefinição não encontrado ou inválido. Acesse através do link enviado no seu e-mail.");
      return;
    }

    if (password !== confirmPassword) {
      setErrorMessage("As senhas não coincidem. Tente novamente.");
      return;
    }

    if (password.length < 6) {
      setErrorMessage("A senha deve ter pelo menos 6 caracteres.");
      return;
    }

    try {
      await resetPassword(token, { password, confirmPassword });
      setIsSuccess(true);
    } catch (error: any) {
      console.error("Erro ao redefinir senha:", error);
      const msg =
        error.response?.data?.message ||
        "Não foi possível redefinir a senha. O token pode ter expirado.";
      setErrorMessage(msg);
    }
  };

  if (isSuccess) {
    return (
      <div className="w-full max-w-md mx-auto flex flex-col items-center text-center">
        <div className="w-20 h-20 bg-green-50 rounded-full flex items-center justify-center mb-6">
          <svg className="w-10 h-10 text-green-600" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2">
            <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
          </svg>
        </div>
        
        <h2 className="text-3xl font-bold text-gray-900 mb-4">Senha redefinida!</h2>
        <p className="text-gray-500 text-base mb-8">
          Sua senha foi alterada com sucesso. Agora você já pode acessar sua conta.
        </p>

        <Link 
          href="/login"
          className="w-full flex justify-center items-center py-2.5 px-4 border border-transparent rounded-lg shadow-sm text-sm font-bold text-white bg-[#b91c1c] hover:bg-[#991b1b] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#b91c1c] transition-colors"
        >
          Fazer Login
        </Link>
      </div>
    );
  }

  return (
    <div className="w-full max-w-md mx-auto">
      <div className="mb-8">
        <h1 className="text-4xl font-bold text-[#b91c1c] mb-2">MoVa</h1>
        <p className="text-gray-500">
          Crie uma nova senha para sua conta.
        </p>
      </div>

      {errorMessage && (
        <div className="mb-4 p-3 bg-red-50 border border-red-200 text-red-600 rounded-lg text-sm font-medium">
          {errorMessage}
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-5">
        <div className="space-y-1">
          <label
            htmlFor="password"
            className="block text-sm font-semibold text-gray-800"
          >
            Nova Senha
          </label>
          <div className="relative">
            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <span className="material-symbols-outlined text-gray-500" style={{ fontSize: 20 }}>
                lock
              </span>
            </div>
            <input
              type="password"
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              placeholder="Digite sua nova senha"
              className="block w-full pl-10 pr-3 py-2.5 border border-gray-300 rounded-lg shadow-sm focus:ring-[#b91c1c] focus:border-[#b91c1c] sm:text-sm transition-colors"
            />
          </div>
        </div>

        <div className="space-y-1">
          <label
            htmlFor="confirmPassword"
            className="block text-sm font-semibold text-gray-800"
          >
            Confirmar Nova Senha
          </label>
          <div className="relative">
            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <span className="material-symbols-outlined text-gray-500" style={{ fontSize: 20 }}>
                lock
              </span>
            </div>
            <input
              type="password"
              id="confirmPassword"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              required
              placeholder="Digite novamente a nova senha"
              className="block w-full pl-10 pr-3 py-2.5 border border-gray-300 rounded-lg shadow-sm focus:ring-[#b91c1c] focus:border-[#b91c1c] sm:text-sm transition-colors"
            />
          </div>
        </div>

        <button
          type="submit"
          disabled={isLoading}
          className={`w-full flex justify-center items-center py-2.5 px-4 border border-transparent rounded-lg shadow-sm text-sm font-bold text-white transition-colors ${
            isLoading
              ? "bg-gray-400 cursor-not-allowed"
              : "bg-[#b91c1c] hover:bg-[#991b1b] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#b91c1c] cursor-pointer"
          }`}
        >
          {isLoading ? "Salvando..." : "Redefinir senha"}
        </button>
      </form>
    </div>
  );
}

export default function ResetPasswordPage() {
  return (
    <div className="flex min-h-screen font-sans">
      <div
        className="hidden md:flex flex-col justify-center w-1/2 bg-cover bg-center relative"
        style={{
          backgroundImage:
            "url('https://bagy.com.br/blog/wp-content/uploads/2023/01/como-recuperar-e-mail-1.jpg')",
        }}
      >
        <div className="absolute inset-0 bg-[#b91c1c]/70 mix-blend-multiply"></div>
        <div className="absolute inset-0 bg-linear-to-t from-black/60 to-transparent"></div>

        <div className="relative z-10 p-12 text-white max-w-lg mx-auto">
          <h2 className="text-5xl font-bold mb-4 leading-tight">
            Sua segurança
            <br />
            em primeiro lugar.
          </h2>
          <p className="text-lg text-gray-100 font-medium">
            Recupere seu acesso e continue explorando a cultura de Sobral com a
            MoVa.
          </p>
        </div>
      </div>

      <div className="flex flex-col justify-center w-full md:w-1/2 p-8 md:p-16 bg-white">
        <Suspense fallback={<div className="flex justify-center"><div className="animate-spin rounded-full h-8 w-8 border-t-2 border-[#b91c1c]"></div></div>}>
          <ResetPasswordForm />
        </Suspense>
      </div>
    </div>
  );
}
