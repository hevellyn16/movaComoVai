"use client";
import Link from "next/link";
import { useState } from "react";
import { useRouter } from "next/navigation"; // Para fazer o redirecionamento
import { useAuth } from "@/hooks/useAuth"; // ATENÇÃO: Ajuste este caminho para o seu arquivo AuthContext

import { GoogleOAuthProvider, useGoogleLogin } from "@react-oauth/google";

export const LoginForm = () => {
  const router = useRouter();
  const { login } = useAuth(); // Puxando a função de login do seu hook

  // Estados dos inputs e de controle da tela
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault(); // Impede a página de recarregar
    setErrorMessage("");
    setIsSubmitting(true);

    try {
      // Chama a função login do contexto passando as credenciais
      // O seu backend espera 'username', então passamos o email para ele
      await login({ username: email, password });

      // Se deu certo, redireciona para a home ou dashboard
      router.push("/");
    } catch (error) {
      console.error("Erro no login:", error);
      setErrorMessage("E-mail ou senha incorretos. Tente novamente.");
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleGoogleLogin = useGoogleLogin({
    onSuccess: async (tokenResponse) => {
      setIsSubmitting(true);
      setErrorMessage("");
      try {
        // Opção A: envia o access_token para o seu backend validar
        await login({ googleToken: tokenResponse.access_token });
        router.push("/");
      } catch (error) {
        console.error("Erro no login com Google:", error);
        setErrorMessage("Não foi possível entrar com o Google. Tente novamente.");
      } finally {
        setIsSubmitting(false);
      }
    },
    onError: () => {
      setErrorMessage("Login com Google cancelado ou falhou.");
    },
  });

  return (
    <div className="flex min-h-screen font-sans">
      <div
        className="hidden md:flex flex-col justify-end w-1/2 bg-cover bg-center relative"
        style={{
          backgroundImage:
            "url('https://media.istockphoto.com/photos/front-view-arco-de-nossa-senhora-de-fatima-symbol-of-sobral-city-of-picture-id649424532?k=20&m=649424532&s=612x612&w=0&h=Z2KHjXmy1Azv41z43nVy--FF-odkq4scRKh9-dGE86A=')",
        }}
      >
        <div className="absolute inset-0 bg-linear-to-t from-black/90 via-black/40 to-transparent"></div>
        <div className="relative z-10 p-12 text-white">
          <h2 className="text-4xl font-bold mb-4">
            Descubra a Pulsação Cultural.
          </h2>
          <p className="text-lg text-gray-200">
            Conecte-se com a essência de Sobral. Explore eventos, encontre sua
            comunidade e viva a cultura local de forma vibrante e moderna.
          </p>
        </div>
      </div>

      <div className="flex flex-col justify-center w-full md:w-1/2 p-8 md:p-16 bg-white">
        <div className="w-full max-w-md mx-auto">
          <div className="mb-8">
            <h1 className="text-4xl font-bold text-[#b91c1c] mb-2">MoVa</h1>
            <p className="text-gray-500">
              Bem-vindo de volta. Acesse sua conta para continuar.
            </p>
          </div>

          {/* Exibição de mensagem de erro caso o login falhe */}
          {errorMessage && (
            <div className="mb-4 p-3 bg-red-50 border border-red-200 text-red-600 rounded-lg text-sm font-medium">
              {errorMessage}
            </div>
          )}

          {/* Form atualizado com onSubmit */}
          <form onSubmit={handleSubmit} className="space-y-5">
            <div className="space-y-1">
              <label
                htmlFor="email"
                className="block text-sm font-semibold text-gray-800"
              >
                E-mail
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

            <div className="space-y-1">
              <div className="flex justify-between items-center">
                <label
                  htmlFor="password"
                  className="block text-sm font-semibold text-gray-800"
                >
                  Senha
                </label>
                <Link
                  href="/forgotPassword"
                  className="text-sm text-[#0066cc] hover:underline font-medium"
                >
                  Esqueci minha senha
                </Link>
              </div>
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
                      d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"
                    />
                  </svg>
                </div>
                <input
                  type={showPassword ? "text" : "password"}
                  id="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                  placeholder="••••••••"
                  minLength={8}
                  className="block w-full pl-10 pr-10 py-2.5 border border-gray-300 rounded-lg shadow-sm focus:ring-[#b91c1c] focus:border-[#b91c1c] sm:text-sm transition-colors"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute inset-y-0 right-0 pr-3 flex items-center hover:text-gray-700 cursor-pointer"
                >
                  {showPassword ? (
                    <svg
                      className="h-5 w-5 text-gray-400"
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
                      className="h-5 w-5 text-gray-400"
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

            <button
              type="submit"
              disabled={isSubmitting} // Desativa o botão enquanto processa
              className={`w-full flex justify-center items-center py-2.5 px-4 border border-transparent rounded-lg shadow-sm text-sm font-bold text-white transition-colors ${
                isSubmitting
                  ? "bg-gray-400 cursor-not-allowed"
                  : "bg-[#b91c1c] hover:bg-[#991b1b] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#b91c1c] cursor-pointer"
              }`}
            >
              {isSubmitting ? (
                "Entrando..."
              ) : (
                <>
                  Entrar <span className="ml-2">→</span>
                </>
              )}
            </button>
          </form>

          <div className="mt-8 relative">
            <div className="absolute inset-0 flex items-center">
              <div className="w-full border-t border-gray-200"></div>
            </div>
            <div className="relative flex justify-center text-sm">
              <span className="px-4 bg-white text-gray-500 font-semibold tracking-wide text-xs">
                OU CONTINUE COM
              </span>
            </div>
          </div>

          <div className="mt-6">
            <button
              type="button"
                onClick={() => handleGoogleLogin()}
              className="w-full flex justify-center items-center py-2.5 px-4 border border-gray-300 rounded-lg shadow-sm bg-white text-sm font-semibold text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-gray-200 transition-colors"
            >
              <svg
                className="h-5 w-5 mr-2"
                viewBox="0 0 24 24"
                width="24"
                height="24"
                xmlns="http://www.w3.org/2000/svg"
              >
                <path
                  d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
                  fill="#4285F4"
                />
                <path
                  d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
                  fill="#34A853"
                />
                <path
                  d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"
                  fill="#FBBC05"
                />
                <path
                  d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"
                  fill="#EA4335"
                />
              </svg>
              Google
            </button>
          </div>

          <p className="mt-8 text-center text-sm text-gray-600">
            Não tem uma conta?{" "}
            <Link
              href="/register"
              className="font-bold text-[#b91c1c] hover:underline"
            >
              Cadastre-se
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default function AuthLoginPage() {
  return (
    <GoogleOAuthProvider clientId={process.env.NEXT_PUBLIC_GOOGLE_CLIENT_ID!}>
      <LoginForm />
    </GoogleOAuthProvider>
  );
}
