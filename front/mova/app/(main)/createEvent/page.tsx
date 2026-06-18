"use client";

import { useState } from "react";
import { feedEvents } from "@/mocks/feed.mocks";

// Tags vindas da tabela tags — em produção: GET /tags
const ALL_TAGS = Array.from(
	new Set(feedEvents.flatMap((e) => e.tags?.map((t) => t.tagname) ?? []))
);

const RATINGS = ["Livre", "10 anos", "12 anos", "14 anos", "16 anos", "18 anos"];

export default function CreateEventPage() {
	const [ticketType, setTicketType] = useState<"free" | "paid">("free");
	const [selectedTag, setSelectedTag] = useState<string | null>(null);

	function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
		e.preventDefault();
		const formData = new FormData(e.currentTarget);

		// Payload mapeado 1:1 com as tabelas events + venues + event_tags
		const payload = {
			// events
			eventName: formData.get("eventName"),
			description: formData.get("description"),
			contentRating: formData.get("contentRating"),
			price: ticketType === "free" ? 0 : Number(formData.get("price")),
			startsAt: `${formData.get("startDate")}T${formData.get("startTime")}:00`,
			endsAt: `${formData.get("endDate")}T${formData.get("endTime")}:00`,
			// event_tags
			tags: selectedTag ? [selectedTag] : [],
			// venues
			venue: {
				name: formData.get("venueName"),
				street: formData.get("venueStreet"),
				number: formData.get("venueNumber"),
				neighborhood: formData.get("venueNeighborhood"),
				city: formData.get("venueCity"),
				landmark: formData.get("venueLandmark") || null,
				hasAccessibility: formData.get("hasAccessibility") === "on",
				hasParkingLot: formData.get("hasParkingLot") === "on",
				hasFoodsAndDrinks: formData.get("hasFoodsAndDrinks") === "on",
				hasBathroom: formData.get("hasBathroom") === "on",
			},
		};

		console.log("Publicando evento...", payload);
		// TODO: POST /events com payload
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
						<h1 className="text-2xl font-bold text-mova-dark">Cadastrar Novo Evento</h1>
						<p className="text-sm text-gray-400 mt-1">
							Preencha os detalhes para publicar seu evento cultural em Sobral.
						</p>
					</div>
					<button
						type="submit"
						form="form-evento"
						className="flex items-center gap-2 px-5 py-2.5 text-sm font-semibold text-white bg-mova-red hover:bg-red-700 rounded-xl transition-colors shadow-sm"
					>
						<span className="material-symbols-outlined" style={{ fontSize: 16 }}>
							publish
						</span>
						Publicar Evento
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
								<span className="material-symbols-outlined text-mova-red" style={{ fontSize: 20 }}>
									info
								</span>
								<h3 className="text-base font-bold text-mova-dark">Informações Básicas</h3>
							</div>
							<div className="space-y-4">
								{/* event_name */}
								<div>
									<label className="block text-sm font-semibold text-gray-700 mb-1">
										Nome do Evento <span className="text-mova-red">*</span>
									</label>
									<input
										required
										name="eventName"
										type="text"
										placeholder="ex: Festival de Quadrilhas de Sobral"
										className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:border-mova-red outline-none transition-all"
									/>
								</div>

								{/* description */}
								<div>
									<label className="block text-sm font-semibold text-gray-700 mb-1">
										Descrição <span className="text-mova-red">*</span>
									</label>
									<textarea
										required
										name="description"
										rows={4}
										placeholder="Descreva o evento, atrações e importância cultural..."
										className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:border-mova-red outline-none transition-all resize-none"
									/>
								</div>

								{/* event_tags — seleção a partir das tags existentes no banco */}
								<div>
									<label className="block text-sm font-semibold text-gray-700 mb-2">
										Tag
									</label>
									<div className="flex flex-wrap gap-2">
										{ALL_TAGS.map((tag) => (
											<button
												key={tag}
												type="button"
												onClick={() =>
													setSelectedTag((prev) => (prev === tag ? null : tag))
												}
												className={`text-xs px-3 py-1.5 rounded-full font-semibold transition-colors ${
													selectedTag === tag
														? "bg-mova-red text-white"
														: "bg-gray-100 text-gray-500 hover:bg-gray-200"
												}`}
											>
												{tag}
											</button>
										))}
									</div>
									<p className="text-xs text-gray-400 mt-2">
										Selecione uma tag existente. Novas tags são cadastradas pelo administrador.
									</p>
								</div>
							</div>
						</div>

						{/* Data e Hora — starts_at / ends_at */}
						<div className="bg-white rounded-2xl border border-gray-100 p-6 shadow-sm">
							<div className="flex items-center gap-2 mb-5">
								<span className="material-symbols-outlined text-mova-red" style={{ fontSize: 20 }}>
									schedule
								</span>
								<h3 className="text-base font-bold text-mova-dark">Data e Hora</h3>
							</div>
							<div className="grid grid-cols-2 gap-4">
								<div>
									<label className="block text-sm font-semibold text-gray-700 mb-1">
										Data de Início <span className="text-mova-red">*</span>
									</label>
									<input
										required
										name="startDate"
										type="date"
										className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm text-gray-600 focus:bg-white focus:border-mova-red outline-none"
									/>
								</div>
								<div>
									<label className="block text-sm font-semibold text-gray-700 mb-1">
										Hora de Início <span className="text-mova-red">*</span>
									</label>
									<input
										required
										name="startTime"
										type="time"
										className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm text-gray-600 focus:bg-white focus:border-mova-red outline-none"
									/>
								</div>
								<div>
									<label className="block text-sm font-semibold text-gray-700 mb-1">
										Data de Término <span className="text-mova-red">*</span>
									</label>
									<input
										required
										name="endDate"
										type="date"
										className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm text-gray-600 focus:bg-white focus:border-mova-red outline-none"
									/>
								</div>
								<div>
									<label className="block text-sm font-semibold text-gray-700 mb-1">
										Hora de Término <span className="text-mova-red">*</span>
									</label>
									<input
										required
										name="endTime"
										type="time"
										className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm text-gray-600 focus:bg-white focus:border-mova-red outline-none"
									/>
								</div>
							</div>
						</div>

						{/* Localização — venues (campos separados conforme a tabela) */}
						<div className="bg-white rounded-2xl border border-gray-100 p-6 shadow-sm">
							<div className="flex items-center gap-2 mb-5">
								<span className="material-symbols-outlined text-mova-red" style={{ fontSize: 20 }}>
									location_on
								</span>
								<h3 className="text-base font-bold text-mova-dark">Localização</h3>
							</div>
							<div className="space-y-4">
								{/* venues.name */}
								<div>
									<label className="block text-sm font-semibold text-gray-700 mb-1">
										Nome do Local <span className="text-mova-red">*</span>
									</label>
									<input
										required
										name="venueName"
										type="text"
										placeholder="ex: Arco do Triunfo"
										className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:border-mova-red outline-none"
									/>
								</div>

								<div className="grid grid-cols-3 gap-4">
									{/* venues.street */}
									<div className="col-span-2">
										<label className="block text-sm font-semibold text-gray-700 mb-1">
											Rua <span className="text-mova-red">*</span>
										</label>
										<input
											required
											name="venueStreet"
											type="text"
											placeholder="ex: Rua Conselheiro Rodrigues"
											className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:border-mova-red outline-none"
										/>
									</div>
									{/* venues.number */}
									<div>
										<label className="block text-sm font-semibold text-gray-700 mb-1">
											Número <span className="text-mova-red">*</span>
										</label>
										<input
											required
											name="venueNumber"
											type="text"
											placeholder="ex: S/N"
											className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:border-mova-red outline-none"
										/>
									</div>
								</div>

								<div className="grid grid-cols-2 gap-4">
									{/* venues.neighborhood */}
									<div>
										<label className="block text-sm font-semibold text-gray-700 mb-1">
											Bairro <span className="text-mova-red">*</span>
										</label>
										<input
											required
											name="venueNeighborhood"
											type="text"
											placeholder="ex: Centro"
											className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:border-mova-red outline-none"
										/>
									</div>
									{/* venues.city */}
									<div>
										<label className="block text-sm font-semibold text-gray-700 mb-1">
											Cidade <span className="text-mova-red">*</span>
										</label>
										<input
											required
											name="venueCity"
											type="text"
											defaultValue="Sobral"
											className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:border-mova-red outline-none"
										/>
									</div>
								</div>

								{/* venues.landmark — nullable no banco */}
								<div>
									<label className="block text-sm font-semibold text-gray-700 mb-1">
										Ponto de Referência{" "}
										<span className="text-gray-400 font-normal">(opcional)</span>
									</label>
									<input
										name="venueLandmark"
										type="text"
										placeholder="ex: Próximo à Catedral de Sobral"
										className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:border-mova-red outline-none"
									/>
								</div>
							</div>
						</div>
					</div>

					{/* Coluna lateral */}
					<div className="space-y-6">

						{/* Imagem de capa — event_pictures.picture_url */}
						<div className="bg-white rounded-2xl border border-gray-100 p-6 shadow-sm">
							<div className="flex items-center gap-2 mb-4">
								<span className="material-symbols-outlined text-mova-red" style={{ fontSize: 20 }}>
									add_photo_alternate
								</span>
								<h3 className="text-base font-bold text-mova-dark">Imagem de Capa</h3>
							</div>
							<div className="border-2 border-dashed border-gray-200 rounded-xl p-6 flex flex-col items-center justify-center text-center hover:border-mova-red hover:bg-red-50/20 transition-colors cursor-pointer gap-2">
								<span
									className="material-symbols-outlined text-gray-300"
									style={{ fontSize: 36 }}
								>
									cloud_upload
								</span>
								<p className="text-sm font-semibold text-gray-600">
									Clique para fazer upload
								</p>
								<p className="text-xs text-gray-400">PNG, JPG ou GIF · máx. 5MB</p>
							</div>
						</div>

						{/* Ingresso — events.price */}
						<div className="bg-white rounded-2xl border border-gray-100 p-6 shadow-sm">
							<div className="flex items-center gap-2 mb-4">
								<span className="material-symbols-outlined text-mova-red" style={{ fontSize: 20 }}>
									confirmation_number
								</span>
								<h3 className="text-base font-bold text-mova-dark">Ingresso</h3>
							</div>
							<div className="space-y-3">
								{[
									{ type: "free", label: "Entrada Gratuita", sub: "Aberto ao público" },
									{ type: "paid", label: "Entrada Paga", sub: "Requer compra de ingresso" },
								].map((opt) => (
									<div
										key={opt.type}
										onClick={() => setTicketType(opt.type as "free" | "paid")}
										className={`flex items-center justify-between p-3 border-2 rounded-xl cursor-pointer transition-all ${
											ticketType === opt.type
												? "border-mova-red bg-red-50/30"
												: "border-gray-200 hover:border-gray-300"
										}`}
									>
										<div>
											<p className="text-sm font-bold text-mova-dark">{opt.label}</p>
											<p className="text-xs text-gray-400">{opt.sub}</p>
										</div>
										<div
											className={`w-4 h-4 rounded-full border-2 flex items-center justify-center ${
												ticketType === opt.type ? "border-mova-red" : "border-gray-300"
											}`}
										>
											{ticketType === opt.type && (
												<div className="w-2 h-2 bg-mova-red rounded-full" />
											)}
										</div>
									</div>
								))}

								{ticketType === "paid" && (
									<div className="pt-1">
										<label className="block text-sm font-semibold text-gray-700 mb-1">
											Preço (R$) <span className="text-mova-red">*</span>
										</label>
										<input
											required
											name="price"
											type="number"
											min="0.01"
											step="0.01"
											placeholder="0,00"
											className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:border-mova-red outline-none"
										/>
									</div>
								)}
							</div>
						</div>

						{/* Classificação Indicativa — events.content_rating */}
						<div className="bg-white rounded-2xl border border-gray-100 p-6 shadow-sm">
							<div className="flex items-center gap-2 mb-4">
								<span className="material-symbols-outlined text-mova-red" style={{ fontSize: 20 }}>
									person_check
								</span>
								<h3 className="text-base font-bold text-mova-dark">
									Classificação Indicativa
								</h3>
							</div>
							<select
								name="contentRating"
								className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm text-gray-600 focus:bg-white focus:border-mova-red outline-none appearance-none cursor-pointer"
							>
								{RATINGS.map((r) => (
									<option key={r} value={r}>
										{r}
									</option>
								))}
							</select>
						</div>

						{/* Infraestrutura — venues booleans */}
						<div className="bg-white rounded-2xl border border-gray-100 p-6 shadow-sm">
							<div className="flex items-center gap-2 mb-4">
								<span className="material-symbols-outlined text-mova-red" style={{ fontSize: 20 }}>
									domain
								</span>
								<h3 className="text-base font-bold text-mova-dark">Infraestrutura</h3>
							</div>
							<div className="space-y-2">
								{amenities.map((a) => (
									<label
										key={a.name}
										className="flex items-center gap-3 p-2.5 rounded-xl hover:bg-gray-50 cursor-pointer transition-colors"
									>
										<input
											name={a.name}
											type="checkbox"
											className="w-4 h-4 accent-mova-red rounded"
										/>
										<span
											className="material-symbols-outlined text-mova-red"
											style={{ fontSize: 16 }}
										>
											{a.icon}
										</span>
										<span className="text-sm text-gray-700">{a.label}</span>
									</label>
								))}
							</div>
						</div>
					</div>
				</form>
			</div>
		</div>
	);
}