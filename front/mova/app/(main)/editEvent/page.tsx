"use client";

import { useState } from "react";

type TabType = "basico" | "agenda_local" | "opcoes";

const ALL_TAGS = ["Música", "Teatro", "Dança", "Exposição", "Oficina", "Arte", "Festa Junina", "Gastronomia"];
const RATINGS = ["Livre", "10 anos", "12 anos", "14 anos", "16 anos", "18 anos"];

export default function ManageEventPage() {
	const [activeTab, setActiveTab] = useState<TabType>("basico");
	const [selectedTag, setSelectedTag] = useState("Música");
	const [ticketType, setTicketType] = useState<"free" | "paid">("free");
	const [eventName, setEventName] = useState("Festival de Quadrilhas de Sobral");

	const tabs = [
		{ id: "basico", label: "1. Informações Básicas" },
		{ id: "agenda_local", label: "2. Agenda e Local" },
		{ id: "opcoes", label: "3. Opções" },
	] as const;

	function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
		e.preventDefault();
		const formData = new FormData(e.currentTarget);
		const payload = {
			eventName: formData.get("eventName"),
			description: formData.get("description"),
			contentRating: formData.get("contentRating"),
			price: ticketType === "free" ? 0 : Number(formData.get("price")),
			startsAt: `${formData.get("startDate")}T${formData.get("startTime")}:00`,
			endsAt: `${formData.get("endDate")}T${formData.get("endTime")}:00`,
			tags: [selectedTag],
			venue: {
				name: formData.get("venueName"),
				street: formData.get("venueStreet"),
				number: formData.get("venueNumber"),
				neighborhood: formData.get("venueNeighborhood"),
				city: formData.get("venueCity"),
				landmark: formData.get("venueLandmark"),
				hasAccessibility: formData.get("hasAccessibility") === "on",
				hasParkingLot: formData.get("hasParkingLot") === "on",
				hasFoodsAndDrinks: formData.get("hasFoodsAndDrinks") === "on",
				hasBathroom: formData.get("hasBathroom") === "on",
			},
		};
		console.log("Salvando evento...", payload);
		// TODO: PATCH /events/:id com payload
	}

	return (
		<div className="min-h-screen bg-gray-50 flex flex-col font-sans text-gray-800">
			{/* Header */}
			<header className="bg-white border-b border-gray-100 px-8 py-5 top-0 z-10">
				<div className="max-w-5xl mx-auto flex justify-between items-center">
					<div>
						<p className="text-xs font-bold tracking-wider text-mova-red uppercase mb-1">
							Modo de Edição
						</p>
						<h1 className="text-xl font-bold text-mova-dark">
							{eventName || "Nome do Evento"}
						</h1>
					</div>
					<button
						type="submit"
						form="edit-event-form"
						className="cursor-pointer px-6 py-2 text-sm font-semibold text-white bg-mova-red hover:bg-red-700 rounded-xl transition-colors shadow-sm"
					>
						Salvar Alterações
					</button>
				</div>
			</header>

			<main className="flex-1 w-full max-w-5xl mx-auto flex gap-8 p-8">
				{/* Menu lateral */}
				<aside className="w-56 shrink-0">
					<nav className="flex flex-col gap-1 top-28">
						{tabs.map((tab) => (
							<button
								key={tab.id}
								onClick={() => setActiveTab(tab.id)}
								className={`cursor-pointer text-left px-4 py-3 rounded-xl text-sm font-medium transition-colors ${
									activeTab === tab.id
										? "bg-red-50 text-mova-red"
										: "text-gray-600 hover:bg-gray-100"
								}`}
							>
								{tab.label}
							</button>
						))}

						<div className="mt-8 pt-6 border-t border-gray-100">
							<button className="cursor-pointer text-left px-4 py-3 rounded-xl text-sm font-medium text-red-500 hover:bg-red-50 transition-colors w-full flex items-center gap-2">
								<span className="material-symbols-outlined" style={{ fontSize: 16 }}>
									delete
								</span>
								Excluir Evento
							</button>
						</div>
					</nav>
				</aside>

				{/* Área de edição */}
				<section className="flex-1 bg-white border border-gray-100 rounded-2xl shadow-sm p-8">
					<form id="edit-event-form" onSubmit={handleSubmit}>

						{/* ABA 1: Informações Básicas */}
						{activeTab === "basico" && (
							<div className="space-y-6">
								<h2 className="text-lg font-bold text-mova-dark border-b border-gray-100 pb-4">
									Informações Básicas
								</h2>

								{/* event_name */}
								<div>
									<label className="block text-sm font-semibold text-gray-700 mb-1">
										Nome do Evento
									</label>
									<input
										name="eventName"
										type="text"
										value={eventName}
										onChange={(e) => setEventName(e.target.value)}
										className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:border-mova-red outline-none transition-all"
									/>
								</div>

								{/* description */}
								<div>
									<label className="block text-sm font-semibold text-gray-700 mb-1">
										Descrição
									</label>
									<textarea
										name="description"
										defaultValue="O tradicional festival de quadrilhas..."
										rows={5}
										className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:border-mova-red outline-none transition-all resize-none"
									/>
								</div>

								{/* event_pictures */}
								<div>
									<label className="block text-sm font-semibold text-gray-700 mb-2">
										Imagem de Capa
									</label>
									<div className="h-44 w-full rounded-xl bg-gray-100 border-2 border-dashed border-gray-200 flex flex-col items-center justify-center cursor-pointer hover:bg-gray-50 transition-colors gap-2">
										<span
											className="material-symbols-outlined text-gray-300"
											style={{ fontSize: 32 }}
										>
											add_photo_alternate
										</span>
										<span className="text-sm text-gray-400 font-medium">
											Clique para alterar a imagem
										</span>
									</div>
								</div>

								{/* tags / event_tags */}
								<div>
									<label className="block text-sm font-semibold text-gray-700 mb-2">
										Tag Principal
									</label>
									<div className="flex flex-wrap gap-2">
										{ALL_TAGS.map((tag) => (
											<button
												key={tag}
												type="button"
												onClick={() => setSelectedTag(tag)}
												className={`cursor-pointer text-xs px-3 py-1.5 rounded-full font-semibold transition-colors ${
													selectedTag === tag
														? "bg-mova-red text-white"
														: "bg-gray-100 text-gray-500 hover:bg-gray-200"
												}`}
											>
												{tag}
											</button>
										))}
									</div>
								</div>
							</div>
						)}

						{/* ABA 2: Agenda e Local */}
						{activeTab === "agenda_local" && (
							<div className="space-y-6">
								<h2 className="text-lg font-bold text-mova-dark border-b border-gray-100 pb-4">
									Agenda e Local
								</h2>

								<div className="grid grid-cols-2 gap-4">
									<div>
										<label className="block text-sm font-semibold text-gray-700 mb-1">Data de Início</label>
										<input name="startDate" type="date" defaultValue="2026-06-20"
											className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:border-mova-red outline-none" />
									</div>
									<div>
										<label className="block text-sm font-semibold text-gray-700 mb-1">Hora de Início</label>
										<input name="startTime" type="time" defaultValue="18:00"
											className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:border-mova-red outline-none" />
									</div>
									<div>
										<label className="block text-sm font-semibold text-gray-700 mb-1">Data de Fim</label>
										<input name="endDate" type="date" defaultValue="2026-06-20"
											className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:border-mova-red outline-none" />
									</div>
									<div>
										<label className="block text-sm font-semibold text-gray-700 mb-1">Hora de Fim</label>
										<input name="endTime" type="time" defaultValue="23:00"
											className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:border-mova-red outline-none" />
									</div>
								</div>

								<div className="pt-4 border-t border-gray-100 space-y-4">
									<h3 className="text-sm font-bold text-mova-dark">Local do Evento</h3>

									<div>
										<label className="block text-sm font-semibold text-gray-700 mb-1">Nome do Local</label>
										<input name="venueName" type="text" defaultValue="Margem Esquerda do Rio Acaraú"
											className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:border-mova-red outline-none" />
									</div>

									<div className="grid grid-cols-3 gap-4">
										<div className="col-span-2">
											<label className="block text-sm font-semibold text-gray-700 mb-1">Rua</label>
											<input name="venueStreet" type="text" defaultValue="Margem Esquerda"
												className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:border-mova-red outline-none" />
										</div>
										<div>
											<label className="block text-sm font-semibold text-gray-700 mb-1">Número</label>
											<input name="venueNumber" type="text" defaultValue="S/N"
												className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:border-mova-red outline-none" />
										</div>
									</div>

									<div className="grid grid-cols-2 gap-4">
										<div>
											<label className="block text-sm font-semibold text-gray-700 mb-1">Bairro</label>
											<input name="venueNeighborhood" type="text" defaultValue="Centro"
												className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:border-mova-red outline-none" />
										</div>
										<div>
											<label className="block text-sm font-semibold text-gray-700 mb-1">Cidade</label>
											<input name="venueCity" type="text" defaultValue="Sobral"
												className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:border-mova-red outline-none" />
										</div>
									</div>

									<div>
										<label className="block text-sm font-semibold text-gray-700 mb-1">
											Ponto de Referência{" "}
											<span className="text-gray-400 font-normal">(opcional)</span>
										</label>
										<input name="venueLandmark" type="text" placeholder="Ex: Próximo à Catedral"
											className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:border-mova-red outline-none" />
									</div>
								</div>
							</div>
						)}

						{/* ABA 3: Opções */}
						{activeTab === "opcoes" && (
							<div className="space-y-6">
								<h2 className="text-lg font-bold text-mova-dark border-b border-gray-100 pb-4">
									Opções
								</h2>

								{/* price */}
								<div>
									<label className="block text-sm font-semibold text-gray-700 mb-2">Ingresso</label>
									<div className="grid grid-cols-2 gap-3">
										<div
											onClick={() => setTicketType("free")}
											className={`cursor-pointer p-4 border-2 rounded-xl transition-all ${
												ticketType === "free" ? "border-mova-red bg-red-50" : "border-gray-200 hover:border-gray-300"
											}`}
										>
											<h4 className="font-bold text-mova-dark text-sm mb-1">Gratuito</h4>
											<p className="text-xs text-gray-400">Aberto ao público geral</p>
										</div>
										<div
											onClick={() => setTicketType("paid")}
											className={`cursor-pointer p-4 border-2 rounded-xl transition-all ${
												ticketType === "paid" ? "border-mova-red bg-red-50" : "border-gray-200 hover:border-gray-300"
											}`}
										>
											<h4 className="font-bold text-mova-dark text-sm mb-1">Pago</h4>
											<p className="text-xs text-gray-400">Requer compra de ingresso</p>
										</div>
									</div>
									{ticketType === "paid" && (
										<div className="mt-3">
											<label className="block text-sm font-semibold text-gray-700 mb-1">Preço (R$)</label>
											<input name="price" type="number" min="0" step="0.01" placeholder="0,00"
												className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:border-mova-red outline-none" />
										</div>
									)}
								</div>

								{/* content_rating */}
								<div>
									<label className="block text-sm font-semibold text-gray-700 mb-1">
										Classificação Indicativa
									</label>
									<select name="contentRating" defaultValue="Livre"
										className="cursor-pointer w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:border-mova-red outline-none appearance-none"
									>
										{RATINGS.map((r) => <option key={r} value={r}>{r}</option>)}
									</select>
								</div>

								{/* venues booleans */}
								<div className="pt-4 border-t border-gray-100">
									<h3 className="text-sm font-bold text-mova-dark mb-4">Infraestrutura do Local</h3>
									<div className="grid grid-cols-2 gap-3">
										{[
											{ name: "hasAccessibility", icon: "accessible", label: "Acessibilidade" },
											{ name: "hasParkingLot", icon: "local_parking", label: "Estacionamento" },
											{ name: "hasFoodsAndDrinks", icon: "restaurant", label: "Alimentação" },
											{ name: "hasBathroom", icon: "wc", label: "Banheiros" },
										].map((item) => (
											<label key={item.name}
												className="cursor-pointer flex items-center gap-3 p-3 rounded-xl bg-gray-50 hover:bg-gray-100 transition-colors"
											>
												<input name={item.name} type="checkbox" defaultChecked
													className="w-4 h-4 accent-mova-red rounded" />
												<span className="material-symbols-outlined text-mova-red" style={{ fontSize: 16 }}>
													{item.icon}
												</span>
												<span className="text-sm text-gray-700">{item.label}</span>
											</label>
										))}
									</div>
								</div>
							</div>
						)}
					</form>
				</section>
			</main>
		</div>
	);
}