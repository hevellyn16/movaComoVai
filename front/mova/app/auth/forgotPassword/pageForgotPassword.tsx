import Link from "next/link";

export const ForgotPasswordSuccessPage = () => {
  return (
    <div className="flex min-h-screen font-sans">
      
      <div 
        className="hidden md:flex flex-col justify-center w-1/2 bg-cover bg-center relative" 
        style={{ backgroundImage: "url('https://bagy.com.br/blog/wp-content/uploads/2023/01/como-recuperar-e-mail-1.jpg')" }}
      >
        <div className="absolute inset-0 bg-[#b91c1c]/70 mix-blend-multiply"></div>
        <div className="absolute inset-0 bg-linear-to-t from-black/60 to-transparent"></div>
        
        <div className="relative z-10 p-12 text-white max-w-lg mx-auto">
          <h2 className="text-5xl font-bold mb-4 leading-tight">Sua segurança<br/>em primeiro lugar.</h2>
          <p className="text-lg text-gray-100 font-medium">
            Recupere seu acesso e continue explorando a cultura de Sobral com a MoVa.
          </p>
        </div>
      </div>

      <div className="flex flex-col justify-center items-center w-full md:w-1/2 p-8 md:p-16 bg-white text-center">
        <div className="w-full max-w-md mx-auto flex flex-col items-center">
          
          <div className="mb-8 w-full text-left">
            <h1 className="text-2xl font-bold text-[#b91c1c]">MoVa</h1>
          </div>

          <div className="w-20 h-20 bg-red-50 rounded-full flex items-center justify-center mb-6">
            <svg className="w-10 h-10 text-[#b91c1c]" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2">
              <path strokeLinecap="round" strokeLinejoin="round" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
            </svg>
          </div>
          
          <h2 className="text-3xl font-bold text-gray-900 mb-4">Verifique seu e-mail</h2>
          <p className="text-gray-500 text-base mb-8">
            Enviamos as instruções e um link seguro para redefinição de senha para o seu endereço de e-mail. Por favor, verifique sua caixa de entrada e a pasta de spam.
          </p>

          <Link 
            href="/auth/login"
            className="w-full flex justify-center items-center py-2.5 px-4 border border-transparent rounded-lg shadow-sm text-sm font-bold text-white bg-[#b91c1c] hover:bg-[#991b1b] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#b91c1c] transition-colors"
          >
            Voltar para o Login
          </Link>

          <p className="mt-8 text-sm text-gray-600">
            Não recebeu o e-mail?{' '}
            <button  type="button" className="font-bold text-[#b91c1c] hover:underline cursor-pointer">
              Clique aqui para reenviar
            </button>
          </p>

        </div>
      </div>
    </div>
  );
};

export default ForgotPasswordSuccessPage;