"use client";
import { useState } from "react";

// Definição da Interface baseada na classe Event do Java backend
interface Tag {
  id: string;
  name: string;
}

interface Venue {
  id: string;
  name: string;
  city: string;
}

interface EventPicture {
  id: string;
  url: string;
}

interface Event {
  id: string;
  eventName: string;
  description: string;
  contentRating: string;
  price: number;
  startsAt: string;
  endsAt: string;
  createdAt: string;
  updatedAt: string;
  venue: Venue;
  tags: Tag[];
  pictures: EventPicture[];
}

// Eventos de exemplo para popular a tela inicialmente (Simulando dados do backend)
// Feito para testar o filtro e a paginação, garantindo que tenhamos mais de 4 eventos para ver o efeito da 
// divisão em páginas.
export default function GestaoEventosPage() {
  const [events, setEvents] = useState<Event[]>([
    {
      id: "6f5b3310-410c-4033-87bb-e8e3c15d71c4",
      eventName: "Festival de Jazz de Sobral",
      description: "O melhor do jazz instrumental na região Norte.",
      contentRating: "Livre",
      price: 0.00,
      startsAt: "2024-10-15T20:00:00",
      endsAt: "2024-10-17T23:59:00",
      createdAt: "2024-01-10T12:00:00",
      updatedAt: "2024-01-10T12:00:00",
      venue: { id: "1", name: "Teatro São João", city: "Sobral" },
      tags: [{ id: "1", name: "Música" }],
      pictures: [{ id: "p1", url: "https://images.unsplash.com/photo-1511192336575-5a79af67a629?w=150" }]
    },
    {
      id: "7a2c1409-5a12-4211-9ffc-1234a56b78c9",
      eventName: "Exposição Cores do Sertão",
      description: "Mostra de artes plásticas regionais.",
      contentRating: "Livre",
      price: 15.00,
      startsAt: "2024-09-02T09:00:00",
      endsAt: "2024-09-30T18:00:00",
      createdAt: "2024-02-15T14:30:00",
      updatedAt: "2024-02-15T14:30:00",
      venue: { id: "2", name: "Casa da Cultura", city: "Sobral" },
      tags: [{ id: "2", name: "Artes Visuais" }],
      pictures: [{ id: "p2", url: "https://images.unsplash.com/photo-1460661419201-fd4cecdf8a8b?w=150" }]
    },
    {
      id: "8f1409ab-2b3c-4d5e-8f9a-bcde12345678",
      eventName: "Peça: O Auto da Compadecida",
      description: "Clássico de Ariano Suassuna.",
      contentRating: "12+",
      price: 40.00,
      startsAt: "2024-11-22T19:00:00",
      endsAt: "2024-11-22T21:30:00",
      createdAt: "2024-03-20T10:15:00",
      updatedAt: "2024-03-22T11:00:00",
      venue: { id: "3", name: "Arena Sobral", city: "Sobral" },
      tags: [{ id: "3", name: "Teatro" }],
      pictures: [{ id: "p3", url: "https://images.unsplash.com/photo-1507676184212-d03ab07a01bf?w=150" }]
    },
    {
      id: "id-teste-01",
      eventName: "Evento 123",
      description: "Mostra de artes plásticas regionais.",
      contentRating: "Livre",
      price: 15.00,
      startsAt: "2024-09-02T09:00:00",
      endsAt: "2024-09-30T18:00:00",
      createdAt: "2024-02-15T14:30:00",
      updatedAt: "2024-02-15T14:30:00",
      venue: { id: "2", name: "Casa da Cultura", city: "Sobral" },
      tags: [{ id: "2", name: "Artes Visuais" }],
      pictures: [{ id: "p2", url: "https://images.unsplash.com/photo-1460661419201-fd4cecdf8a8b?w=150" }]
    },
    {
      id: "id-teste-02", // ID corrigido para não repetir
      eventName: "Outro Evento",
      description: "Mostra de artes plásticas regionais.",
      contentRating: "Livre",
      price: 15.00,
      startsAt: "2024-09-02T09:00:00",
      endsAt: "2024-09-30T18:00:00",
      createdAt: "2024-02-15T14:30:00",
      updatedAt: "2024-02-15T14:30:00",
      venue: { id: "2", name: "Casa da Cultura", city: "Sobral" },
      tags: [{ id: "2", name: "Música" }],
      pictures: [{ id: "p2", url: "https://images.unsplash.com/photo-1460661419201-fd4cecdf8a8b?w=150" }]
    },
  ]);

  // ESTADOS DA TELA
  const [searchTerm, setSearchTerm] = useState("");
  const [isFilterOpen, setIsFilterOpen] = useState(false);
  const [selectedCategory, setSelectedCategory] = useState<string | null>(null);
  const [currentPage, setCurrentPage] = useState(1);
  const ITEMS_PER_PAGE = 4;

  // Categorias retiradas das Preverencias do usuário, para facilitar os testes.
  const CATEGORIES = [
    "Música",
    "Teatro",
    "Artes Visuais",
    "Gastronomia",
    "Cinema",
    "Literatura",
    "Dança"
  ];

  // Função auxiliar para formatação de data
  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    const months = ["Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"];
    return `${date.getDate().toString().padStart(2, '0')} ${months[date.getMonth()]}, ${date.getFullYear()}`;
  };

  // Filtragem completa (Busca + Categoria)
  const filteredEvents = events.filter(event => {
    const matchesSearch = event.eventName.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesCategory = selectedCategory 
      ? event.tags.some(tag => tag.name === selectedCategory) 
      : true;

    return matchesSearch && matchesCategory;
  });

  // Lógica da Paginação baseada nos eventos já filtrados
  const totalPages = Math.ceil(filteredEvents.length / ITEMS_PER_PAGE);
  const startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
  const endIndex = startIndex + ITEMS_PER_PAGE;
  
  // Array final que vai para a tela (Corte dos 4 itens).
  const currentEvents = filteredEvents.slice(startIndex, endIndex);

  return (
    <div className="w-[calc(100vw-224px)] min-h-screen bg-gray-50/50 p-8 font-sans antialiased text-foreground">
      <div className="max-w-5xl mx-auto w-full">
        
        {/* HEADER DA PÁGINA */}
        <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 mb-12">
          <div>
            <h1 className="text-2xl font-bold text-mova-dark tracking-tight">Gestão de Eventos</h1>
            <p className="text-sm text-gray-500 mt-1">
              Painel administrativo para controle de sua produção cultural
            </p>
          </div>
          
          <button className="inline-flex items-center gap-2 bg-mova-red hover:bg-mova-red-dark text-white font-semibold px-5 py-2.5 rounded-xl transition-all duration-200 shadow-sm text-sm cursor-pointer">
            + Criar Novo Evento
          </button>
        </div>

        {/* CONTAINER DA LISTA E FILTROS */}
        <div className="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
          
          {/* BARRA DE FERRAMENTAS: BUSCA E FILTROS */}
          <div className="p-4 border-b border-gray-100 flex flex-col sm:flex-row gap-3 justify-between items-center bg-white">
            <div className="relative w-full sm:max-w-xs">
              <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">🔍</span>
              <input 
                type="text" 
                placeholder="Buscar por nome do evento..."
                value={searchTerm}
                onChange={(e) => {
                  setSearchTerm(e.target.value);
                  setCurrentPage(1); // Volta pra primeira página ao buscar
                }}
                className="w-full pl-9 pr-4 py-2 border border-gray-200 rounded-xl text-sm focus:outline-none focus:border-mova-red transition-colors placeholder:text-gray-400"
              />
            </div>

            <div className="flex gap-2 w-full sm:w-auto justify-end">
              
              {/* CONTAINER DO FILTRO (Relative para a caixinha flutuar aqui dentro) */}
              <div className="relative">
                <button 
                  onClick={() => setIsFilterOpen(!isFilterOpen)}
                  className={`inline-flex items-center gap-2 px-4 py-2 border rounded-xl text-sm font-medium transition-colors cursor-pointer ${
                    isFilterOpen || selectedCategory ? 'bg-gray-50 border-gray-300 text-mova-dark' : 'border-gray-200 text-gray-700 hover:bg-gray-50'
                  }`}
                >
                  ⚙ Filtros
                  {selectedCategory && <span className="w-2 h-2 rounded-full bg-mova-red"></span>}
                </button>

                {/* CAIXA DE OPÇÕES (Dropdown) */}
                {isFilterOpen && (
                  <div className="absolute right-0 mt-2 w-48 bg-white border border-gray-100 rounded-xl shadow-lg z-10 py-2">
                    <div className="px-4 py-2 text-xs font-semibold text-gray-400 uppercase tracking-wider">
                      Categorias
                    </div>
                    
                    <button
                      onClick={() => { 
                        setSelectedCategory(null); 
                        setIsFilterOpen(false);
                        setCurrentPage(1);
                      }}
                      className={`w-full text-left px-4 py-2 text-sm hover:bg-gray-50 transition-colors cursor-pointer ${
                        selectedCategory === null ? 'text-mova-red font-medium' : 'text-gray-600'
                      }`}
                    >
                      Todas as categorias
                    </button>

                    {CATEGORIES.map(category => (
                      <button
                        key={category}
                        onClick={() => { 
                          setSelectedCategory(category); 
                          setIsFilterOpen(false);
                          setCurrentPage(1); // Volta pra primeira página ao filtrar
                        }}
                        className={`w-full text-left px-4 py-2 text-sm hover:bg-gray-50 transition-colors cursor-pointer ${
                          selectedCategory === category ? 'text-mova-red font-medium' : 'text-gray-600'
                        }`}
                      >
                        {category}
                      </button>
                    ))}
                  </div>
                )}
              </div>

              {/* BOTÃO EXPORTAR */}
              <button className="inline-flex items-center gap-2 px-4 py-2 border border-gray-200 rounded-xl text-sm font-medium text-gray-700 hover:bg-gray-50 transition-colors cursor-pointer">
                ⬇ Exportar
              </button>
            </div>
          </div>

          {/* TABELA DE EVENTOS */}
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="bg-gray-50/70 border-b border-gray-100">
                  <th className="p-4 text-xs font-semibold uppercase text-gray-500 tracking-wider w-[40%]">Evento</th>
                  <th className="p-4 text-xs font-semibold uppercase text-gray-500 tracking-wider">Data</th>
                  <th className="p-4 text-xs font-semibold uppercase text-gray-500 tracking-wider">Preço</th>
                  <th className="p-4 text-xs font-semibold uppercase text-gray-500 tracking-wider text-right">Ações</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100">
                {currentEvents.map((event) => (
                  <tr key={event.id} className="hover:bg-gray-50/50 transition-colors group">
                    <td className="p-4">
                      <div className="flex items-center gap-3">
                        <div className="w-12 h-12 rounded-lg bg-gray-100 overflow-hidden shrink-0 border border-gray-200/60 shadow-sm">
                          {event.pictures && event.pictures[0] ? (
                            <img 
                              src={event.pictures[0].url} 
                              alt={event.eventName} 
                              className="w-full h-full object-cover transition-transform group-hover:scale-105"
                            />
                          ) : (
                            <div className="w-full h-full bg-mova-red/10 flex items-center justify-center font-bold text-mova-red">
                              {event.eventName.charAt(0)}
                            </div>
                          )}
                        </div>
                        <div>
                          <h4 className="font-semibold text-mova-dark text-sm leading-snug">{event.eventName}</h4>
                          <p className="text-xs text-gray-400 mt-0.5">{event.venue?.name}</p>
                        </div>
                      </div>
                    </td>

                    <td className="p-4 text-sm text-gray-600 font-medium">
                      {formatDate(event.startsAt)}
                    </td>

                    <td className="p-4 text-sm">
                      {event.price === 0 ? (
                        <span className="inline-flex items-center px-2 py-0.5 rounded-md text-xs font-semibold bg-green-50 text-green-700 border border-green-200/50">
                          Gratuito
                        </span>
                      ) : (
                        <span className="font-medium text-gray-700">
                          {event.price.toLocaleString("pt-BR", { style: "currency", currency: "BRL" })}
                        </span>
                      )}
                    </td>

                    <td className="p-4 text-right">
                      <div className="flex items-center justify-end gap-1.5">
                        <button className="p-1.5 text-gray-400 hover:text-mova-dark hover:bg-gray-100 rounded-lg transition-all cursor-pointer" title="Visualizar">
                          Ver
                        </button>
                        <button className="p-1.5 text-gray-400 hover:text-mova-yellow-dark hover:bg-mova-yellow/10 rounded-lg transition-all cursor-pointer" title="Editar">
                          Editar
                        </button>
                        <button className="p-1.5 text-gray-400 hover:text-mova-red hover:bg-mova-red/10 rounded-lg transition-all cursor-pointer" title="Excluir">
                          Excluir
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}

                {filteredEvents.length === 0 && (
                  <tr>
                    <td colSpan={4} className="p-8 text-center text-sm text-gray-400">
                      Nenhum evento encontrado.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>

          {/* RODAPÉ E PAGINAÇÃO */}
          <div className="p-4 border-t border-gray-100 flex flex-col sm:flex-row justify-between items-center gap-3 bg-gray-50/40">
            <p className="text-xs text-gray-500 font-medium">
              Exibindo {filteredEvents.length === 0 ? 0 : startIndex + 1} a {Math.min(endIndex, filteredEvents.length)} de {filteredEvents.length} eventos
            </p>
            
            {/* Só mostra a paginação se tiver mais de 1 página */}
            {totalPages > 1 && (
              <div className="flex items-center gap-1.5">
                
                {/* Botão Voltar */}
                <button 
                  onClick={() => setCurrentPage(prev => Math.max(prev - 1, 1))}
                  disabled={currentPage === 1}
                  className="p-1 rounded-lg border border-gray-200 hover:bg-white text-gray-500 disabled:opacity-50 transition-colors cursor-pointer"
                >
                  ‹
                </button>

                {/* Gerador automático dos números das páginas */}
                {Array.from({ length: totalPages }, (_, index) => index + 1).map((page) => (
                  <button
                    key={page}
                    onClick={() => setCurrentPage(page)}
                    className={`w-7 h-7 inline-flex items-center justify-center text-xs font-bold rounded-lg transition-colors cursor-pointer border ${
                      currentPage === page 
                        ? "bg-mova-red text-white border-mova-red" 
                        : "text-gray-600 hover:bg-white border-transparent hover:border-gray-200" 
                    }`}
                  >
                    {page}
                  </button>
                ))}

                {/* Botão Avançar */}
                <button 
                  onClick={() => setCurrentPage(prev => Math.min(prev + 1, totalPages))}
                  disabled={currentPage === totalPages}
                  className="p-1 rounded-lg border border-gray-200 hover:bg-white text-gray-500 disabled:opacity-50 transition-colors cursor-pointer"
                >
                  ›
                </button>
                
              </div>
            )}
          </div>

        </div>
      </div>
    </div>
  );
}