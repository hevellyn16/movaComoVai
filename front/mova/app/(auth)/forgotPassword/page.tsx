"use client";

import Link from "next/link";
import { useState } from "react";
import { useUser } from "@/hooks/useUsers";

export default function ForgotPasswordPage() {
  const { forgotPassword, isLoading } = useUser();
  const [email, setEmail] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [isSubmitted, setIsSubmitted] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMessage("");

    try {
      await forgotPassword(email);
      setIsSubmitted(true);
    } catch (error: any) {
      console.error("Erro ao solicitar redefinição:", error);
      const msg =
        error.response?.data?.message ||
        "Não foi possível enviar o e-mail. Verifique o endereço e tente novamente.";
      setErrorMessage(msg);
    }
  };

  const handleResend = async () => {
    setErrorMessage("");
    try {
      await forgotPassword(email);
    } catch (error: any) {
      console.error("Erro ao reenviar:", error);
      setErrorMessage("Falha ao reenviar. Tente novamente em alguns minutos.");
    }
  };

  // ======================== TELA DE SUCESSO ========================
  if (isSubmitted) {
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

        <div className="flex flex-col justify-center items-center w-full md:w-1/2 p-8 md:p-16 bg-white text-center">
          <div className="w-full max-w-md mx-auto flex flex-col items-center">
            <div className="mb-8 w-full text-left">
              <h1 className="text-2xl font-bold text-[#b91c1c]">MoVa</h1>
            </div>

            <div className="w-20 h-20 bg-red-50 rounded-full flex items-center justify-center mb-6">
              <svg
                className="w-10 h-10 text-[#b91c1c]"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
                strokeWidth="2"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"
                />
              </svg>
            </div>

            <h2 className="text-3xl font-bold text-gray-900 mb-4">
              Verifique seu e-mail
            </h2>
            <p className="text-gray-500 text-base mb-2">
              Enviamos as instruções e um link seguro para redefinição de senha
              para:
            </p>
            <p className="text-gray-900 font-semibold text-base mb-8">
              {email}
            </p>

            <Link
              href="/login"
              className="w-full flex justify-center items-center py-2.5 px-4 border border-transparent rounded-lg shadow-sm text-sm font-bold text-white bg-[#b91c1c] hover:bg-[#991b1b] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#b91c1c] transition-colors"
            >
              Voltar para o Login
            </Link>

            {errorMessage && (
              <div className="mt-4 p-3 bg-red-50 border border-red-200 text-red-600 rounded-lg text-sm font-medium w-full">
                {errorMessage}
              </div>
            )}

            <p className="mt-8 text-sm text-gray-600">
              Não recebeu o e-mail?{" "}
              <button
                type="button"
                onClick={handleResend}
                disabled={isLoading}
                className="font-bold text-[#b91c1c] hover:underline cursor-pointer disabled:opacity-50"
              >
                {isLoading ? "Reenviando..." : "Clique aqui para reenviar"}
              </button>
            </p>
          </div>
        </div>
      </div>
    );
  }

  // ======================== TELA DO FORMULÁRIO ========================
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
        <div className="w-full max-w-md mx-auto">
          <div className="mb-8">
            <h1 className="text-4xl font-bold text-[#b91c1c] mb-2">MoVa</h1>
            <p className="text-gray-500">
              Esqueceu sua senha? Sem problemas. Informe seu e-mail abaixo.
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
                htmlFor="email"
                className="block text-sm font-semibold text-gray-800"
              >
                E-mail cadastrado
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <svg
                    className="h-5 w-5 text-gray-500"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                    strokeWidth="2"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"
                    />
                  </svg>
                </div>
                <input
                  type="email"
                  id="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                  placeholder="nome@exemplo.com"
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
              {isLoading ? "Enviando..." : "Enviar link de redefinição"}
            </button>
          </form>

          <p className="mt-8 text-center text-sm text-gray-600">
            Lembrou sua senha?{" "}
            <Link
              href="/login"
              className="font-bold text-[#b91c1c] hover:underline"
            >
              Voltar para o Login
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}