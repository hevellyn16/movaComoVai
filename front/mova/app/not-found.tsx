"use client";

import Link from "next/link";

export default function NotFoundPage() {

  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-gray-50 font-sans p-6 text-center">
      <div className="w-full max-w-md flex flex-col items-center bg-white p-10 rounded-2xl shadow-sm border border-gray-100">
        
        <h1 className="text-3xl font-bold text-[#b91c1c] mb-6">MoVa</h1>
        
        <div className="text-8xl font-black text-gray-100 mb-4 tracking-tighter">
          404
        </div>
        
        <h2 className="text-2xl font-bold text-gray-900 mb-3">
          Caminho não encontrado
        </h2>
        <p className="text-gray-500 text-sm mb-8 leading-relaxed">
          Parece que você se perdeu explorando. A página que você tentou acessar não existe ou foi movida. 
        </p>

        <Link 
          href="/login"
          className="w-full flex justify-center items-center py-2.5 px-4 border border-transparent rounded-lg shadow-sm text-sm font-bold text-white bg-[#b91c1c] hover:bg-[#991b1b] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#b91c1c] transition-colors"
        >
          Ir para o Login agora
        </Link>
        
      </div>
    </div>
  );
}