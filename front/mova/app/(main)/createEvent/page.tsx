"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { useEvents } from "@/hooks/useEvents";
import { useTags } from "@/hooks/useTags";
import { useVenues } from "@/hooks/useVenues";
import { Venue } from "@/types/venue.types";

const RATINGS = [
  "Livre",
  "10 anos",
  "12 anos",
  "14 anos",
  "16 anos",
  "18 anos",
];

export default function CreateEventPage() {
  const router = useRouter();

  // Integração com a API
  const { createEvent } = useEvents();
  const { fetchAllTags, tags } = useTags();
  const { fetchAllVenues, createVenue } = useVenues();

  // Estados do Formulário
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [ticketType, setTicketType] = useState<"free" | "paid">("free");
  const [selectedTag, setSelectedTag] = useState<string | null>(null);

  // Estados de Localização
  const [venueMode, setVenueMode] = useState<"existing" | "new">("existing");
  const [selectedVenueId, setSelectedVenueId] = useState<string>("");
  const [venuesList, setVenuesList] = useState<Venue[]>([]);

  // Carrega dados essenciais na montagem
  useEffect(() => {
    fetchAllTags();
    fetchAllVenues(0, 100)
      .then((data) => setVenuesList(data.content))
      .catch(console.error);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const selectedVenue = venuesList.find((v) => v.id === selectedVenueId);

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setIsSubmitting(true);
    const formData = new FormData(e.currentTarget);

    try {
      // 1. Resolve a Localização (Venue)
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
      } else if (!finalVenueId) {
        alert("Por favor, selecione um local existente ou cadastre um novo.");
        setIsSubmitting(false);
        return;
      }

      // 2. Resolve a Tag
      const tagId = tags.find((t) => t.tagName === selectedTag)?.id;

      // 3. Cria o Evento
      await createEvent({
        eventName: formData.get("eventName") as string,
        description: formData.get("description") as string,
        contentRating: formData.get("contentRating") as string,
        price: ticketType === "free" ? 0 : Number(formData.get("price")),
        startsAt: `${formData.get("startDate")}T${formData.get("startTime")}:00`,
        endsAt: `${formData.get("endDate")}T${formData.get("endTime")}:00`,
        venueId: finalVenueId,
        tagIds: tagId ? [tagId] : [],
      });

      alert("Evento publicado com sucesso!");
      router.push("/gestao"); // Ajuste para a rota do seu painel de gestão ou feed
    } catch (error) {
      console.error("Erro ao publicar evento:", error);
      alert(
        "Ocorreu um erro ao publicar o evento. Verifique os dados e tente novamente.",
      );
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

  return (
    <div className="flex-1 bg-gray-50 font-sans text-gray-800">
      <div className="max-w-5xl mx-auto p-8">
        {/* Header */}
        <div className="flex justify-between items-end mb-8">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">
              Cadastrar Novo Evento
            </h1>
            <p className="text-sm text-gray-500 mt-1">
              Preencha os detalhes para publicar seu evento cultural em Sobral.
            </p>
          </div>
          <button
            type="submit"
            form="form-evento"
            disabled={isSubmitting}
            className={`cursor-pointer flex items-center gap-2 px-5 py-2.5 text-sm font-semibold text-white rounded-xl transition-colors shadow-sm ${
              isSubmitting
                ? "bg-gray-400 cursor-not-allowed"
                : "bg-[#b91c1c] hover:bg-[#991b1b]"
            }`}
          >
            <span
              className="material-symbols-outlined"
              style={{ fontSize: 16 }}
            >
              publish
            </span>
            {isSubmitting ? "Publicando..." : "Publicar Evento"}
          </button>
        </div>

        <form
          id="form-evento"
          onSubmit={handleSubmit}
          className="grid grid-cols-1 lg:grid-cols-3 gap-6"
        >
          {/* Coluna principal */}
          <div className="lg:col-span-2 space-y-6">
            {/* Informações Básicas */}
            <div className="bg-white rounded-2xl border border-gray-100 p-6 shadow-sm">
              <div className="flex items-center gap-2 mb-5">
                <span
                  className="material-symbols-outlined text-[#b91c1c]"
                  style={{ fontSize: 20 }}
                >
                  info
                </span>
                <h3 className="text-base font-bold text-gray-900">
                  Informações Básicas
                </h3>
              </div>
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-1">
                    Nome do Evento <span className="text-[#b91c1c]">*</span>
                  </label>
                  <input
                    required
                    name="eventName"
                    type="text"
                    placeholder="ex: Festival de Quadrilhas de Sobral"
                    className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:border-[#b91c1c] outline-none transition-all"
                  />
                </div>

                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-1">
                    Descrição <span className="text-[#b91c1c]">*</span>
                  </label>
                  <textarea
                    required
                    name="description"
                    rows={4}
                    placeholder="Descreva o evento, atrações e importância cultural..."
                    className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:border-[#b91c1c] outline-none transition-all resize-none"
                  />
                </div>

                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-2">
                    Tag
                  </label>
                  <div className="flex flex-wrap gap-2">
                    {tags.map((tag) => (
                      <button
                        key={tag.id}
                        type="button"
                        onClick={() =>
                          setSelectedTag((prev) =>
                            prev === tag.tagName ? null : tag.tagName,
                          )
                        }
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
                  <p className="text-xs text-gray-400 mt-2">
                    Selecione uma tag existente. Novas tags são cadastradas pelo
                    administrador.
                  </p>
                </div>
              </div>
            </div>

            {/* Data e Hora */}
            <div className="bg-white rounded-2xl border border-gray-100 p-6 shadow-sm">
              <div className="flex items-center gap-2 mb-5">
                <span
                  className="material-symbols-outlined text-[#b91c1c]"
                  style={{ fontSize: 20 }}
                >
                  schedule
                </span>
                <h3 className="text-base font-bold text-gray-900">
                  Data e Hora
                </h3>
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-1">
                    Data de Início <span className="text-[#b91c1c]">*</span>
                  </label>
                  <input
                    required
                    name="startDate"
                    type="date"
                    className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm text-gray-600 focus:bg-white focus:border-[#b91c1c] outline-none"
                  />
                </div>
                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-1">
                    Hora de Início <span className="text-[#b91c1c]">*</span>
                  </label>
                  <input
                    required
                    name="startTime"
                    type="time"
                    className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm text-gray-600 focus:bg-white focus:border-[#b91c1c] outline-none"
                  />
                </div>
                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-1">
                    Data de Término <span className="text-[#b91c1c]">*</span>
                  </label>
                  <input
                    required
                    name="endDate"
                    type="date"
                    className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm text-gray-600 focus:bg-white focus:border-[#b91c1c] outline-none"
                  />
                </div>
                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-1">
                    Hora de Término <span className="text-[#b91c1c]">*</span>
                  </label>
                  <input
                    required
                    name="endTime"
                    type="time"
                    className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm text-gray-600 focus:bg-white focus:border-[#b91c1c] outline-none"
                  />
                </div>
              </div>
            </div>

            {/* Localização (Com Toggle) */}
            <div className="bg-white rounded-2xl border border-gray-100 p-6 shadow-sm">
              <div className="flex items-center justify-between mb-5">
                <div className="flex items-center gap-2">
                  <span
                    className="material-symbols-outlined text-[#b91c1c]"
                    style={{ fontSize: 20 }}
                  >
                    location_on
                  </span>
                  <h3 className="text-base font-bold text-gray-900">
                    Localização
                  </h3>
                </div>
                {/* Toggle Existing / New */}
                <div className="flex bg-gray-100 p-1 rounded-lg">
                  <button
                    type="button"
                    onClick={() => setVenueMode("existing")}
                    className={`text-xs px-3 py-1.5 rounded-md font-bold transition-colors cursor-pointer ${venueMode === "existing" ? "bg-white shadow-sm text-[#b91c1c]" : "text-gray-500 hover:text-gray-700"}`}
                  >
                    Local Existente
                  </button>
                  <button
                    type="button"
                    onClick={() => setVenueMode("new")}
                    className={`text-xs px-3 py-1.5 rounded-md font-bold transition-colors cursor-pointer ${venueMode === "new" ? "bg-white shadow-sm text-[#b91c1c]" : "text-gray-500 hover:text-gray-700"}`}
                  >
                    Cadastrar Novo
                  </button>
                </div>
              </div>

              {venueMode === "existing" ? (
                // Modo: SELECIONAR EXISTENTE
                <div className="space-y-4">
                  <div>
                    <label className="block text-sm font-semibold text-gray-700 mb-1">
                      Selecione o Local{" "}
                      <span className="text-[#b91c1c]">*</span>
                    </label>
                    <select
                      required={venueMode === "existing"}
                      value={selectedVenueId}
                      onChange={(e) => setSelectedVenueId(e.target.value)}
                      className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm text-gray-600 focus:bg-white focus:border-[#b91c1c] outline-none appearance-none cursor-pointer"
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
                  </div>
                  {selectedVenue && (
                    <div className="p-4 bg-gray-50 rounded-xl border border-gray-100 text-sm">
                      <p className="text-gray-600">
                        {selectedVenue.street}, {selectedVenue.number} -{" "}
                        {selectedVenue.neighborhood}, {selectedVenue.city}
                      </p>
                    </div>
                  )}
                </div>
              ) : (
                // Modo: CADASTRAR NOVO
                <div className="space-y-4">
                  <div>
                    <label className="block text-sm font-semibold text-gray-700 mb-1">
                      Nome do Local <span className="text-[#b91c1c]">*</span>
                    </label>
                    <input
                      required={venueMode === "new"}
                      name="venueName"
                      type="text"
                      placeholder="ex: Arco do Triunfo"
                      className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:border-[#b91c1c] outline-none"
                    />
                  </div>

                  <div className="grid grid-cols-3 gap-4">
                    <div className="col-span-2">
                      <label className="block text-sm font-semibold text-gray-700 mb-1">
                        Rua <span className="text-[#b91c1c]">*</span>
                      </label>
                      <input
                        required={venueMode === "new"}
                        name="venueStreet"
                        type="text"
                        placeholder="ex: Rua Conselheiro Rodrigues"
                        className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:border-[#b91c1c] outline-none"
                      />
                    </div>
                    <div>
                      <label className="block text-sm font-semibold text-gray-700 mb-1">
                        Número <span className="text-[#b91c1c]">*</span>
                      </label>
                      <input
                        required={venueMode === "new"}
                        name="venueNumber"
                        type="number"
                        placeholder="67"
                        className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:border-[#b91c1c] outline-none"
                      />
                    </div>
                  </div>

                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <label className="block text-sm font-semibold text-gray-700 mb-1">
                        Bairro <span className="text-[#b91c1c]">*</span>
                      </label>
                      <input
                        required={venueMode === "new"}
                        name="venueNeighborhood"
                        type="text"
                        placeholder="ex: Centro"
                        className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:border-[#b91c1c] outline-none"
                      />
                    </div>
                    <div>
                      <label className="block text-sm font-semibold text-gray-700 mb-1">
                        Cidade <span className="text-[#b91c1c]">*</span>
                      </label>
                      <input
                        required={venueMode === "new"}
                        name="venueCity"
                        type="text"
                        defaultValue="Sobral"
                        className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:border-[#b91c1c] outline-none"
                      />
                    </div>
                  </div>

                  <div>
                    <label className="block text-sm font-semibold text-gray-700 mb-1">
                      Ponto de Referência{" "}
                      <span className="text-gray-400 font-normal">
                        (opcional)
                      </span>
                    </label>
                    <input
                      name="venueLandmark"
                      type="text"
                      placeholder="ex: Próximo à Catedral de Sobral"
                      className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:border-[#b91c1c] outline-none"
                    />
                  </div>
                </div>
              )}
            </div>
          </div>

          {/* Coluna lateral */}
          <div className="space-y-6">
            {/* Imagem de capa */}
            <div className="bg-white rounded-2xl border border-gray-100 p-6 shadow-sm">
              <div className="flex items-center gap-2 mb-4">
                <span
                  className="material-symbols-outlined text-[#b91c1c]"
                  style={{ fontSize: 20 }}
                >
                  add_photo_alternate
                </span>
                <h3 className="text-base font-bold text-gray-900">
                  Imagem de Capa
                </h3>
              </div>
              <div className="border-2 border-dashed border-gray-200 rounded-xl p-6 flex flex-col items-center justify-center text-center hover:border-[#b91c1c] hover:bg-red-50/20 transition-colors cursor-pointer gap-2">
                <span
                  className="material-symbols-outlined text-gray-300"
                  style={{ fontSize: 36 }}
                >
                  cloud_upload
                </span>
                <p className="text-sm font-semibold text-gray-600">
                  Clique para fazer upload
                </p>
                <p className="text-xs text-gray-400">
                  PNG, JPG ou GIF · máx. 5MB
                </p>
              </div>
            </div>

            {/* Ingresso */}
            <div className="bg-white rounded-2xl border border-gray-100 p-6 shadow-sm">
              <div className="flex items-center gap-2 mb-4">
                <span
                  className="material-symbols-outlined text-[#b91c1c]"
                  style={{ fontSize: 20 }}
                >
                  confirmation_number
                </span>
                <h3 className="text-base font-bold text-gray-900">Ingresso</h3>
              </div>
              <div className="space-y-3">
                {[
                  {
                    type: "free",
                    label: "Entrada Gratuita",
                    sub: "Aberto ao público",
                  },
                  {
                    type: "paid",
                    label: "Entrada Paga",
                    sub: "Requer compra de ingresso",
                  },
                ].map((opt) => (
                  <div
                    key={opt.type}
                    onClick={() => setTicketType(opt.type as "free" | "paid")}
                    className={`flex items-center justify-between p-3 border-2 rounded-xl cursor-pointer transition-all ${
                      ticketType === opt.type
                        ? "border-[#b91c1c] bg-red-50/30"
                        : "border-gray-200 hover:border-gray-300"
                    }`}
                  >
                    <div>
                      <p className="text-sm font-bold text-gray-900">
                        {opt.label}
                      </p>
                      <p className="text-xs text-gray-500">{opt.sub}</p>
                    </div>
                    <div
                      className={`w-4 h-4 rounded-full border-2 flex items-center justify-center ${
                        ticketType === opt.type
                          ? "border-[#b91c1c]"
                          : "border-gray-300"
                      }`}
                    >
                      {ticketType === opt.type && (
                        <div className="w-2 h-2 bg-[#b91c1c] rounded-full" />
                      )}
                    </div>
                  </div>
                ))}

                {ticketType === "paid" && (
                  <div className="pt-1">
                    <label className="block text-sm font-semibold text-gray-700 mb-1">
                      Preço (R$) <span className="text-[#b91c1c]">*</span>
                    </label>
                    <input
                      required
                      name="price"
                      type="number"
                      min="0.01"
                      step="0.01"
                      placeholder="0,00"
                      className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:border-[#b91c1c] outline-none"
                    />
                  </div>
                )}
              </div>
            </div>

            {/* Classificação Indicativa */}
            <div className="bg-white rounded-2xl border border-gray-100 p-6 shadow-sm">
              <div className="flex items-center gap-2 mb-4">
                <span
                  className="material-symbols-outlined text-[#b91c1c]"
                  style={{ fontSize: 20 }}
                >
                  person_check
                </span>
                <h3 className="text-base font-bold text-gray-900">
                  Classificação Indicativa
                </h3>
              </div>
              <select
                name="contentRating"
                className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm text-gray-600 focus:bg-white focus:border-[#b91c1c] outline-none appearance-none cursor-pointer"
              >
                {RATINGS.map((r) => (
                  <option key={r} value={r}>
                    {r}
                  </option>
                ))}
              </select>
            </div>

            {/* Infraestrutura Dinâmica */}
            <div className="bg-white rounded-2xl border border-gray-100 p-6 shadow-sm">
              <div className="flex items-center gap-2 mb-4">
                <span
                  className="material-symbols-outlined text-[#b91c1c]"
                  style={{ fontSize: 20 }}
                >
                  domain
                </span>
                <h3 className="text-base font-bold text-gray-900">
                  Infraestrutura
                </h3>
              </div>

              {venueMode === "existing" ? (
                // Modo Existente: Apenas exibir o que o local tem
                <div className="space-y-2">
                  {!selectedVenue ? (
                    <p className="text-xs text-gray-400">
                      Selecione um local para ver sua infraestrutura.
                    </p>
                  ) : (
                    <div className="flex flex-col gap-2">
                      {amenities.map((a) => {
                        const hasAmenity = selectedVenue[a.name as keyof Venue];
                        return hasAmenity ? (
                          <div
                            key={a.name}
                            className="flex items-center gap-2 px-3 py-2 bg-gray-50 rounded-xl"
                          >
                            <span
                              className="material-symbols-outlined text-[#b91c1c]"
                              style={{ fontSize: 16 }}
                            >
                              {a.icon}
                            </span>
                            <span className="text-sm text-gray-700 font-medium">
                              {a.label}
                            </span>
                          </div>
                        ) : null;
                      })}
                      {!selectedVenue.hasAccessibility &&
                        !selectedVenue.hasParkingLot &&
                        !selectedVenue.hasFoodsAndDrinks &&
                        !selectedVenue.hasBathroom && (
                          <p className="text-sm text-gray-500">
                            Nenhuma infraestrutura especial informada.
                          </p>
                        )}
                    </div>
                  )}
                </div>
              ) : (
                // Modo Novo Local: Checkboxes para o usuário marcar
                <div className="space-y-2">
                  <p className="text-xs text-gray-500 mb-3">
                    Marque o que este novo local oferece:
                  </p>
                  {amenities.map((a) => (
                    <label
                      key={a.name}
                      className="flex items-center gap-3 p-2.5 rounded-xl hover:bg-gray-50 cursor-pointer transition-colors group"
                    >
                      <input
                        name={a.name}
                        type="checkbox"
                        className="cursor-pointer w-4 h-4 accent-[#b91c1c] rounded"
                      />
                      <span
                        className="material-symbols-outlined text-[#b91c1c]"
                        style={{ fontSize: 16 }}
                      >
                        {a.icon}
                      </span>
                      <span className="text-sm text-gray-700 group-hover:text-gray-900 font-medium">
                        {a.label}
                      </span>
                    </label>
                  ))}
                </div>
              )}
            </div>
          </div>
        </form>
      </div>
    </div>
  );
}
