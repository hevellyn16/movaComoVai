"use client";

import { useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useUser } from "@/hooks/useUsers";

const RegisterForm = () => {
  const router = useRouter();
  const { register, isLoading, error } = useUser();

  const [showPassword, setShowPassword] = useState(false);

  // Estados dos campos do formulário (Username removido daqui)
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  // Para feedback visual após o cadastro
  const [successMessage, setSuccessMessage] = useState("");

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSuccessMessage("");

    try {
      // Passamos o 'email' tanto para o campo email quanto para o username
      await register({ name, username: email, email, password });

      setSuccessMessage(
        "Conta criada com sucesso! Redirecionando para o login...",
      );

      setTimeout(() => {
        router.push("/login");
      }, 2000);
    } catch (err) {
      console.error("Falha ao registrar:", err);
    }
  };

  return (
    <div className="flex min-h-screen font-sans">
      <div
        className="hidden md:flex flex-col justify-center w-1/2 bg-cover bg-center relative"
        style={{
          backgroundImage:
            "url('https://static.vecteezy.com/system/resources/previews/011/437/600/non_2x/candid-friends-seated-at-the-park-talking-to-each-other-photo.jpg')",
        }}
      >
        <div className="absolute inset-0 bg-[#b91c1c]/70 mix-blend-multiply"></div>
        <div className="absolute inset-0 bg-linear-to-t from-black/60 to-transparent"></div>

        <div className="relative z-10 p-12 text-white max-w-lg mx-auto">
          <h2 className="text-5xl font-bold mb-4 leading-tight">
            A cultura de Sobral
            <br />
            em um só lugar.
          </h2>
          <p className="text-lg text-gray-100 font-medium">
            Conecte-se com eventos, artistas e espaços que movem a nossa cidade.
          </p>
        </div>
      </div>

      <div className="flex flex-col justify-center w-full md:w-1/2 p-8 md:p-16 bg-white overflow-y-auto">
        <div className="w-full max-w-md mx-auto">
          <div className="mb-6">
            <h1 className="text-2xl font-bold text-[#b91c1c] mb-6">MoVa</h1>
            <h2 className="text-3xl font-bold text-gray-900 mb-2">
              Crie sua conta
            </h2>
            <p className="text-gray-500 text-sm">
              Junte-se à comunidade cultural e descubra o que está rolando.
            </p>
          </div>


          {error && (
            <div className="mb-4 p-3 bg-red-50 border border-red-200 text-red-600 rounded-lg text-sm font-medium">
              {error}
            </div>
          )}
          {successMessage && (
            <div className="mb-4 p-3 bg-green-50 border border-green-200 text-green-700 rounded-lg text-sm font-medium">
              {successMessage}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-1">
              <label
                htmlFor="name"
                className="block text-xs font-bold text-gray-700"
              >
                Nome Completo
              </label>
              <input
                type="text"
                id="name"
                value={name}
                onChange={(e) => setName(e.target.value)}
                required
                minLength={3}
                maxLength={255}
                placeholder="Ex: Maria Silva"
                className="block w-full px-3 py-2.5 border border-gray-300 rounded-lg shadow-sm focus:ring-[#b91c1c] focus:border-[#b91c1c] sm:text-sm transition-colors"
              />
            </div>

            <div className="space-y-1">
              <label
                htmlFor="email"
                className="block text-xs font-bold text-gray-700"
              >
                E-mail
              </label>
              <input
                type="email"
                id="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                pattern=".*@.*"
                title="Por favor, inclua um '@' no endereço de e-mail."
                placeholder="seu@email.com"
                className="block w-full px-3 py-2.5 border border-gray-300 rounded-lg shadow-sm focus:ring-[#b91c1c] focus:border-[#b91c1c] sm:text-sm transition-colors"
              />
            </div>

            <div className="space-y-1">
              <label
                htmlFor="password"
                className="block text-xs font-bold text-gray-700"
              >
                Senha
              </label>
              <div className="relative">
                <input
                  type={showPassword ? "text" : "password"}
                  id="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                  minLength={8}
                  maxLength={20}
                  placeholder="Mínimo de 8 caracteres"
                  className="block w-full px-3 pr-10 py-2.5 border border-gray-300 rounded-lg shadow-sm focus:ring-[#b91c1c] focus:border-[#b91c1c] sm:text-sm transition-colors"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute inset-y-0 right-0 pr-3 flex items-center hover:text-gray-700 cursor-pointer"
                >
                  {showPassword ? (
                    <svg
                      className="h-4 w-4 text-gray-400"
                      fill="none"
                      viewBox="0 0 24 24"
                      stroke="currentColor"
                      strokeWidth="2"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21"
                      />
                    </svg>
                  ) : (
                    <svg
                      className="h-4 w-4 text-gray-400"
                      fill="none"
                      viewBox="0 0 24 24"
                      stroke="currentColor"
                      strokeWidth="2"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"
                      />
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"
                      />
                    </svg>
                  )}
                </button>
              </div>
            </div>

            <div className="flex items-start mt-4 mb-6">
              <div className="flex items-center h-5">
                <input
                  id="terms"
                  type="checkbox"
                  required
                  className="cursor-pointer w-4 h-4 text-[#b91c1c] border-gray-300 rounded focus:ring-[#b91c1c]"
                />
              </div>
              <div className="ml-3 text-sm">
                <label htmlFor="terms" className="text-gray-600">
                  Eu concordo com os{" "}
                  <Link
                    href="/termos"
                    className="font-bold text-[#0066cc] hover:underline"
                  >
                    Termos de Uso
                  </Link>{" "}
                  e a{" "}
                  <Link
                    href="/privacidade"
                    className="font-bold text-[#0066cc] hover:underline"
                  >
                    Política de Privacidade
                  </Link>{" "}
                  da MoVa.
                </label>
              </div>
            </div>

            <button
              type="submit"
              disabled={isLoading || !!successMessage}
              className={`w-full flex justify-center items-center py-2.5 px-4 border border-transparent rounded-lg shadow-sm text-sm font-bold text-white transition-colors ${
                isLoading || successMessage
                  ? "bg-gray-400 cursor-not-allowed"
                  : "bg-[#b91c1c] hover:bg-[#991b1b] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#b91c1c] cursor-pointer"
              }`}
            >
              {isLoading ? (
                "Criando..."
              ) : (
                <>
                  Criar Conta <span className="ml-2">→</span>
                </>
              )}
            </button>
          </form>

          <p className="mt-8 text-center text-sm text-gray-600">
            Já tem uma conta?{" "}
            <Link
              href="/login"
              className="font-bold text-[#b91c1c] hover:underline"
            >
              Entrar
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default function AuthRegisterPage() {
  return (
    //<GoogleOAuthProvider clientId="COLOQUE_SEU_CLIENT_ID_AQUI.apps.googleusercontent.com">
    <RegisterForm />
    //</GoogleOAuthProvider>
  );
}
