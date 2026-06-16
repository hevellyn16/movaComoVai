"use client";

import EventCard from "@/components/feed/eventCard";
import { feedEvents } from "@/mocks/feed.mocks";

// Em produção: GET /favorites?userId=... retorna lista de events com isFavorited: true
const favoriteEvents = feedEvents.filter((e) => e.isFavorited);

export default function FavoritesPage() {
	return (
		<div className="flex-1 p-6 max-w-3xl mx-auto">
			<div className="mb-5">
				<h1 className="text-xl font-bold text-mova-dark">Meus Favoritos</h1>
				<p className="text-sm text-gray-400">
					{favoriteEvents.length > 0
						? `${favoriteEvents.length} evento${favoriteEvents.length > 1 ? "s" : ""} salvos`
						: "Nenhum evento salvo ainda"}
				</p>
			</div>

			{favoriteEvents.length === 0 ? (
				<div className="py-24 flex flex-col items-center text-center gap-3">
					<span
						className="material-symbols-outlined text-gray-200"
						style={{ fontSize: 56 }}
					>
						bookmark_border
					</span>
					<p className="text-sm font-semibold text-gray-400">
						Você ainda não salvou nenhum evento.
					</p>
					<p className="text-xs text-gray-400 max-w-xs">
						Toque no{" "}
						<span className="material-symbols-outlined align-middle text-gray-400" style={{ fontSize: 13 }}>
							bookmark_border
						</span>{" "}
						em qualquer evento para salvar aqui.
					</p>
				</div>
			) : (
				favoriteEvents.map((event) => (
					<EventCard key={event.id} event={event} />
				))
			)}
		</div>
	);
}