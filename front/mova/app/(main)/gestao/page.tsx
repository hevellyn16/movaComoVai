"use client";

import { useState, useEffect, useRef } from "react";
import { feedEvents } from "@/mocks/feed.mocks";

function formatDate(dateString: string) {
	const date = new Date(dateString);
	const months = ["Jan","Fev","Mar","Abr","Mai","Jun","Jul","Ago","Set","Out","Nov","Dez"];
	return `${date.getDate().toString().padStart(2, "0")} ${months[date.getMonth()]}, ${date.getFullYear()}`;
}

function inferStatus(startsAt: string, endsAt: string) {
	const now = Date.now();
	const start = new Date(startsAt).getTime();
	const end = new Date(endsAt).getTime();
	if (now < start) return { label: "Agendado", classes: "bg-blue-50 text-blue-600 border-blue-200/50" };
	if (now <= end) return { label: "Em andamento", classes: "bg-green-50 text-green-700 border-green-200/50" };
	return { label: "Encerrado", classes: "bg-gray-100 text-gray-500 border-gray-200/50" };
}

// Tags extraídas dos eventos — em produção: GET /tags
const ALL_TAGS = Array.from(
	new Set(feedEvents.flatMap((e) => e.tags?.map((t) => t.tagname) ?? []))
);

const ITEMS_PER_PAGE = 4;

