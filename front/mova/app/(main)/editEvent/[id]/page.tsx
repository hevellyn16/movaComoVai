"use client";

import { useState, useEffect, use } from "react";
import { useRouter } from "next/navigation";
import { useEvents } from "@/hooks/useEvents";
import { useTags } from "@/hooks/useTags";
import { useVenues } from "@/hooks/useVenues";
import { Event } from "@/types/event.types";
import { Venue } from "@/types/venue.types";

type TabType = "basico" | "agenda_local" | "opcoes";

const RATINGS = [
  "Livre",
  "10 anos",
  "12 anos",
  "14 anos",
  "16 anos",
  "18 anos",
];

export default function EditEventPage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  const { id } = use(params);
  const router = useRouter();

  // Hooks da API
  const { fetchEventById, updateEvent } = useEvents();
  const { fetchAllTags, tags: availableTags } = useTags();
  const {
    fetchAllVenues,
    createVenue,
    isLoading: isVenuesLoading,
  } = useVenues();

  // Estados de UI e Form
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [activeTab, setActiveTab] = useState<TabType>("basico");

  // Dados do Evento
  const [event, setEvent] = useState<Event | null>(null);
  const [eventName, setEventName] = useState("");
  const [selectedTag, setSelectedTag] = useState<string | null>(null);
  const [ticketType, setTicketType] = useState<"free" | "paid">("free");

  // Localização
  const [venuesList, setVenuesList] = useState<Venue[]>([]);
  const [venueMode, setVenueMode] = useState<"existing" | "new">("existing");
  const [selectedVenueId, setSelectedVenueId] = useState<string>("");

  const tabs = [
    { id: "basico", label: "1. Informações Básicas" },
    { id: "agenda_local", label: "2. Agenda e Local" },
    { id: "opcoes", label: "3. Opções" },
  ] as const;

  // Carregamento Inicial
  useEffect(() => {
    const loadEventData = async () => {
      try {
        fetchAllTags();
        const venuesData = await fetchAllVenues(0, 100);
        setVenuesList(venuesData.content);

        const eventData = await fetchEventById(id);
        setEvent(eventData);

        // Preenche os estados básicos com os dados atuais
        setEventName(eventData.eventName);
        setTicketType(eventData.price > 0 ? "paid" : "free");
        setSelectedVenueId(eventData.venue?.id || "");
        if (eventData.tags && eventData.tags.length > 0) {
          setSelectedTag(eventData.tags[0].tagname);
        }
      } catch (error) {
        console.error("Erro ao carregar evento:", error);
        alert("Não foi possível carregar os dados do evento.");
        router.push("/gestao");
      } finally {
        setIsLoading(false);
      }
    };

    loadEventData();
  }, [id]);

  const selectedVenue = venuesList.find((v) => v.id === selectedVenueId);

  // Helpers para formatar data e hora pro input value
  const getStartDate = () =>
    event?.startsAt ? event.startsAt.split("T")[0] : "";
  const getStartTime = () =>
    event?.startsAt ? event.startsAt.split("T")[1].substring(0, 5) : "";
  const getEndDate = () => (event?.endsAt ? event.endsAt.split("T")[0] : "");
  const getEndTime = () =>
    event?.endsAt ? event.endsAt.split("T")[1].substring(0, 5) : "";

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setIsSubmitting(true);
    const formData = new FormData(e.currentTarget);

    try {
      // 1. Resolve o Venue (Local)
      let finalVenueId = selectedVenueId;

      if (venueMode === "new") {
        const newVenue = await createVenue({
          name: formData.get("venueName") as string,
          street: formData.get("venueStreet") as string,
          number: formData.get("venueNumber") as string,
          neighborhood: formData.get("venueNeighborhood") as string,
          city: formData.get("venueCity") as string,
          landmark: (formData.get("venueLandmark") as string) || undefined,
          hasAccessibility: formData.get("hasAccessibility") === "on",
          hasParkingLot: formData.get("hasParkingLot") === "on",
          hasFoodsAndDrinks: formData.get("hasFoodsAndDrinks") === "on",
          hasBathroom: formData.get("hasBathroom") === "on",
        });
        finalVenueId = newVenue.id;
      }

      // 2. Resolve as Tags
      const tagId = availableTags.find((t) => t.tagName === selectedTag)?.id;

      // 3. Monta e Valida as Datas com segurança
      const startDate = formData.get("startDate") as string;
      const startTime = formData.get("startTime") as string;
      // Só monta a string se os dois valores existirem de verdade
      const startsAt =
        startDate && startTime ? `${startDate}T${startTime}:00` : undefined;

      const endDate = formData.get("endDate") as string;
      const endTime = formData.get("endTime") as string;
      const endsAt =
        endDate && endTime ? `${endDate}T${endTime}:00` : undefined;

      // 4. Monta o DTO de Atualização
      const updatePayload = {
        eventName: formData.get("eventName") as string,
        description: formData.get("description") as string,
        contentRating: formData.get("contentRating") as string,
        price: ticketType === "free" ? 0 : Number(formData.get("price")),
        startsAt, // Envia undefined caso falhe, evitando o erro no Spring Boot
        endsAt,
        venueId: finalVenueId,
        tagIds: tagId ? [tagId] : [],
      };

      await updateEvent(id, updatePayload);
      alert("Evento atualizado com sucesso!");
      router.push("/gestao");
    } catch (error) {
      console.error("Erro ao atualizar evento:", error);
      alert("Falha ao atualizar evento. Tente novamente.");
    } finally {
      setIsSubmitting(false);
    }
  }

  const amenities = [
    { name: "hasAccessibility", icon: "accessible", label: "Acessibilidade" },
    { name: "hasParkingLot", icon: "local_parking", label: "Estacionamento" },
    { name: "hasFoodsAndDrinks", icon: "restaurant", label: "Alimentação" },
    { name: "hasBathroom", icon: "wc", label: "Banheiros" },
  ] as const;

  if (isLoading || !event) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="animate-spin rounded-full h-10 w-10 border-t-2 border-b-2 border-[#b91c1c]"></div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col font-sans text-gray-800">
      {/* Header */}
      <header className="bg-white border-b border-gray-100 px-8 py-5 top-0 z-10 sticky">
        <div className="max-w-5xl mx-auto flex justify-between items-center">
          <div>
            <p className="text-xs font-bold tracking-wider text-[#b91c1c] uppercase mb-1">
              Modo de Edição
            </p>
            <h1 className="text-xl font-bold text-gray-900">
              {eventName || "Nome do Evento"}
            </h1>
          </div>
          <div className="flex gap-3">
            <button
              type="button"
              onClick={() => router.push("/gestao")}
              className="px-5 py-2 text-sm font-semibold text-gray-600 bg-white border border-gray-200 hover:bg-gray-50 rounded-xl transition-colors"
            >
              Cancelar
            </button>
            <button
              type="submit"
              form="edit-event-form"
              disabled={isSubmitting}
              className={`cursor-pointer px-6 py-2 text-sm font-semibold text-white rounded-xl transition-colors shadow-sm ${
                isSubmitting ? "bg-gray-400" : "bg-[#b91c1c] hover:bg-[#991b1b]"
              }`}
            >
              {isSubmitting ? "Salvando..." : "Salvar Alterações"}
            </button>
          </div>
        </div>
      </header>

      <main className="flex-1 w-full max-w-5xl mx-auto flex flex-col md:flex-row gap-8 p-8">
        {/* Menu lateral */}
        <aside className="w-full md:w-56 shrink-0">
          <nav className="flex flex-col gap-1 md:sticky top-32">
            {tabs.map((tab) => (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={`cursor-pointer text-left px-4 py-3 rounded-xl text-sm font-medium transition-colors ${
                  activeTab === tab.id
                    ? "bg-red-50 text-[#b91c1c]"
                    : "text-gray-600 hover:bg-gray-100"
                }`}
              >
                {tab.label}
              </button>
            ))}
          </nav>
        </aside>

        {/* Área de edição */}
        <section className="flex-1 bg-white border border-gray-100 rounded-2xl shadow-sm p-8">
          <form id="edit-event-form" onSubmit={handleSubmit}>
            {/* ABA 1: Informações Básicas */}
            <div
              className={`space-y-6 ${activeTab === "basico" ? "block" : "hidden"}`}
            >
              <h2 className="text-lg font-bold text-gray-900 border-b border-gray-100 pb-4">
                Informações Básicas
              </h2>

              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-1">
                  Nome do Evento
                </label>
                <input
                  required
                  name="eventName"
                  type="text"
                  value={eventName}
                  onChange={(e) => setEventName(e.target.value)}
                  className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:border-[#b91c1c] outline-none transition-all"
                />
              </div>

              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-1">
                  Descrição
                </label>
                <textarea
                  required
                  name="description"
                  defaultValue={event.description}
                  rows={5}
                  className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:border-[#b91c1c] outline-none transition-all resize-none"
                />
              </div>

              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">
                  Imagem de Capa
                </label>
                <div className="h-44 w-full rounded-xl bg-gray-100 border-2 border-dashed border-gray-200 flex flex-col items-center justify-center cursor-pointer hover:bg-gray-50 transition-colors gap-2 relative overflow-hidden">
                  {event.pictures && event.pictures.length > 0 ? (
                    <img
                      src={event.pictures[0].pictureUrl}
                      alt="Capa atual"
                      className="absolute inset-0 w-full h-full object-cover opacity-50"
                    />
                  ) : null}
                  <span
                    className="material-symbols-outlined text-gray-500 z-10"
                    style={{ fontSize: 32 }}
                  >
                    add_photo_alternate
                  </span>
                  <span className="text-sm text-gray-600 font-medium z-10 bg-white/80 px-3 py-1 rounded-full">
                    Clique para alterar a imagem
                  </span>
                </div>
              </div>

              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">
                  Tag Principal
                </label>
                <div className="flex flex-wrap gap-2">
                  {availableTags.map((tag) => (
                    <button
                      key={tag.id}
                      type="button"
                      onClick={() => setSelectedTag(tag.tagName)}
                      className={`cursor-pointer text-xs px-3 py-1.5 rounded-full font-semibold transition-colors ${
                        selectedTag === tag.tagName
                          ? "bg-[#b91c1c] text-white"
                          : "bg-gray-100 text-gray-500 hover:bg-gray-200"
                      }`}
                    >
                      {tag.tagName}
                    </button>
                  ))}
                </div>
              </div>
            </div>

            {/* ABA 2: Agenda e Local */}
            <div
              className={`space-y-6 ${activeTab === "agenda_local" ? "block" : "hidden"}`}
            >
              <h2 className="text-lg font-bold text-gray-900 border-b border-gray-100 pb-4">
                Agenda e Local
              </h2>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-1">
                    Data de Início
                  </label>
                  <input
                    required
                    name="startDate"
                    type="date"
                    defaultValue={getStartDate()}
                    className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:border-[#b91c1c] outline-none"
                  />
                </div>
                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-1">
                    Hora de Início
                  </label>
                  <input
                    required
                    name="startTime"
                    type="time"
                    defaultValue={getStartTime()}
                    className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:border-[#b91c1c] outline-none"
                  />
                </div>
                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-1">
                    Data de Fim
                  </label>
                  <input
                    required
                    name="endDate"
                    type="date"
                    defaultValue={getEndDate()}
                    className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:border-[#b91c1c] outline-none"
                  />
                </div>
                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-1">
                    Hora de Fim
                  </label>
                  <input
                    required
                    name="endTime"
                    type="time"
                    defaultValue={getEndTime()}
                    className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:border-[#b91c1c] outline-none"
                  />
                </div>
              </div>

              <div className="pt-4 border-t border-gray-100 space-y-4">
                <div className="flex items-center justify-between">
                  <h3 className="text-sm font-bold text-gray-900">
                    Local do Evento
                  </h3>
                  <div className="flex bg-gray-100 p-1 rounded-lg">
                    <button
                      type="button"
                      onClick={() => setVenueMode("existing")}
                      className={`text-xs px-3 py-1 rounded-md font-bold transition-colors cursor-pointer ${venueMode === "existing" ? "bg-white shadow-sm text-[#b91c1c]" : "text-gray-500"}`}
                    >
                      Manter Existente
                    </button>
                    <button
                      type="button"
                      onClick={() => setVenueMode("new")}
                      className={`text-xs px-3 py-1 rounded-md font-bold transition-colors cursor-pointer ${venueMode === "new" ? "bg-white shadow-sm text-[#b91c1c]" : "text-gray-500"}`}
                    >
                      Trocar / Novo
                    </button>
                  </div>
                </div>

                {venueMode === "existing" ? (
                  <div>
                    <select
                      required={venueMode === "existing"}
                      value={selectedVenueId}
                      onChange={(e) => setSelectedVenueId(e.target.value)}
                      className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm text-gray-700 focus:bg-white focus:border-[#b91c1c] outline-none appearance-none"
                    >
                      <option value="" disabled>
                        Escolha um local cadastrado...
                      </option>
                      {venuesList.map((v) => (
                        <option key={v.id} value={v.id}>
                          {v.name} - {v.neighborhood}
                        </option>
                      ))}
                    </select>
                    {selectedVenue && (
                      <p className="text-xs text-gray-500 mt-2 ml-1">
                        {selectedVenue.street}, {selectedVenue.number} -{" "}
                        {selectedVenue.city}
                      </p>
                    )}
                  </div>
                ) : (
                  <div className="space-y-4 p-4 bg-gray-50 border border-gray-200 rounded-xl">
                    <div>
                      <label className="block text-sm font-semibold text-gray-700 mb-1">
                        Nome do Local
                      </label>
                      <input
                        required={venueMode === "new"}
                        name="venueName"
                        type="text"
                        placeholder="Novo Local"
                        className="w-full px-4 py-2.5 bg-white border border-gray-200 rounded-xl text-sm focus:border-[#b91c1c] outline-none"
                      />
                    </div>
                    <div className="grid grid-cols-3 gap-4">
                      <div className="col-span-2">
                        <label className="block text-sm font-semibold text-gray-700 mb-1">
                          Rua
                        </label>
                        <input
                          required={venueMode === "new"}
                          name="venueStreet"
                          type="text"
                          className="w-full px-4 py-2.5 bg-white border border-gray-200 rounded-xl text-sm focus:border-[#b91c1c] outline-none"
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-semibold text-gray-700 mb-1">
                          Número
                        </label>
                        <input
                          required={venueMode === "new"}
                          name="venueNumber"
                          type="text"
                          className="w-full px-4 py-2.5 bg-white border border-gray-200 rounded-xl text-sm focus:border-[#b91c1c] outline-none"
                        />
                      </div>
                    </div>
                    <div className="grid grid-cols-2 gap-4">
                      <div>
                        <label className="block text-sm font-semibold text-gray-700 mb-1">
                          Bairro
                        </label>
                        <input
                          required={venueMode === "new"}
                          name="venueNeighborhood"
                          type="text"
                          className="w-full px-4 py-2.5 bg-white border border-gray-200 rounded-xl text-sm focus:border-[#b91c1c] outline-none"
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-semibold text-gray-700 mb-1">
                          Cidade
                        </label>
                        <input
                          required={venueMode === "new"}
                          name="venueCity"
                          type="text"
                          defaultValue="Sobral"
                          className="w-full px-4 py-2.5 bg-white border border-gray-200 rounded-xl text-sm focus:border-[#b91c1c] outline-none"
                        />
                      </div>
                    </div>
                  </div>
                )}
              </div>
            </div>

            {/* ABA 3: Opções */}
            <div
              className={`space-y-6 ${activeTab === "opcoes" ? "block" : "hidden"}`}
            >
              <h2 className="text-lg font-bold text-gray-900 border-b border-gray-100 pb-4">
                Opções do Evento
              </h2>

              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">
                  Ingresso
                </label>
                <div className="grid grid-cols-2 gap-3">
                  <div
                    onClick={() => setTicketType("free")}
                    className={`cursor-pointer p-4 border-2 rounded-xl transition-all ${
                      ticketType === "free"
                        ? "border-[#b91c1c] bg-red-50"
                        : "border-gray-200 hover:border-gray-300"
                    }`}
                  >
                    <h4 className="font-bold text-gray-900 text-sm mb-1">
                      Gratuito
                    </h4>
                    <p className="text-xs text-gray-500">
                      Aberto ao público geral
                    </p>
                  </div>
                  <div
                    onClick={() => setTicketType("paid")}
                    className={`cursor-pointer p-4 border-2 rounded-xl transition-all ${
                      ticketType === "paid"
                        ? "border-[#b91c1c] bg-red-50"
                        : "border-gray-200 hover:border-gray-300"
                    }`}
                  >
                    <h4 className="font-bold text-gray-900 text-sm mb-1">
                      Pago
                    </h4>
                    <p className="text-xs text-gray-500">
                      Requer compra de ingresso
                    </p>
                  </div>
                </div>
                {ticketType === "paid" && (
                  <div className="mt-4">
                    <label className="block text-sm font-semibold text-gray-700 mb-1">
                      Preço (R$)
                    </label>
                    <input
                      required
                      name="price"
                      type="number"
                      min="0"
                      step="0.01"
                      defaultValue={event.price || ""}
                      className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:border-[#b91c1c] outline-none"
                    />
                  </div>
                )}
              </div>

              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-1">
                  Classificação Indicativa
                </label>
                <select
                  name="contentRating"
                  defaultValue={event.contentRating || "Livre"}
                  className="cursor-pointer w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:border-[#b91c1c] outline-none appearance-none"
                >
                  {RATINGS.map((r) => (
                    <option key={r} value={r}>
                      {r}
                    </option>
                  ))}
                </select>
              </div>

              <div className="pt-4 border-t border-gray-100">
                <h3 className="text-sm font-bold text-gray-900 mb-4">
                  Infraestrutura do Local
                </h3>

                {venueMode === "existing" && selectedVenue ? (
                  <div className="flex flex-wrap gap-2">
                    {selectedVenue.hasAccessibility && (
                      <span className="bg-blue-100 text-blue-700 px-3 py-1.5 rounded-lg text-xs font-semibold">
                        ♿ Acessibilidade
                      </span>
                    )}
                    {selectedVenue.hasParkingLot && (
                      <span className="bg-gray-200 text-gray-700 px-3 py-1.5 rounded-lg text-xs font-semibold">
                        🅿️ Estacionamento
                      </span>
                    )}
                    {selectedVenue.hasFoodsAndDrinks && (
                      <span className="bg-orange-100 text-orange-700 px-3 py-1.5 rounded-lg text-xs font-semibold">
                        🍔 Alimentação
                      </span>
                    )}
                    {selectedVenue.hasBathroom && (
                      <span className="bg-teal-100 text-teal-700 px-3 py-1.5 rounded-lg text-xs font-semibold">
                        🚻 Banheiros
                      </span>
                    )}
                    {!selectedVenue.hasAccessibility &&
                      !selectedVenue.hasParkingLot &&
                      !selectedVenue.hasFoodsAndDrinks &&
                      !selectedVenue.hasBathroom && (
                        <span className="text-gray-500 text-sm">
                          O local selecionado não possui infraestrutura
                          cadastrada.
                        </span>
                      )}
                  </div>
                ) : (
                  <div className="grid grid-cols-2 gap-3">
                    {amenities.map((item) => (
                      <label
                        key={item.name}
                        className="cursor-pointer flex items-center gap-3 p-3 rounded-xl bg-gray-50 hover:bg-gray-100 transition-colors"
                      >
                        <input
                          name={item.name}
                          type="checkbox"
                          className="w-4 h-4 accent-[#b91c1c] rounded"
                        />
                        <span
                          className="material-symbols-outlined text-[#b91c1c]"
                          style={{ fontSize: 16 }}
                        >
                          {item.icon}
                        </span>
                        <span className="text-sm text-gray-700">
                          {item.label}
                        </span>
                      </label>
                    ))}
                  </div>
                )}
              </div>
            </div>
          </form>
        </section>
      </main>
    </div>
  );
}
