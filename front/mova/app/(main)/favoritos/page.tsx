"use client";

import { useState, useEffect } from "react";
import EventCard from "@/components/feed/eventCard";
import { useEvents } from "@/hooks/useEvents";
import { Event } from "@/types/event.types";

export default function FavoritesPage() {
	const { fetchAllEvents, isLoading } = useEvents();
	const [favoriteEvents, setFavoriteEvents] = useState<Event[]>([]);
	const [hasLoaded, setHasLoaded] = useState(false);

	useEffect(() => {
		const loadFavorites = async () => {
			try {
				// Busca todos os eventos e filtra os favoritados pelo usuário
				const data = await fetchAllEvents(0, 100);
				const favorites = data.content.filter((e) => e.isFavorited);
				setFavoriteEvents(favorites);
			} catch (error) {
				console.error("Erro ao carregar favoritos:", error);
			} finally {
				setHasLoaded(true);
			}
		};

		loadFavorites();
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);

	if (!hasLoaded && isLoading) {
		return (
			<div className="flex-1 p-6 max-w-3xl mx-auto flex items-center justify-center min-h-[60vh]">
				<div className="animate-spin rounded-full h-10 w-10 border-t-2 border-b-2 border-[#b91c1c]"></div>
			</div>
		);
	}

	return (
		<div className="flex-1 p-4 sm:p-6 max-w-5xl w-full mx-auto">
			<div className="mb-5">
				<h1 className="text-xl font-bold text-mova-dark">Meus Favoritos</h1>
				<p className="text-sm text-gray-400">
					{favoriteEvents.length > 0
						? `${favoriteEvents.length} evento${favoriteEvents.length > 1 ? "s" : ""} salvo${favoriteEvents.length > 1 ? "s" : ""}`
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
				<div className="space-y-4 md:space-y-6">
					{favoriteEvents.map((event) => (
						<EventCard key={event.id} event={event} />
					))}
				</div>
			)}
		</div>
	);
}