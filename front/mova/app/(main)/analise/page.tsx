"use client";
import { useState } from "react";

interface MetricSummary {
  icon: string;
  label: string;
  value: string;
  trend: string;
  isPositive: boolean;
  colorClass: string;
}

interface TopCategory {
  name: string;
  percent: number;
  colorClass: string;
}

interface FeaturedEventDTO {
  id: string;
  eventName: string;
  participants: number;
  revenue: string;
  status: "Concluído" | "Em andamento" | "Planejado";
  coverImageUrl: string;
}

export default function MetricsPage() {
  // ESTADOS DA TELA
  const [timeRange, setTimeRange] = useState<"30" | "90" | "365">("30");

  // DADOS SIMULADOS
  const kpis: MetricSummary[] = [
    { icon: "calendar_month", label: "Total de Eventos", value: "128", trend: "+12%", isPositive: true, colorClass: "text-mova-red bg-mova-red/10" },
    { icon: "group", label: "Novos Usuários", value: "1.402", trend: "+8%", isPositive: true, colorClass: "text-mova-yellow-dark bg-mova-yellow/20" },
    { icon: "favorite", label: "Engajamento", value: "84%", trend: "+5%", isPositive: true, colorClass: "text-blue-600 bg-blue-50" },
    { icon: "confirmation_number", label: "Ingressos", value: "3.150", trend: "-2%", isPositive: false, colorClass: "text-gray-600 bg-gray-100" },
  ];

  const topCategories: TopCategory[] = [
    { name: "Música", percent: 72, colorClass: "bg-mova-red" },
    { name: "Teatro", percent: 45, colorClass: "bg-mova-yellow" },
    { name: "Artes Visuais", percent: 30, colorClass: "bg-blue-500" },
  ];

  // A categoria que mais cresceu e seu percentual (dinâmico baseado no mock acima)
  const fastestGrowingCategory = { name: "Teatro", growth: "15%" };

  const featuredEvents: FeaturedEventDTO[] = [
    {
      id: "1",
      eventName: "Festival Sertão Vivo",
      participants: 842,
      revenue: "R$ 12k",
      status: "Concluído",
      coverImageUrl: "https://images.unsplash.com/photo-1511192336575-5a79af67a629?w=150"
    },
    {
      id: "2",
      eventName: "Noite do Rock Sobralense",
      participants: 1205,
      revenue: "R$ 18k",
      status: "Em andamento",
      coverImageUrl: "https://images.unsplash.com/photo-1460661419201-fd4cecdf8a8b?w=150"
    },
    {
      id: "3",
      eventName: "Exposição Cores da Terra",
      participants: 450,
      revenue: "R$ 5k",
      status: "Planejado",
      coverImageUrl: "https://images.unsplash.com/photo-1507676184212-d03ab07a01bf?w=150"
    }
  ];

  // LÓGICA DO GRÁFICO BASEADA NO FILTRO DE TEMPO
  const getChartConfig = () => {
    switch (timeRange) {
      case "30":
        return {
          subtitle: "Últimas 4 semanas",
          bars: [
            { label: "Semana 1", height: "40%", opacity: "bg-mova-red/30" },
            { label: "Semana 2", height: "60%", opacity: "bg-mova-red/50" },
            { label: "Semana 3", height: "55%", opacity: "bg-mova-red/70" },
            { label: "Semana 4", height: "85%", opacity: "bg-mova-red" }
          ]
        };
      case "90":
        return {
          subtitle: "Últimas 12 semanas",
          bars: Array.from({ length: 12 }, (_, i) => ({
            label: `Sem. ${i + 1}`,
            height: `${Math.floor(Math.random() * 60) + 30}%`, // Altura aleatória para simular
            opacity: "bg-mova-red/60"
          }))
        };
      case "365":
        const months = ["Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"];
        return {
          subtitle: "Últimos 12 meses",
          bars: months.map(month => ({
            label: month,
            height: `${Math.floor(Math.random() * 70) + 30}%`,
            opacity: "bg-mova-red/80"
          }))
        };
    }
  };

  const chartConfig = getChartConfig();

  // Função auxiliar para cor das badges de status da tabela
  const getStatusBadge = (status: string) => {
    switch (status) {
      case "Concluído":
        return "bg-blue-50 text-blue-600";
      case "Em andamento":
        return "bg-red-50 text-mova-red";
      case "Planejado":
        return "bg-orange-50 text-orange-600";
      default:
        return "bg-gray-50 text-gray-600";
    }
  };

  return (
    <div className="w-[calc(100vw-224px)] min-h-screen bg-gray-50/30 p-8 font-sans antialiased text-foreground">
      <div className="max-w-6xl mx-auto w-full flex flex-col gap-8">
        
        {/*HEADER E FILTROS DE TEMPO*/}
        <div className="flex flex-col md:flex-row justify-between items-start md:items-end gap-4">
          <div>
            <h1 className="text-3xl font-extrabold text-mova-dark tracking-tight leading-tight">
              Métricas de<br />Performance
            </h1>
            <p className="text-sm text-gray-500 mt-2 font-medium">
              Visão geral do ecossistema cultural de Sobral.
            </p>
          </div>

          <div className="flex items-center gap-4">
            {/* Seletor de Período */}
            <div className="flex bg-gray-100/80 p-1 rounded-xl border border-gray-200/50 shadow-inner">
              <button
                onClick={() => setTimeRange("30")}
                className={`px-5 py-2 rounded-lg text-xs font-semibold transition-all duration-200 cursor-pointer ${
                  timeRange === "30" ? "bg-white shadow-sm text-mova-dark" : "text-gray-500 hover:text-gray-700"
                }`}
              >
                30 dias
              </button>
              <button
                onClick={() => setTimeRange("90")}
                className={`px-5 py-2 rounded-lg text-xs font-semibold transition-all duration-200 cursor-pointer ${
                  timeRange === "90" ? "bg-white shadow-sm text-mova-dark" : "text-gray-500 hover:text-gray-700"
                }`}
              >
                90 dias
              </button>
              <button
                onClick={() => setTimeRange("365")}
                className={`px-5 py-2 rounded-lg text-xs font-semibold transition-all duration-200 cursor-pointer ${
                  timeRange === "365" ? "bg-white shadow-sm text-mova-dark" : "text-gray-500 hover:text-gray-700"
                }`}
              >
                1 ano
              </button>
            </div>

            {/* Botão Exportar */}
            <button className="inline-flex items-center gap-2 px-4 py-2 border border-blue-200 text-blue-600 bg-blue-50/30 hover:bg-blue-50 rounded-xl text-sm font-semibold transition-colors cursor-pointer shadow-sm">
              <span className="material-symbols-outlined" style={{ fontSize: 18 }}>download</span>
              Exportar Dados
            </button>
          </div>
        </div>

        {/*CARDS DE KPI*/}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mt-2">
          {kpis.map((kpi, index) => (
            <div key={index} className="bg-white border border-gray-100 rounded-2xl p-6 shadow-sm hover:shadow-md transition-shadow relative">
              <div className="flex justify-between items-start mb-4">
                <div className={`w-10 h-10 rounded-xl flex items-center justify-center ${kpi.colorClass}`}>
                  <span className="material-symbols-outlined" style={{ fontSize: 20 }}>{kpi.icon}</span>
                </div>
                <span className={`text-xs font-bold px-2 py-1 rounded-md ${
                  kpi.isPositive ? "bg-red-50 text-mova-red" : "bg-gray-100 text-gray-600"
                }`}>
                  {kpi.trend}
                </span>
              </div>
              <p className="text-xs font-semibold text-gray-500 uppercase tracking-wider">{kpi.label}</p>
              <h3 className="text-2xl font-bold text-mova-dark mt-1">{kpi.value}</h3>
            </div>
          ))}
        </div>

        {/*SEÇÃO CENTRAL: GRÁFICO E CATEGORIAS*/}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          
          {/* GRÁFICO DE USUÁRIOS */}
          <div className="bg-white border border-gray-100 rounded-2xl p-6 shadow-sm lg:col-span-2 flex flex-col">
            <div className="flex justify-between items-center mb-8">
              <h2 className="text-lg font-bold text-mova-dark">Usuários ao Longo do Tempo</h2>
              <span className="text-xs font-semibold text-gray-500">{chartConfig.subtitle}</span>
            </div>
            
            {/* Visualização de Gráfico em CSS Puro */}
            <div className="flex-1 flex items-end gap-2 h-48 mt-auto pb-6 border-b border-gray-100">
              {chartConfig.bars.map((bar, index) => (
                <div key={index} className="flex-1 flex flex-col justify-end items-center group h-full relative">
                  <div className="absolute -top-8 bg-mova-dark text-white text-xs py-1 px-2 rounded opacity-0 group-hover:opacity-100 transition-opacity pointer-events-none">
                    {bar.height}
                  </div>
                  {/* Barra */}
                  <div 
                    className={`w-full rounded-t-lg transition-all duration-500 ${bar.opacity} hover:opacity-100`}
                    style={{ height: bar.height }}
                  ></div>
                </div>
              ))}
            </div>
            {/* Labels do eixo X */}
            <div className="flex justify-between mt-4">
              {chartConfig.bars.map((bar, index) => (
                <span key={index} className="text-[10px] font-semibold text-gray-400 flex-1 text-center truncate px-1">
                  {bar.label}
                </span>
              ))}
            </div>
          </div>

          {/* CATEGORIAS POPULARES */}
          <div className="bg-white border border-gray-100 rounded-2xl p-6 shadow-sm flex flex-col">
            <h2 className="text-lg font-bold text-mova-dark mb-6">Categorias Populares</h2>
            
            <div className="space-y-5">
              {topCategories.map((cat, index) => (
                <div key={index}>
                  <div className="flex justify-between text-xs font-bold mb-1">
                    <span className="text-gray-600">{cat.name}</span>
                    <span className="text-mova-dark">{cat.percent}%</span>
                  </div>
                  <div className="w-full h-2 bg-gray-100 rounded-full overflow-hidden">
                    <div 
                      className={`h-full rounded-full ${cat.colorClass}`} 
                      style={{ width: `${cat.percent}%` }}
                    ></div>
                  </div>
                </div>
              ))}
            </div>

            {/* Mensagem de Insight Dinâmica */}
            <div className="mt-auto pt-8 flex flex-col items-center text-center">
              <span className="material-symbols-outlined text-mova-red/40 mb-2" style={{ fontSize: 32 }}>
                auto_awesome
              </span>
              <p className="text-xs font-medium text-gray-600 leading-relaxed px-4">
                Sobral experimentou um crescimento de <span className="font-bold text-mova-dark">{fastestGrowingCategory.growth}</span> na demanda por <span className="font-bold text-mova-dark">{fastestGrowingCategory.name}</span> este mês.
              </p>
            </div>
          </div>
        </div>

        {/*EVENTOS EM DESTAQUE (TABELA)*/}
        <div className="bg-white border border-gray-100 rounded-2xl shadow-sm overflow-hidden mb-8">
          <div className="p-6 border-b border-gray-100 flex justify-between items-center">
            <h2 className="text-lg font-bold text-mova-dark">Eventos em Destaque</h2>
            <button className="text-sm font-semibold text-mova-red hover:underline cursor-pointer">
              Ver todos
            </button>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="bg-white border-b border-gray-100">
                  <th className="p-4 pl-6 text-xs font-semibold uppercase text-gray-400 tracking-wider w-[40%]">Evento</th>
                  <th className="p-4 text-xs font-semibold uppercase text-gray-400 tracking-wider text-center">Participantes</th>
                  <th className="p-4 text-xs font-semibold uppercase text-gray-400 tracking-wider text-center">Receita Bruta</th>
                  <th className="p-4 text-xs font-semibold uppercase text-gray-400 tracking-wider text-center">Status</th>
                  <th className="p-4 pr-6 text-xs font-semibold uppercase text-gray-400 tracking-wider text-center">Ações</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100">
                {featuredEvents.map((event) => (
                  <tr key={event.id} className="hover:bg-gray-50/50 transition-colors group">
                    <td className="p-4 pl-6">
                      <div className="flex items-center gap-3">
                        <div className="w-10 h-10 rounded-lg overflow-hidden shrink-0 shadow-sm">
                          <img src={event.coverImageUrl} alt={event.eventName} className="w-full h-full object-cover transition-transform group-hover:scale-105" />
                        </div>
                        <h4 className="font-semibold text-mova-dark text-sm leading-snug">{event.eventName}</h4>
                      </div>
                    </td>

                    <td className="p-4 text-sm text-gray-600 font-medium text-center">
                      {event.participants.toLocaleString('pt-BR')}
                    </td>

                    <td className="p-4 text-sm font-bold text-mova-dark text-center">
                      {event.revenue}
                    </td>

                    <td className="p-4 text-sm text-center">
                      <span className={`inline-flex items-center px-2.5 py-1 rounded-md text-xs font-bold ${getStatusBadge(event.status)}`}>
                        {event.status}
                      </span>
                    </td>

                    <td className="p-4 pr-6 text-right">
                      <div className="flex items-center justify-center gap-1.5">
                        <button 
                          className="p-1.5 text-gray-400 hover:text-mova-dark hover:bg-gray-100 rounded-lg transition-all cursor-pointer" 
                          title="Visualizar"
                        >
                          <span className="material-symbols-outlined" style={{ fontSize: 18 }}>visibility</span>
                        </button>
                        <button 
                          className="p-1.5 text-gray-400 hover:text-mova-yellow-dark hover:bg-mova-yellow/10 rounded-lg transition-all cursor-pointer" 
                          title="Editar"
                        >
                          <span className="material-symbols-outlined" style={{ fontSize: 18 }}>edit</span>
                        </button>
                        <button 
                          className="p-1.5 text-gray-400 hover:text-mova-red hover:bg-mova-red/10 rounded-lg transition-all cursor-pointer" 
                          title="Excluir"
                        >
                          <span className="material-symbols-outlined" style={{ fontSize: 18 }}>delete</span>
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
        
      </div>
    </div>
  );
}