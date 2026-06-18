"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { useEvents } from "@/hooks/useEvents";
import { useTags } from "@/hooks/useTags";
import { useVenues } from "@/hooks/useVenues";
import { Venue } from "@/types/venue.types";

export default function CreateEventPage() {
  const router = useRouter();

  // Integração com a API
  const { createEvent, isLoading: isCreatingEvent } = useEvents();
  const { tags: availableTags, fetchAllTags } = useTags();
  const { fetchAllVenues, createVenue, isLoading: isVenuesLoading } = useVenues();

  // Estados Locais
  const [ticketType, setTicketType] = useState<"free" | "paid">("free");
  const [selectedTagIds, setSelectedTagIds] = useState<string[]>([]);
  
  // Estados de Localização
  const [venues, setVenues] = useState<Venue[]>([]);
  const [venueMode, setVenueMode] = useState<"existing" | "new">("existing");
  const [selectedVenueId, setSelectedVenueId] = useState<string>("");

  // Carrega Tags e Locais ao montar a página
  useEffect(() => {
    const loadData = async () => {
      fetchAllTags();
      try {
        const venuePage = await fetchAllVenues(0, 100);
        setVenues(venuePage.content);
      } catch (error) {
        console.error("Erro ao carregar locais", error);
      }
    };
    loadData();
  }, [fetchAllTags, fetchAllVenues]);

  const selectedVenue = venues.find(v => v.id === selectedVenueId);
  
  // O botão de salvar fica bloqueado se estiver carregando algo
  const isProcessing = isCreatingEvent || isVenuesLoading;

  const toggleTag = (tagId: string) => {
    if (selectedTagIds.includes(tagId)) {
      setSelectedTagIds(selectedTagIds.filter(id => id !== tagId));
    } else {
      setSelectedTagIds([...selectedTagIds, tagId]);
    }
  };

  const handlePublicarEvento = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const formData = new FormData(e.currentTarget);

    try {
      // 1. RESOLVER A LOCALIZAÇÃO (VENUE) PRIMEIRO
      let finalVenueId = selectedVenueId;

      if (venueMode === "new") {
        // Pega os dados do formulário de novo local
        const newVenueData = {
          name: formData.get("novoLocalNome") as string,
          street: formData.get("rua") as string,
          number: formData.get("numero") as string,
          neighborhood: formData.get("bairro") as string,
          city: formData.get("cidade") as string,
          hasAccessibility: formData.get("acessibilidade") === "on",
          hasParkingLot: formData.get("estacionamento") === "on",
          hasFoodsAndDrinks: formData.get("alimentacao") === "on",
          hasBathroom: formData.get("banheiros") === "on",
        };

        // Chama o service para criar o local e captura o ID gerado
        const createdVenue = await createVenue(newVenueData);
        finalVenueId = createdVenue.id;
      } else if (!finalVenueId) {
        alert("Por favor, selecione um local ou cadastre um novo.");
        return;
      }

      // 2. PREPARAR DADOS DO EVENTO
      const dataInicio = formData.get("dataInicio") as string;
      const horaInicio = formData.get("horaInicio") as string;
      const startsAt = `${dataInicio}T${horaInicio}:00`;

      let dataFim = formData.get("dataFim") as string;
      let horaFim = formData.get("horaFim") as string;
      let endsAt = "";
      
      if (dataFim && horaFim) {
        endsAt = `${dataFim}T${horaFim}:00`;
      } else {
        const d = new Date(startsAt);
        d.setHours(d.getHours() + 4);
        endsAt = d.toISOString().slice(0, 19); 
      }

      const eventoData = {
        eventName: formData.get("titulo") as string,
        description: formData.get("descricao") as string,
        contentRating: formData.get("classificacaoIndicativa") as string,
        price: ticketType === "paid" ? Number(formData.get("preco")) : 0,
        startsAt,
        endsAt,
        venueId: finalVenueId, // Usa o ID existente ou o que acabou de ser criado
        tagIds: selectedTagIds,
      };

      // 3. CRIAR O EVENTO FINAL
      await createEvent(eventoData);
      alert("Evento e Local publicados com sucesso!");
      router.push("/feed");

    } catch (error) {
      console.error("Erro no processo de criação:", error);
    }
  };

  return (
    <div className="flex h-screen w-full bg-[#f9fafb] font-sans text-gray-800 overflow-hidden">
      <main className="flex-1 flex flex-col h-screen overflow-hidden">
        <div className="flex-1 overflow-y-auto p-8">
          <div className="max-w-6xl mx-auto">
            
            <div className="flex justify-between items-end mb-8">
              <div>
                <h2 className="text-3xl font-bold text-gray-900 mb-1">Cadastrar Novo Evento</h2>
                <p className="text-gray-500 text-sm">Preencha os detalhes abaixo para publicar seu evento cultural em Sobral.</p>
              </div>
              <div className="flex gap-3">
                <button type="button" onClick={() => alert("Em breve")} className="cursor-pointer px-5 py-2.5 text-sm font-semibold text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors shadow-sm">
                  Salvar Rascunho
                </button>
                <button 
                  type="submit" 
                  form="form-evento" 
                  disabled={isProcessing}
                  className={`flex items-center gap-2 px-5 py-2.5 text-sm font-semibold text-white border border-transparent rounded-lg transition-colors shadow-sm ${isProcessing ? 'bg-gray-400 cursor-not-allowed' : 'bg-[#b91c1c] hover:bg-[#991b1b] cursor-pointer'}`}
                >
                  <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}><path strokeLinecap="round" strokeLinejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12" /></svg>
                  {isProcessing ? "Processando..." : "Publicar Evento"}
                </button>
              </div>
            </div>

            <form id="form-evento" onSubmit={handlePublicarEvento} className="grid grid-cols-1 lg:grid-cols-3 gap-6 pb-20">
              
              <div className="lg:col-span-2 space-y-6">
                
                {/* Informações Básicas */}
                <div className="bg-white rounded-xl border border-gray-200 p-6 shadow-sm">
                  <div className="flex items-center gap-2 mb-6">
                    <svg className="w-5 h-5 text-[#b91c1c]" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}><path strokeLinecap="round" strokeLinejoin="round" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
                    <h3 className="text-lg font-bold text-gray-900">Informações Básicas</h3>
                  </div>
                  <div className="space-y-4">
                    <div>
                      <label className="block text-sm font-bold text-gray-700 mb-1">Título do Evento *</label>
                      <input required name="titulo" type="text" placeholder="ex: Festival de Quadrilhas de Sobral" className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-[#b91c1c] outline-none transition-colors" />
                    </div>
                    <div>
                      <label className="block text-sm font-bold text-gray-700 mb-1">Descrição *</label>
                      <textarea required rows={4} name="descricao" placeholder="Descreva o evento, atrações e importância cultural..." className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-[#b91c1c] outline-none transition-colors resize-none"></textarea>
                    </div>
                    <div>
                      <label className="block text-sm font-bold text-gray-700 mb-2">Tags do Evento</label>
                      <div className="flex flex-wrap gap-2">
                        {availableTags.map(tag => (
                          <button
                            key={tag.id} 
                            type="button"
                            onClick={() => toggleTag(tag.id)}
                            className={`cursor-pointer px-4 py-1 text-xs font-semibold rounded-full border transition-colors ${selectedTagIds.includes(tag.id) ? 'bg-red-50 text-[#b91c1c] border-red-200' : 'bg-white text-gray-600 border-gray-300 hover:bg-gray-50'}`}
                          >
                            {tag.tagName}
                          </button>
                        ))}
                      </div>
                    </div>
                  </div>
                </div>

                {/* Data e Hora */}
                <div className="bg-white rounded-xl border border-gray-200 p-6 shadow-sm">
                  <div className="flex items-center gap-2 mb-6">
                    <svg className="w-5 h-5 text-[#b91c1c]" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}><path strokeLinecap="round" strokeLinejoin="round" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
                    <h3 className="text-lg font-bold text-gray-900">Data e Hora</h3>
                  </div>
                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <label className="block text-sm font-bold text-gray-700 mb-1">Data de Início *</label>
                      <input required name="dataInicio" type="date" className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm text-gray-500 focus:ring-[#b91c1c] outline-none" />
                    </div>
                    <div>
                      <label className="block text-sm font-bold text-gray-700 mb-1">Hora de Início *</label>
                      <input required name="horaInicio" type="time" className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm text-gray-500 focus:ring-[#b91c1c] outline-none" />
                    </div>
                    <div>
                      <label className="block text-sm font-bold text-gray-700 mb-1">Data de Término</label>
                      <input name="dataFim" type="date" className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm text-gray-500 focus:ring-[#b91c1c] outline-none" />
                    </div>
                    <div>
                      <label className="block text-sm font-bold text-gray-700 mb-1">Hora de Término</label>
                      <input name="horaFim" type="time" className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm text-gray-500 focus:ring-[#b91c1c] outline-none" />
                    </div>
                  </div>
                </div>

                {/* Localização com Toggle (Existente ou Novo) */}
                <div className="bg-white rounded-xl border border-gray-200 p-6 shadow-sm">
                  <div className="flex items-center justify-between mb-6">
                    <div className="flex items-center gap-2">
                      <svg className="w-5 h-5 text-[#b91c1c]" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}><path strokeLinecap="round" strokeLinejoin="round" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" /><path strokeLinecap="round" strokeLinejoin="round" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" /></svg>
                      <h3 className="text-lg font-bold text-gray-900">Localização</h3>
                    </div>
                    
                    {/* Toggle de Seleção de Modo */}
                    <div className="flex bg-gray-100 p-1 rounded-lg">
                      <button 
                        type="button" 
                        onClick={() => setVenueMode("existing")}
                        className={`text-xs px-3 py-1.5 rounded-md font-bold transition-colors cursor-pointer ${venueMode === 'existing' ? 'bg-white shadow-sm text-[#b91c1c]' : 'text-gray-500 hover:text-gray-700'}`}
                      >
                        Local Existente
                      </button>
                      <button 
                        type="button" 
                        onClick={() => setVenueMode("new")}
                        className={`text-xs px-3 py-1.5 rounded-md font-bold transition-colors cursor-pointer ${venueMode === 'new' ? 'bg-white shadow-sm text-[#b91c1c]' : 'text-gray-500 hover:text-gray-700'}`}
                      >
                        Cadastrar Novo
                      </button>
                    </div>
                  </div>
                  
                  {venueMode === "existing" ? (
                    // MODO: LOCAL EXISTENTE
                    <div>
                      <label className="block text-sm font-bold text-gray-700 mb-1">Selecione o Local *</label>
                      <select 
                        required={venueMode === "existing"}
                        value={selectedVenueId}
                        onChange={(e) => setSelectedVenueId(e.target.value)}
                        className="cursor-pointer w-full px-3 py-2 border border-gray-300 rounded-lg text-sm text-gray-700 bg-white focus:ring-[#b91c1c] outline-none appearance-none"
                      >
                        <option value="" disabled>Escolha um local cadastrado...</option>
                        {venues.map(v => (
                          <option key={v.id} value={v.id}>{v.name} - {v.neighborhood}</option>
                        ))}
                      </select>
                      
                      {selectedVenue && (
                        <div className="mt-4 p-3 bg-gray-50 rounded-lg border border-gray-100 text-sm">
                          <p className="text-gray-600">{selectedVenue.street}, {selectedVenue.number} - {selectedVenue.neighborhood}, {selectedVenue.city}</p>
                        </div>
                      )}
                    </div>
                  ) : (
                    // MODO: NOVO LOCAL
                    <div className="space-y-4">
                      <div>
                        <label className="block text-sm font-bold text-gray-700 mb-1">Nome do Local *</label>
                        <input required={venueMode === "new"} name="novoLocalNome" type="text" placeholder="ex: Theatro São João" className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-[#b91c1c] outline-none" />
                      </div>
                      <div className="grid grid-cols-4 gap-4">
                        <div className="col-span-3">
                          <label className="block text-sm font-bold text-gray-700 mb-1">Rua *</label>
                          <input required={venueMode === "new"} name="rua" type="text" placeholder="Praça São João" className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-[#b91c1c] outline-none" />
                        </div>
                        <div className="col-span-1">
                          <label className="block text-sm font-bold text-gray-700 mb-1">Número *</label>
                          <input required={venueMode === "new"} name="numero" type="text" placeholder="156" className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-[#b91c1c] outline-none" />
                        </div>
                      </div>
                      <div className="grid grid-cols-2 gap-4">
                        <div>
                          <label className="block text-sm font-bold text-gray-700 mb-1">Bairro *</label>
                          <input required={venueMode === "new"} name="bairro" type="text" placeholder="Centro" className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-[#b91c1c] outline-none" />
                        </div>
                        <div>
                          <label className="block text-sm font-bold text-gray-700 mb-1">Cidade *</label>
                          <input required={venueMode === "new"} name="cidade" type="text" defaultValue="Sobral" className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-[#b91c1c] outline-none" />
                        </div>
                      </div>
                    </div>
                  )}
                </div>

              </div>

              {/* Coluna Direito */}
              <div className="space-y-6">
                
                {/* Infraestrutura Dinâmica */}
                <div className="bg-white rounded-xl border border-gray-200 p-6 shadow-sm">
                  <div className="flex items-center gap-2 mb-4">
                    <svg className="w-5 h-5 text-[#b91c1c]" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}><path strokeLinecap="round" strokeLinejoin="round" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" /></svg>
                    <h3 className="text-lg font-bold text-gray-900">Infraestrutura do Local</h3>
                  </div>

                  {venueMode === "existing" ? (
                    // Mostrar infra do local selecionado (Apenas Leitura)
                    <div className="space-y-2">
                      {!selectedVenue ? (
                        <p className="text-xs text-gray-400">Selecione um local para ver sua infraestrutura.</p>
                      ) : (
                        <>
                          <div className="flex flex-wrap gap-2">
                            {selectedVenue.hasAccessibility && <span className="bg-blue-100 text-blue-700 px-2 py-1 rounded text-xs font-semibold">♿ Acessibilidade</span>}
                            {selectedVenue.hasParkingLot && <span className="bg-gray-200 text-gray-700 px-2 py-1 rounded text-xs font-semibold">🅿️ Estacionamento</span>}
                            {selectedVenue.hasFoodsAndDrinks && <span className="bg-orange-100 text-orange-700 px-2 py-1 rounded text-xs font-semibold">🍔 Alimentação</span>}
                            {selectedVenue.hasBathroom && <span className="bg-teal-100 text-teal-700 px-2 py-1 rounded text-xs font-semibold">🚻 Banheiros</span>}
                            {(!selectedVenue.hasAccessibility && !selectedVenue.hasParkingLot && !selectedVenue.hasFoodsAndDrinks && !selectedVenue.hasBathroom) && (
                              <span className="text-gray-500 text-xs">Nenhuma infraestrutura especial.</span>
                            )}
                          </div>
                          <p className="text-[11px] text-gray-400 mt-2">* A infraestrutura é herdada do local selecionado.</p>
                        </>
                      )}
                    </div>
                  ) : (
                    // Mostrar Checkboxes para preencher o novo local
                    <div className="space-y-3">
                      <p className="text-xs text-gray-500 mb-3">Marque o que este novo local oferece:</p>
                      <label className="flex items-center gap-3 cursor-pointer group">
                        <input name="acessibilidade" type="checkbox" className="cursor-pointer w-4 h-4 text-[#b91c1c] border-gray-300 rounded focus:ring-[#b91c1c]" />
                        <span className="text-sm font-medium text-gray-700 flex items-center gap-2">♿ Acessibilidade</span>
                      </label>
                      <label className="flex items-center gap-3 cursor-pointer group">
                        <input name="estacionamento" type="checkbox" className="cursor-pointer w-4 h-4 text-[#b91c1c] border-gray-300 rounded focus:ring-[#b91c1c]" />
                        <span className="text-sm font-medium text-gray-700 flex items-center gap-2">🅿️ Estacionamento</span>
                      </label>
                      <label className="flex items-center gap-3 cursor-pointer group">
                        <input name="alimentacao" type="checkbox" className="cursor-pointer w-4 h-4 text-[#b91c1c] border-gray-300 rounded focus:ring-[#b91c1c]" />
                        <span className="text-sm font-medium text-gray-700 flex items-center gap-2">🍔 Alimentação</span>
                      </label>
                      <label className="flex items-center gap-3 cursor-pointer group">
                        <input name="banheiros" type="checkbox" className="cursor-pointer w-4 h-4 text-[#b91c1c] border-gray-300 rounded focus:ring-[#b91c1c]" />
                        <span className="text-sm font-medium text-gray-700 flex items-center gap-2">🚻 Banheiros</span>
                      </label>
                    </div>
                  )}
                </div>

                {/* Ingressos */}
                <div className="bg-white rounded-xl border border-gray-200 p-6 shadow-sm">
                  <div className="flex items-center gap-2 mb-6">
                    <svg className="w-5 h-5 text-[#b91c1c]" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}><path strokeLinecap="round" strokeLinejoin="round" d="M15 5v2m0 4v2m0 4v2M5 5a2 2 0 00-2 2v3a2 2 0 110 4v3a2 2 0 002 2h14a2 2 0 002-2v-3a2 2 0 110-4V7a2 2 0 00-2-2H5z" /></svg>
                    <h3 className="text-lg font-bold text-gray-900">Ingressos</h3>
                  </div>

                  <div className="space-y-3 mb-4">
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
                        </div>
                      </div>
                      <div className={`w-4 h-4 rounded-full border-2 flex items-center justify-center ${ticketType === 'free' ? 'border-[#b91c1c]' : 'border-gray-300'}`}>
                        {ticketType === 'free' && <div className="w-2 h-2 bg-[#b91c1c] rounded-full"></div>}
                      </div>
                    </div>

                    <div 
                      onClick={() => setTicketType("paid")}
                      className={`flex items-center justify-between p-3 border rounded-lg cursor-pointer transition-colors ${ticketType === 'paid' ? 'border-[#b91c1c] bg-red-50/30' : 'border-gray-200 hover:border-gray-300'}`}
                    >
                      <div className="flex items-center gap-3">
                        <div className={`p-2 rounded-full transition-colors ${ticketType === 'paid' ? 'bg-[#b91c1c] text-white' : 'bg-gray-100 text-gray-500'}`}>
                          <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}><path strokeLinecap="round" strokeLinejoin="round" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z" /></svg>
                        </div>
                        <div>
                          <p className="text-sm font-bold text-gray-900">Entrada Paga</p>
                        </div>
                      </div>
                      <div className={`w-4 h-4 rounded-full border-2 flex items-center justify-center ${ticketType === 'paid' ? 'border-[#b91c1c]' : 'border-gray-300'}`}>
                        {ticketType === 'paid' && <div className="w-2 h-2 bg-[#b91c1c] rounded-full"></div>}
                      </div>
                    </div>
                  </div>

                  {ticketType === "paid" && (
                    <div className="mb-4">
                      <label className="block text-sm font-bold text-gray-700 mb-1">Valor do Ingresso (R$) *</label>
                      <input required name="preco" type="number" step="0.01" min="0" placeholder="ex: 50.00" className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-[#b91c1c] outline-none" />
                    </div>
                  )}
                </div>

                {/* Classificação Indicativa */}
                <div className="bg-white rounded-xl border border-gray-200 p-6 shadow-sm">
                  <div className="flex items-center gap-2 mb-4">
                    <svg className="w-5 h-5 text-[#b91c1c]" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}><path strokeLinecap="round" strokeLinejoin="round" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" /></svg>
                    <h3 className="text-lg font-bold text-gray-900">Classificação Indicativa</h3>
                  </div>
                  <select name="classificacaoIndicativa" className="cursor-pointer w-full px-3 py-2 border border-gray-300 rounded-lg text-sm text-gray-700 bg-white focus:ring-[#b91c1c] outline-none appearance-none">
                    <option value="Livre">Livre (Todas as idades)</option>
                    <option value="10+">10 anos</option>
                    <option value="12+">12 anos</option>
                    <option value="14+">14 anos</option>
                    <option value="16+">16 anos</option>
                    <option value="18+">18 anos</option>
                  </select>
                </div>

                {/* Imagem de Capa */}
                <div className="bg-white rounded-xl border border-gray-200 p-6 shadow-sm">
                  <div className="flex items-center gap-2 mb-4">
                    <svg className="w-5 h-5 text-[#b91c1c]" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}><path strokeLinecap="round" strokeLinejoin="round" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" /></svg>
                    <h3 className="text-lg font-bold text-gray-900">Imagem de Capa</h3>
                  </div>
                  <div className="border-2 border-dashed border-[#b91c1c]/30 rounded-lg p-6 flex flex-col items-center justify-center text-center bg-red-50/20 hover:bg-red-50/50 transition-colors cursor-pointer">
                    <svg className="w-8 h-8 text-[#b91c1c] mb-2" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}><path strokeLinecap="round" strokeLinejoin="round" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" /></svg>
                    <p className="text-sm font-bold text-gray-700">Clique para fazer upload</p>
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