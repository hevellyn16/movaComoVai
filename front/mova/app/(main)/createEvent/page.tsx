"use client";

import { useState, useRef } from "react";

export default function CreateEventPage() {
  // Estados simples para as interações visuais mostradas na imagem
  const [ticketType, setTicketType] = useState<"free" | "paid">("free");
  const [selectedCategory, setSelectedCategory] = useState("Música");
  
  // Novos estados e ref para as tags dinâmicas
  const [categories, setCategories] = useState(["Música", "Teatro", "Dança", "Exposição", "Oficina"]);
  const tagInputRef = useRef<HTMLInputElement>(null);
  
  return (
    <div className="flex h-screen w-full bg-[#f9fafb] font-sans text-gray-800 overflow-hidden">
      {/* ======================= MAIN CONTENT AREA ======================= */}
      <main className="flex-1 flex flex-col h-screen overflow-hidden">
        {/* SCROLLABLE FORM AREA */}
        <div className="flex-1 overflow-y-auto p-8">
          <div className="max-w-6xl mx-auto">
            
            {/* Header da Página */}
            <div className="flex justify-between items-end mb-8">
              <div>
                <h2 className="text-3xl font-bold text-gray-900 mb-1">Cadastrar Novo Evento</h2>
                <p className="text-gray-500 text-sm">Preencha os detalhes abaixo para publicar seu evento cultural em Sobral.</p>
              </div>
              <div className="flex gap-3">
                <button type="button" className="cursor-pointer px-5 py-2.5 text-sm font-semibold text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors shadow-sm">
                  Salvar Rascunho
                </button>
                <button type="submit" className="cursor-pointer flex items-center gap-2 px-5 py-2.5 text-sm font-semibold text-white bg-[#b91c1c] border border-transparent rounded-lg hover:bg-[#991b1b] transition-colors shadow-sm">
                  <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}><path strokeLinecap="round" strokeLinejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12" /></svg>
                  Publicar Evento
                </button>
              </div>
            </div>

            {/* Formulário Grid (2 colunas para info, 1 coluna para configs extras) */}
            <form className="grid grid-cols-1 lg:grid-cols-3 gap-6 pb-20">
              
              {/* === COLUNA ESQUERDA (Principal) === */}
              <div className="lg:col-span-2 space-y-6">
                
                {/* Card: Informações Básicas */}
                <div className="bg-white rounded-xl border border-gray-200 p-6 shadow-sm">
                  <div className="flex items-center gap-2 mb-6">
                    <svg className="w-5 h-5 text-[#b91c1c]" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}><path strokeLinecap="round" strokeLinejoin="round" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
                    <h3 className="text-lg font-bold text-gray-900">Informações Básicas</h3>
                  </div>
                  
                  <div className="space-y-4">
                    <div>
                      <label className="block text-sm font-bold text-gray-700 mb-1">Título do Evento *</label>
                      <input type="text" placeholder="ex: Festival de Quadrilhas de Sobral" className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-[#b91c1c] focus:border-[#b91c1c] outline-none transition-colors" />
                    </div>
                    <div>
                      <label className="block text-sm font-bold text-gray-700 mb-1">Descrição *</label>
                      <textarea rows={4} placeholder="Descreva o evento, atrações e importância cultural..." className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-[#b91c1c] focus:border-[#b91c1c] outline-none transition-colors resize-none"></textarea>
                    </div>
                    
                    {/* Alteração feita aqui na parte de Tags */}
                    <div>
                      <label className="block text-sm font-bold text-gray-700 mb-2">Categoria e Tags *</label>
                      <div className="flex flex-wrap gap-2 mb-3">
                        {categories.map(cat => (
                          <button 
                            key={cat} 
                            type="button" 
                            onClick={() => setSelectedCategory(cat)}
                            className={`cursor-pointer px-4 py-1 text-xs font-semibold rounded-full border transition-colors ${selectedCategory === cat ? 'bg-blue-50 text-blue-700 border-blue-200' : 'bg-white text-gray-600 border-gray-300 hover:bg-gray-50'}`}
                          >
                            {cat}
                          </button>
                        ))}
                      </div>
                      <div className="relative">
                        <svg className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}><path strokeLinecap="round" strokeLinejoin="round" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z" /></svg>
                        <input 
                          type="text" 
                          ref={tagInputRef}
                          onKeyDown={(e) => {
                            if (e.key === 'Enter' || e.key === ',') {
                              e.preventDefault();
                              
                              const currentVal = tagInputRef.current?.value || "";
                              const newTag = currentVal.trim().replace(',', ''); 
                              
                              if (newTag && !categories.includes(newTag)) {
                                setCategories([...categories, newTag]); 
                                setSelectedCategory(newTag); 
                              }
                              
                              if (tagInputRef.current) {
                                tagInputRef.current.value = ""; 
                              }
                            }
                          }}
                          placeholder="Adicionar tags personalizadas (pressione Enter ou vírgula)..." 
                          className="w-full pl-9 pr-3 py-2 border border-gray-300 rounded-lg text-sm bg-gray-50/50 focus:ring-[#b91c1c] focus:border-[#b91c1c] outline-none transition-colors" 
                        />
                      </div>
                    </div>
                  </div>
                </div>

                {/* Card: Data e Hora */}
                <div className="bg-white rounded-xl border border-gray-200 p-6 shadow-sm">
                  <div className="flex items-center gap-2 mb-6">
                    <svg className="w-5 h-5 text-[#b91c1c]" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}><path strokeLinecap="round" strokeLinejoin="round" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
                    <h3 className="text-lg font-bold text-gray-900">Data e Hora</h3>
                  </div>
                  
                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <label className="block text-sm font-bold text-gray-700 mb-1">Data de Início *</label>
                      <input type="date" className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm text-gray-500 focus:ring-[#b91c1c] outline-none" />
                    </div>
                    <div>
                      <label className="block text-sm font-bold text-gray-700 mb-1">Hora de Início *</label>
                      <input type="time" className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm text-gray-500 focus:ring-[#b91c1c] outline-none" />
                    </div>
                    <div>
                      <label className="block text-sm font-bold text-gray-700 mb-1">Data de Término</label>
                      <input type="date" className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm text-gray-500 focus:ring-[#b91c1c] outline-none" />
                    </div>
                    <div>
                      <label className="block text-sm font-bold text-gray-700 mb-1">Hora de Término</label>
                      <input type="time" className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm text-gray-500 focus:ring-[#b91c1c] outline-none" />
                    </div>
                  </div>
                </div>

                {/* Card: Localização */}
                <div className="bg-white rounded-xl border border-gray-200 p-6 shadow-sm">
                  <div className="flex items-center gap-2 mb-6">
                    <svg className="w-5 h-5 text-[#b91c1c]" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}><path strokeLinecap="round" strokeLinejoin="round" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" /><path strokeLinecap="round" strokeLinejoin="round" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" /></svg>
                    <h3 className="text-lg font-bold text-gray-900">Localização</h3>
                  </div>
                  
                  <div className="space-y-4">
                    <div>
                      <label className="block text-sm font-bold text-gray-700 mb-1">Nome do Local *</label>
                      <input type="text" placeholder="ex: Margem Esquerda do Rio Acaraú" className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-[#b91c1c] outline-none" />
                    </div>
                    <div>
                      <label className="block text-sm font-bold text-gray-700 mb-1">Endereço Completo *</label>
                      <input type="text" placeholder="Rua, Número, Bairro" className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-[#b91c1c] outline-none" />
                    </div>
                    {/* Fake Map Area */}
                    <div className="w-full h-48 bg-gray-200 rounded-lg border border-gray-300 flex items-end justify-center pb-4 relative overflow-hidden">
                      <button type="button" className="cursor-pointer relative z-10 flex items-center gap-2 px-4 py-2 bg-white rounded-full shadow-sm text-xs font-bold text-gray-700 hover:bg-gray-50">
                        <svg className="w-4 h-4 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}><path strokeLinecap="round" strokeLinejoin="round" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" /><path strokeLinecap="round" strokeLinejoin="round" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" /></svg>
                        Clique para definir a localização exata no mapa
                      </button>
                    </div>
                  </div>
                </div>

              </div>

              {/* === COLUNA DIREITA (Complementar) === */}
              <div className="space-y-6">
                
                {/* Card: Ingressos */}
                <div className="bg-white rounded-xl border border-gray-200 p-6 shadow-sm">
                  <div className="flex items-center gap-2 mb-6">
                    <svg className="w-5 h-5 text-[#b91c1c]" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}><path strokeLinecap="round" strokeLinejoin="round" d="M15 5v2m0 4v2m0 4v2M5 5a2 2 0 00-2 2v3a2 2 0 110 4v3a2 2 0 002 2h14a2 2 0 002-2v-3a2 2 0 110-4V7a2 2 0 00-2-2H5z" /></svg>
                    <h3 className="text-lg font-bold text-gray-900">Ingressos</h3>
                  </div>

                  <div className="space-y-3 mb-4">
                    {/* Botão Radio Fake: Gratuita */}
                    <div 
                      onClick={() => setTicketType("free")}
                      className={`flex items-center justify-between p-3 border rounded-lg cursor-pointer transition-colors ${ticketType === 'free' ? 'border-[#b91c1c] bg-red-50/30' : 'border-gray-200 hover:border-gray-300'}`}
                    >
                      <div className="flex items-center gap-3">
                        <div className={`p-2 rounded-full ${ticketType === 'free' ? 'bg-[#b91c1c] text-white' : 'bg-yellow-100 text-yellow-600'}`}>
                          <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}><path strokeLinecap="round" strokeLinejoin="round" d="M12 8v13m0-13V6a2 2 0 112 2h-2zm0 0V5.5A2.5 2.5 0 109.5 8H12zm-3 4h6m-6 4h6m-4-8h2" /></svg>
                        </div>
                        <div>
                          <p className="text-sm font-bold text-gray-900">Entrada Gratuita</p>
                          <p className="text-xs text-gray-500">Aberto ao público</p>
                        </div>
                      </div>
                      <div className={`w-4 h-4 rounded-full border-2 flex items-center justify-center ${ticketType === 'free' ? 'border-[#b91c1c]' : 'border-gray-300'}`}>
                        {ticketType === 'free' && <div className="w-2 h-2 bg-[#b91c1c] rounded-full"></div>}
                      </div>
                    </div>

                    {/* Botão Radio Fake: Paga */}
                    <div 
                      onClick={() => setTicketType("paid")}
                      className={`flex items-center justify-between p-3 border rounded-lg cursor-pointer transition-colors ${ticketType === 'paid' ? 'border-[#b91c1c] bg-red-50/30' : 'border-gray-200 hover:border-gray-300'}`}
                    >
                      <div className="flex items-center gap-3">
                        {/* A mágica da cor acontece nesta linha abaixo: */}
                        <div className={`p-2 rounded-full transition-colors ${ticketType === 'paid' ? 'bg-[#b91c1c] text-white' : 'bg-gray-100 text-gray-500'}`}>
                          <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}><path strokeLinecap="round" strokeLinejoin="round" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z" /></svg>
                        </div>
                        <div>
                          <p className="text-sm font-bold text-gray-900">Entrada Paga</p>
                          <p className="text-xs text-gray-500">Requer ingressos</p>
                        </div>
                      </div>
                      <div className={`w-4 h-4 rounded-full border-2 flex items-center justify-center ${ticketType === 'paid' ? 'border-[#b91c1c]' : 'border-gray-300'}`}>
                        {ticketType === 'paid' && <div className="w-2 h-2 bg-[#b91c1c] rounded-full"></div>}
                      </div>
                    </div>
                  </div>

                  <div>
                    <label className="block text-sm font-bold text-gray-700 mb-1">Capacidade (Opcional)</label>
                    <input type="text" placeholder="ex: 500" className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-[#b91c1c] outline-none" />
                  </div>
                </div>

                {/* Card: Classificação Indicativa */}
                <div className="bg-white rounded-xl border border-gray-200 p-6 shadow-sm">
                  <div className="flex items-center gap-2 mb-4">
                    <svg className="w-5 h-5 text-[#b91c1c]" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}><path strokeLinecap="round" strokeLinejoin="round" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" /></svg>
                    <h3 className="text-lg font-bold text-gray-900">Classificação Indicativa</h3>
                  </div>
                  <select className="cursor-pointer w-full px-3 py-2 border border-gray-300 rounded-lg text-sm text-gray-700 bg-white focus:ring-[#b91c1c] outline-none appearance-none">
                    <option>Livre (Todas as idades)</option>
                    <option>10 anos</option>
                    <option>12 anos</option>
                    <option>14 anos</option>
                    <option>16 anos</option>
                    <option>18 anos</option>
                  </select>
                </div>

                {/* Card: Infraestrutura */}
                <div className="bg-white rounded-xl border border-gray-200 p-6 shadow-sm">
                  <div className="flex items-center gap-2 mb-4">
                    <svg className="w-5 h-5 text-[#b91c1c]" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}><path strokeLinecap="round" strokeLinejoin="round" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" /></svg>
                    <h3 className="text-lg font-bold text-gray-900">Infraestrutura</h3>
                  </div>
                  <div className="space-y-3">
                    <label className="flex items-center gap-3 cursor-pointer group">
                      <input type="checkbox" className="cursor-pointer w-4 h-4 text-[#b91c1c] border-gray-300 rounded focus:ring-[#b91c1c]" />
                      <span className="text-sm font-medium text-gray-700 flex items-center gap-2 group-hover:text-gray-900">
                        <span className="w-4 h-4 font-bold text-lg leading-none">♿</span> Acessibilidade para Cadeirantes
                      </span>
                    </label>
                    <label className="flex items-center gap-3 cursor-pointer group">
                      <input type="checkbox" className="cursor-pointer w-4 h-4 text-[#b91c1c] border-gray-300 rounded focus:ring-[#b91c1c]" />
                      <span className="text-sm font-medium text-gray-700 flex items-center gap-2 group-hover:text-gray-900">
                        <span className="w-4 h-4 font-bold text-lg leading-none">P</span> Estacionamento Disponível
                      </span>
                    </label>
                    <label className="flex items-center gap-3 cursor-pointer group">
                      <input type="checkbox" className="cursor-pointer w-4 h-4 text-[#b91c1c] border-gray-300 rounded focus:ring-[#b91c1c]" />
                      <span className="text-sm font-medium text-gray-700 flex items-center gap-2 group-hover:text-gray-900">
                        <span className="w-4 h-4 font-bold text-lg leading-none">🍴</span> Alimentação e Bebidas
                      </span>
                    </label>
                    <label className="flex items-center gap-3 cursor-pointer group">
                      <input type="checkbox" className="cursor-pointer w-4 h-4 text-[#b91c1c] border-gray-300 rounded focus:ring-[#b91c1c]" />
                      <span className="text-sm font-medium text-gray-700 flex items-center gap-2 group-hover:text-gray-900">
                        <span className="w-4 h-4 font-bold text-lg leading-none">🚻</span> Banheiros
                      </span>
                    </label>
                  </div>
                </div>

                {/* Card: Imagem de Capa */}
                <div className="bg-white rounded-xl border border-gray-200 p-6 shadow-sm">
                  <div className="flex items-center gap-2 mb-4">
                    <svg className="w-5 h-5 text-[#b91c1c]" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}><path strokeLinecap="round" strokeLinejoin="round" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" /></svg>
                    <h3 className="text-lg font-bold text-gray-900">Imagem de Capa</h3>
                  </div>
                  <div className="border-2 border-dashed border-[#b91c1c]/30 rounded-lg p-6 flex flex-col items-center justify-center text-center bg-red-50/20 hover:bg-red-50/50 transition-colors cursor-pointer">
                    <svg className="w-8 h-8 text-[#b91c1c] mb-2" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}><path strokeLinecap="round" strokeLinejoin="round" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" /></svg>
                    <p className="text-sm font-bold text-gray-700">Clique para fazer upload<br/>ou arraste e solte</p>
                    <p className="text-xs text-gray-500 mt-1">SVG, PNG, JPG ou GIF<br/>(máx. 5MB)</p>
                  </div>
                </div>

              </div>
            </form>
          </div>
        </div>
      </main>
    </div>
  );
}