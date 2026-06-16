"use client";

import Link from "next/link";
import { notFound } from "next/navigation";
import { use, useState } from "react";
import { feedEvents } from "@/mocks/feed.mocks";
import { commentsByEvent } from "@/mocks/comments.mocks";
import CommentSection from "@/components/comments/commentSection";

function formatDate(dateString: string) {
	return new Date(dateString).toLocaleDateString("pt-BR", {
		year: "numeric",
		month: "long",
		day: "2-digit",
	});
}

function formatTime(dateString: string) {
	return new Date(dateString).toLocaleTimeString("pt-BR", {
		hour: "2-digit",
		minute: "2-digit",
	});
}

function inferStatus(startsAt: string, endsAt: string) {
	const now = Date.now();
	const start = new Date(startsAt).getTime();
	const end = new Date(endsAt).getTime();
	if (now < start) return { label: "Agendado", color: "bg-blue-50 text-blue-600" };
	if (now <= end) return { label: "Em andamento", color: "bg-green-50 text-green-600" };
	return null; // encerrado — sem status visual, banco não armazena isso
}

export default function EventDetailPage({
	params,
}: {
	params: Promise<{ id: string }>;
}) {
	const { id } = use(params);
	const event = feedEvents.find((item) => item.id === id);
	if (!event) notFound();

	const [isLiked, setIsLiked] = useState(event.isLiked ?? false);
	const [isFavorited, setIsFavorited] = useState(event.isFavorited ?? false);
	const [likesCount, setLikesCount] = useState(event.likesCount ?? 0);

	const image = event.pictures?.[0]?.pictureUrl;
	const extraPictures = event.pictures?.slice(1) ?? [];
	const startDate = formatDate(event.startsAt);
	const startTime = formatTime(event.startsAt);
	const endTime = formatTime(event.endsAt);
	const price = event.price === 0 ? "Gratuito" : `R$ ${event.price.toFixed(2)}`;
	const venue = event.venue;
	const location = venue
		? `${venue.name}, ${venue.neighborhood}`
		: "Local não informado";
	const status = inferStatus(event.startsAt, event.endsAt);

	// "Semelhantes" = mesmo banco de dados, mesma tag — sem algoritmo, só filtragem local
	const similar = feedEvents.filter(
		(item) =>
			item.id !== event.id &&
			item.tags?.some((t) => event.tags?.some((et) => et.id === t.id))
	);

	function handleLike() {
		setIsLiked((prev) => !prev);
		setLikesCount((prev) => (isLiked ? prev - 1 : prev + 1));
		// TODO: PATCH /events/:id/like
	}

	function handleFavorite() {
		setIsFavorited((prev) => !prev);
		// TODO: PATCH /events/:id/favorite
	}

	const amenities = [
		{ key: "hasAccessibility", icon: "accessible", label: "Acessibilidade" },
		{ key: "hasBathroom", icon: "wc", label: "Banheiros" },
		{ key: "hasFoodsAndDrinks", icon: "restaurant", label: "Alimentação" },
		{ key: "hasParkingLot", icon: "local_parking", label: "Estacionamento" },
	] as const;

	const availableAmenities = venue
		? amenities.filter((a) => venue[a.key])
		: [];

	return (
		<div className="flex-1 p-6 max-w-4xl mx-auto">
			{/* Voltar */}
			<Link
				href="/events"
				className="inline-flex items-center gap-1 text-sm font-medium text-gray-500 hover:text-mova-dark mb-6"
			>
				<span className="material-symbols-outlined" style={{ fontSize: 16 }}>
					arrow_back
				</span>
				Todos os eventos
			</Link>

			{/* Cabeçalho */}
			<div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between mb-6">
				<div className="flex-1">
					<div className="flex flex-wrap items-center gap-2 mb-2">
						{event.tags?.map((tag) => (
							<span
								key={tag.id}
								className="text-xs font-bold text-mova-red bg-red-50 px-2 py-0.5 rounded-full"
							>
								{tag.tagname}
							</span>
						))}
						<span
							className={`text-xs font-bold px-2 py-0.5 rounded-full ${
								event.price === 0
									? "text-green-600 bg-green-50"
									: "text-blue-600 bg-blue-50"
							}`}
						>
							{price}
						</span>
						{status && (
							<span
								className={`text-xs font-bold px-2 py-0.5 rounded-full ${status.color}`}
							>
								{status.label}
							</span>
						)}
					</div>
					<h1 className="text-3xl font-bold text-mova-dark">{event.eventName}</h1>
					<p className="mt-2 text-sm text-gray-500 max-w-2xl leading-relaxed">
						{event.description}
					</p>
				</div>

				{/* Ações */}
				<div className="flex gap-3 shrink-0 mt-1">
					<button
						onClick={handleLike}
						className="flex items-center gap-1.5 text-sm font-semibold text-gray-500 cursor-pointer hover:text-mova-red transition-colors"
						aria-label={isLiked ? "Descurtir" : "Curtir"}
					>
						<span
							className={`material-symbols-outlined ${isLiked ? "text-mova-red" : ""}`}
							style={{ fontSize: 22 }}
						>
							{isLiked ? "favorite" : "favorite_border"}
						</span>
						{likesCount > 0 && <span>{likesCount}</span>}
					</button>
					<button
						onClick={handleFavorite}
						className="flex items-center gap-1.5 text-sm font-semibold cursor-pointer text-gray-500 hover:text-yellow-500 transition-colors"
						aria-label={isFavorited ? "Remover dos favoritos" : "Salvar"}
					>
						<span
							className={`material-symbols-outlined ${isFavorited ? "text-yellow-400" : ""}`}
							style={{ fontSize: 22 }}
						>
							{isFavorited ? "bookmark" : "bookmark_border"}
						</span>
					</button>
				</div>
			</div>

			<div className="grid gap-6 lg:grid-cols-[2fr_1fr]">
				{/* Coluna principal */}
				<div className="space-y-6">
					{/* Imagem principal */}
					<div className="overflow-hidden rounded-3xl bg-gray-100 shadow-sm">
						{image ? (
							<img
								src={image}
								alt={event.eventName}
								className="w-full h-72 object-cover"
							/>
						) : (
							<div className="flex h-72 items-center justify-center bg-gray-100">
								<span
									className="material-symbols-outlined text-gray-300"
									style={{ fontSize: 48 }}
								>
									image
								</span>
							</div>
						)}
					</div>

					{/* Galeria extra — event_pictures */}
					{extraPictures.length > 0 && (
						<div className="grid grid-cols-3 gap-3">
							{extraPictures.map((pic) => (
								<div
									key={pic.id}
									className="aspect-square overflow-hidden rounded-2xl bg-gray-100"
								>
									<img
										src={pic.pictureUrl}
										alt=""
										className="w-full h-full object-cover"
									/>
								</div>
							))}
						</div>
					)}

					{/* Classificação indicativa */}
					<div className="rounded-3xl border border-gray-200 bg-white p-6 shadow-sm">
						<h3 className="text-sm font-bold text-mova-dark mb-3">
							Classificação Indicativa
						</h3>
						<span className="inline-block bg-gray-100 text-gray-600 text-sm font-semibold px-3 py-1 rounded-full">
							{event.contentRating}
						</span>
					</div>
				</div>

				{/* Coluna lateral */}
				<div className="space-y-4">
					{/* Detalhes */}
					<div className="rounded-3xl border border-gray-200 bg-white p-6 shadow-sm">
						<h3 className="text-sm font-bold text-mova-dark mb-4">Detalhes</h3>
						<div className="space-y-3 text-sm text-gray-600">
							<div className="flex items-start gap-3">
								<span
									className="material-symbols-outlined text-mova-red mt-0.5"
									style={{ fontSize: 18 }}
								>
									calendar_today
								</span>
								<div>
									<p>{startDate}</p>
									<p className="text-gray-400">
										{startTime} até {endTime}
									</p>
								</div>
							</div>
							<div className="flex items-start gap-3">
								<span
									className="material-symbols-outlined text-mova-red mt-0.5"
									style={{ fontSize: 18 }}
								>
									location_on
								</span>
								<div>
									<p>{venue?.name}</p>
									{venue && (
										<p className="text-gray-400">
											{venue.street}, {venue.neighborhood}
											{venue.number !== "S/N" ? `, ${venue.number}` : ""}
										</p>
									)}
									{venue?.landmark && (
										<p className="text-gray-400 text-xs mt-0.5">
											Perto de: {venue.landmark}
										</p>
									)}
								</div>
							</div>
							<div className="flex items-center gap-3">
								<span
									className="material-symbols-outlined text-mova-red"
									style={{ fontSize: 18 }}
								>
									attach_money
								</span>
								<span>{price}</span>
							</div>
						</div>
					</div>

					{/* Comodidades — venue booleans */}
					{availableAmenities.length > 0 && (
						<div className="rounded-3xl border border-gray-200 bg-white p-6 shadow-sm">
							<h3 className="text-sm font-bold text-mova-dark mb-4">
								Comodidades
							</h3>
							<div className="grid grid-cols-1 gap-2">
								{availableAmenities.map((a) => (
									<div
										key={a.key}
										className="flex items-center gap-2 rounded-2xl bg-gray-50 p-2.5 text-xs text-gray-600 font-medium overflow-hidden"
									>
										<span
											className="material-symbols-outlined text-mova-red shrink-0"
											style={{ fontSize: 16 }}
										>
											{a.icon}
										</span>
										<span className="truncate">{a.label}</span>
									</div>
								))}
							</div>
						</div>
					)}
				</div>
			</div>

			{/* Eventos semelhantes — mesma tag */}
			{similar.length > 0 && (
				<div className="mt-8 rounded-3xl border border-gray-200 bg-white p-6 shadow-sm">
					<div className="flex items-center justify-between mb-5">
						<h3 className="text-base font-bold text-mova-dark">
							Mais em {event.tags?.[0]?.tagname ?? "Sobral"}
						</h3>
						<Link
							href="/events"
							className="text-sm font-semibold text-mova-red hover:underline"
						>
							Ver todos
						</Link>
					</div>
					<div className="grid gap-4 sm:grid-cols-2">
						{similar.map((item) => (
							<Link
								key={item.id}
								href={`/events/${item.id}`}
								className="rounded-2xl border border-gray-200 overflow-hidden bg-white shadow-sm transition hover:border-mova-red group"
							>
								<div className="h-28 overflow-hidden bg-gray-100">
									{item.pictures?.[0]?.pictureUrl ? (
										<img
											src={item.pictures[0].pictureUrl}
											alt={item.eventName}
											className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
										/>
									) : (
										<div className="w-full h-full flex items-center justify-center">
											<span
												className="material-symbols-outlined text-gray-300"
												style={{ fontSize: 28 }}
											>
												image
											</span>
										</div>
									)}
								</div>
								<div className="p-4">
									<p className="text-xs text-mova-red font-bold uppercase">
										{item.tags?.[0]?.tagname}
									</p>
									<h4 className="mt-1 text-sm font-semibold text-mova-dark">
										{item.eventName}
									</h4>
									<p className="mt-1 text-xs text-gray-400">
										{formatDate(item.startsAt)}
									</p>
								</div>
							</Link>
						))}
					</div>
				</div>
			)}
		{/* Comentários — comments + comment_likes + answers */}
		<CommentSection
			eventId={event.id}
			initialComments={commentsByEvent[event.id] ?? []}
		/>
		</div>
	);
}