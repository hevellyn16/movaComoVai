"use client";

import { useState } from "react";
import EventCard from "@/components/feed/eventCard";
import { feedEvents } from "@/mocks/feed.mocks";

// Tags extraídas dos eventos — em produção viriam da API /tags
const ALL_TAGS = ["Tudo", ...Array.from(
	new Set(feedEvents.flatMap((e) => e.tags?.map((t) => t.tagname) ?? []))
)];

export default function FeedPage() {
	const [activeTag, setActiveTag] = useState("Tudo");

	const filtered =
		activeTag === "Tudo"
			? feedEvents
			: feedEvents.filter((e) =>
					e.tags?.some((t) => t.tagname === activeTag)
			);

	return (
		<div className="flex-1 p-6 max-w-3xl mx-auto">
			{/* Cabeçalho */}
			<div className="mb-5">
				<h1 className="text-xl font-bold text-mova-dark">Eventos em Sobral</h1>
				<p className="text-sm text-gray-400">O que está acontecendo por aqui</p>
			</div>

			{/* Filtro por tag */}
			<div className="flex gap-2 overflow-x-auto pb-1 mb-1 scrollbar-hide">
				{ALL_TAGS.map((tag) => (
					<button
						key={tag}
						onClick={() => setActiveTag(tag)}
						className={`text-xs px-3 py-1.5 rounded-full font-semibold whitespace-nowrap transition-colors ${
							tag === activeTag
								? "bg-mova-dark text-white"
								: "bg-gray-100 text-gray-500 hover:bg-gray-200"
						}`}
					>
						{tag}
					</button>
				))}
			</div>

			{/* Lista de eventos */}
			{filtered.length === 0 ? (
				<div className="py-16 text-center">
					<span
						className="material-symbols-outlined text-gray-200 block mb-2"
						style={{ fontSize: 48 }}
					>
						event_busy
					</span>
					<p className="text-sm text-gray-400">
						Nenhum evento encontrado para essa categoria.
					</p>
				</div>
			) : (
				filtered.map((event) => (
					<EventCard key={event.id} event={event} />
				))
			)}
		</div>
	);
}