export default function GestaoEventosPage() {
	const [searchTerm, setSearchTerm] = useState("");
	const [isFilterOpen, setIsFilterOpen] = useState(false);
	const [selectedTag, setSelectedTag] = useState<string | null>(null);
	const [currentPage, setCurrentPage] = useState(1);
  
  const filterRef = useRef<HTMLDivElement>(null);
  
useEffect(() => {
    function handleClickOutside(e: MouseEvent) {
        if (filterRef.current && !filterRef.current.contains(e.target as Node)) {
            setIsFilterOpen(false);
        }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
}, []);

	const filtered = feedEvents.filter((event) => {
		const matchesSearch = event.eventName
			.toLowerCase()
			.includes(searchTerm.toLowerCase());
		const matchesTag = selectedTag
			? event.tags?.some((t) => t.tagname === selectedTag)
			: true;
		return matchesSearch && matchesTag;
	});

	const totalPages = Math.ceil(filtered.length / ITEMS_PER_PAGE);
	const startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
	const currentEvents = filtered.slice(startIndex, startIndex + ITEMS_PER_PAGE);

	return (
		<div className="w-[calc(100vw-224px)] min-h-screen bg-gray-50/50 p-8 font-sans antialiased">
			<div className="max-w-5xl mx-auto w-full">

				{/* Header */}
				<div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 mb-12">
					<div>
						<h1 className="text-2xl font-bold text-mova-dark tracking-tight">
							Gestão de Eventos
						</h1>
						<p className="text-sm text-gray-500 mt-1">
							Painel administrativo para controle de sua produção cultural
						</p>
					</div>
					<button className="inline-flex items-center gap-2 bg-mova-red hover:bg-red-700 text-white font-semibold px-5 py-2.5 rounded-xl transition-all duration-200 shadow-sm text-sm cursor-pointer">
						+ Criar Novo Evento
					</button>
				</div>

				{/* Container */}
				<div className="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">

					{/* Barra de ferramentas */}
					<div className="p-4 border-b border-gray-100 flex flex-col sm:flex-row gap-3 justify-between items-center bg-white">
						<div className="relative w-full sm:max-w-xs">
							<span
								className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 material-symbols-outlined"
								style={{ fontSize: 18 }}
							>
								search
							</span>
							<input
								type="text"
								placeholder="Buscar por nome do evento..."
								value={searchTerm}
								onChange={(e) => {
									setSearchTerm(e.target.value);
									setCurrentPage(1);
								}}
								className="w-full pl-9 pr-4 py-2 border border-gray-200 rounded-xl text-sm focus:outline-none focus:border-mova-red transition-colors placeholder:text-gray-400"
							/>
						</div>

						{/* Filtro por tag — event_tags */}
						<div className="relative" ref={filterRef}>
							<button
								onClick={() => setIsFilterOpen(!isFilterOpen)}
								className={`inline-flex items-center gap-2 px-4 py-2 border rounded-xl text-sm font-medium transition-colors cursor-pointer ${
									isFilterOpen || selectedTag
										? "bg-gray-50 border-gray-300 text-mova-dark"
										: "border-gray-200 text-gray-700 hover:bg-gray-50"
								}`}
							>
								<span className="material-symbols-outlined" style={{ fontSize: 16 }}>
									tune
								</span>
								Filtros
								{selectedTag && (
									<span className="w-2 h-2 rounded-full bg-mova-red" />
								)}
							</button>

							{isFilterOpen && (
								<div className="absolute right-0 mt-2 w-48 bg-white border border-gray-100 rounded-xl shadow-lg z-10 py-2">
									<div className="px-4 py-2 text-xs font-semibold text-gray-400 uppercase tracking-wider">
										Tags
									</div>
									<button
										onClick={() => {
											setSelectedTag(null);
											setIsFilterOpen(false);
											setCurrentPage(1);
										}}
										className={`w-full text-left px-4 py-2 text-sm hover:bg-gray-50 transition-colors cursor-pointer ${
											selectedTag === null
												? "text-mova-red font-medium"
												: "text-gray-600"
										}`}
									>
										Todas
									</button>
									{ALL_TAGS.map((tag) => (
										<button
											key={tag}
											onClick={() => {
												setSelectedTag(tag);
												setIsFilterOpen(false);
												setCurrentPage(1);
											}}
											className={`w-full text-left px-4 py-2 text-sm hover:bg-gray-50 transition-colors cursor-pointer ${
												selectedTag === tag
													? "text-mova-red font-medium"
													: "text-gray-600"
											}`}
										>
											{tag}
										</button>
									))}
								</div>
							)}
						</div>
					</div>

					{/* Tabela */}
					<div className="overflow-x-auto">
						<table className="w-full text-left border-collapse">
							<thead>
								<tr className="bg-gray-50/70 border-b border-gray-100">
									<th className="p-4 text-xs font-semibold uppercase text-gray-500 tracking-wider w-[40%]">
										Evento
									</th>
									<th className="p-4 text-xs font-semibold uppercase text-gray-500 tracking-wider text-center">
										Status
									</th>
									<th className="p-4 text-xs font-semibold uppercase text-gray-500 tracking-wider text-center">
										Data
									</th>
									<th className="p-4 text-xs font-semibold uppercase text-gray-500 tracking-wider text-center">
										Preço
									</th>
									<th className="p-4 text-xs font-semibold uppercase text-gray-500 tracking-wider text-center">
										Ações
									</th>
								</tr>
							</thead>
							<tbody className="divide-y divide-gray-100">
								{currentEvents.map((event) => {
									const image = event.pictures?.[0]?.pictureUrl;
									const status = inferStatus(event.startsAt, event.endsAt);

									return (
										<tr
											key={event.id}
											className="hover:bg-gray-50/50 transition-colors group"
										>
											{/* Nome + local */}
											<td className="p-4">
												<div className="flex items-center gap-3">
													<div className="w-12 h-12 rounded-lg bg-gray-100 overflow-hidden shrink-0 border border-gray-200/60 shadow-sm">
														{image ? (
															<img
																src={image}
																alt={event.eventName}
																className="w-full h-full object-cover transition-transform group-hover:scale-105"
															/>
														) : (
															<div className="w-full h-full bg-red-50 flex items-center justify-center font-bold text-mova-red">
																{event.eventName.charAt(0)}
															</div>
														)}
													</div>
													<div>
														<h4 className="font-semibold text-mova-dark text-sm leading-snug">
															{event.eventName}
														</h4>
														<p className="text-xs text-gray-400 mt-0.5">
															{event.venue?.name ?? "Local não informado"}
														</p>
														{/* Tags — event_tags */}
														{event.tags && event.tags.length > 0 && (
															<span className="inline-block mt-1 text-[10px] font-bold text-mova-red bg-red-50 px-1.5 py-0.5 rounded-full">
																{event.tags[0].tagname}
															</span>
														)}
													</div>
												</div>
											</td>

											{/* Status inferido via starts_at / ends_at */}
											<td className="p-4 text-center">
												<span
													className={`inline-flex items-center px-2 py-0.5 rounded-md text-xs font-semibold border ${status.classes}`}
												>
													{status.label}
												</span>
											</td>

											{/* Data */}
											<td className="p-4 text-sm text-gray-600 font-medium text-center">
												{formatDate(event.startsAt)}
											</td>

											{/* Preço */}
											<td className="p-4 text-sm text-center">
												{event.price === 0 ? (
													<span className="inline-flex items-center px-2 py-0.5 rounded-md text-xs font-semibold bg-green-50 text-green-700 border border-green-200/50">
														Gratuito
													</span>
												) : (
													<span className="font-medium text-gray-700">
														{event.price.toLocaleString("pt-BR", {
															style: "currency",
															currency: "BRL",
														})}
													</span>
												)}
											</td>

											{/* Ações */}
											<td className="p-4 text-center">
												<div className="flex items-center justify-center gap-1.5">
													<button
														className="p-1.5 text-gray-400 hover:text-mova-dark hover:bg-gray-100 rounded-lg transition-all cursor-pointer"
														title="Visualizar"
													>
														<span className="material-symbols-outlined" style={{ fontSize: 18 }}>
															visibility
														</span>
													</button>
													<button
														className="p-1.5 text-gray-400 hover:text-yellow-600 hover:bg-yellow-50 rounded-lg transition-all cursor-pointer"
														title="Editar"
													>
														<span className="material-symbols-outlined" style={{ fontSize: 18 }}>
															edit
														</span>
													</button>
													<button
														className="p-1.5 text-gray-400 hover:text-mova-red hover:bg-red-50 rounded-lg transition-all cursor-pointer"
														title="Excluir"
													>
														<span className="material-symbols-outlined" style={{ fontSize: 18 }}>
															delete
														</span>
													</button>
												</div>
											</td>
										</tr>
									);
								})}

								{filtered.length === 0 && (
									<tr>
										<td colSpan={5} className="p-12 text-center">
											<span
												className="material-symbols-outlined text-gray-200 block mb-2"
												style={{ fontSize: 40 }}
											>
												event_busy
											</span>
											<p className="text-sm text-gray-400">
												Nenhum evento encontrado.
											</p>
										</td>
									</tr>
								)}
							</tbody>
						</table>
					</div>

					{/* Rodapé + paginação */}
					<div className="p-4 border-t border-gray-100 flex flex-col sm:flex-row justify-between items-center gap-3 bg-gray-50/40">
						<p className="text-xs text-gray-500 font-medium">
							Exibindo{" "}
							{filtered.length === 0 ? 0 : startIndex + 1} a{" "}
							{Math.min(startIndex + ITEMS_PER_PAGE, filtered.length)} de{" "}
							{filtered.length} eventos
						</p>

						{totalPages > 1 && (
							<div className="flex items-center gap-1.5">
								<button
									onClick={() => setCurrentPage((p) => Math.max(p - 1, 1))}
									disabled={currentPage === 1}
									className="p-1 rounded-lg border border-gray-200 hover:bg-white text-gray-500 disabled:opacity-50 transition-colors cursor-pointer"
								>
									‹
								</button>
								{Array.from({ length: totalPages }, (_, i) => i + 1).map((page) => (
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
								<button
									onClick={() => setCurrentPage((p) => Math.min(p + 1, totalPages))}
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