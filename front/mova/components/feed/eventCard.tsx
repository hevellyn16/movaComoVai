"use client";

import Link from "next/link";
import { useState } from "react";
import { Event } from "@/types/event.types";

export default function EventCard({ event }: { event: Event }) {
	const [isLiked, setIsLiked] = useState(event.isLiked ?? false);
	const [isFavorited, setIsFavorited] = useState(event.isFavorited ?? false);
	const [likesCount, setLikesCount] = useState(event.likesCount ?? 0);

	const image = event.pictures?.[0]?.pictureUrl;
	const category = event.tags?.[0]?.tagname;
	const location = event.venue
		? `${event.venue.name}, ${event.venue.neighborhood}`
		: null;

	const date = new Date(event.startsAt).toLocaleDateString("pt-BR", {
		day: "2-digit",
		month: "short",
	});

	const time = new Date(event.startsAt).toLocaleTimeString("pt-BR", {
		hour: "2-digit",
		minute: "2-digit",
	});

	const price =
		event.price === 0 ? "Gratuito" : `R$ ${event.price.toFixed(2)}`;

	function handleLike(e: React.MouseEvent) {
		e.preventDefault();
		setIsLiked((prev) => !prev);
		setLikesCount((prev) => (isLiked ? prev - 1 : prev + 1));
		// TODO: chamar API PATCH /events/:id/like
	}

	function handleFavorite(e: React.MouseEvent) {
		e.preventDefault();
		setIsFavorited((prev) => !prev);
		// TODO: chamar API PATCH /events/:id/favorite
	}

	return (
		<div className="flex gap-4 items-start py-4 border-b border-gray-100">
			<Link
				href={`/events/${event.id}`}
				className="flex-1 flex gap-4 items-start group"
			>
				{/* Thumbnail */}
				<div className="w-24 h-24 rounded-xl bg-gray-100 overflow-hidden shrink-0">
					{image ? (
						<img
							src={image}
							alt={event.eventName}
							className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
						/>
					) : (
						<div className="w-full h-full flex items-center justify-center">
							<span
								className="material-symbols-outlined text-gray-300"
								style={{ fontSize: 32 }}
							>
								image
							</span>
						</div>
					)}
				</div>

				{/* Conteúdo */}
				<div className="flex-1 min-w-0">
					{/* Badges */}
					<div className="flex items-center gap-2 flex-wrap">
						{category && (
							<span className="text-xs font-bold text-mova-red bg-red-50 px-2 py-0.5 rounded-full">
								{category}
							</span>
						)}
						<span
							className={`text-xs font-bold px-2 py-0.5 rounded-full ${
								event.price === 0
									? "text-green-600 bg-green-50"
									: "text-blue-600 bg-blue-50"
							}`}
						>
							{price}
						</span>
					</div>

					{/* Nome */}
					<h3 className="text-base font-bold text-mova-dark mt-1 truncate group-hover:text-mova-red transition-colors">
						{event.eventName}
					</h3>

					{/* Descrição */}
					<p className="text-xs text-gray-500 mt-0.5 line-clamp-2 leading-relaxed">
						{event.description}
					</p>

					{/* Meta */}
					<div className="flex items-center gap-3 text-gray-400 text-xs mt-2">
						<span className="flex items-center gap-1">
							<span
								className="material-symbols-outlined"
								style={{ fontSize: 12 }}
							>
								calendar_today
							</span>
							{date} · {time}
						</span>
						{location && (
							<span className="flex items-center gap-1 truncate">
								<span
									className="material-symbols-outlined"
									style={{ fontSize: 12 }}
								>
									location_on
								</span>
								{location}
							</span>
						)}
					</div>
				</div>
			</Link>

			{/* Ações */}
			<div className="flex flex-col items-center gap-3 shrink-0">
				{/* Curtir */}
				<button
					type="button"
					onClick={handleLike}
					className="flex flex-col items-center cursor-pointer gap-0.5 text-gray-400 hover:text-mova-red transition-colors"
					aria-label={isLiked ? "Descurtir evento" : "Curtir evento"}
				>
					<span
						className={`material-symbols-outlined transition-colors ${
							isLiked ? "text-mova-red" : ""
						}`}
						style={{ fontSize: 20 }}
					>
						{isLiked ? "favorite" : "favorite_border"}
					</span>
					{likesCount > 0 && (
						<span className="text-[10px] font-semibold leading-none">
							{likesCount}
						</span>
					)}
				</button>

				{/* Favoritar */}
				<button
					type="button"
					onClick={handleFavorite}
					className="text-gray-400 hover:text-yellow-500 cursor-pointer transition-colors"
					aria-label={isFavorited ? "Remover dos favoritos" : "Salvar nos favoritos"}
				>
					<span
						className={`material-symbols-outlined transition-colors ${
							isFavorited ? "text-yellow-400" : ""
						}`}
						style={{ fontSize: 20 }}
					>
						{isFavorited ? "bookmark" : "bookmark_border"}
					</span>
				</button>
			</div>
		</div>
	);
}