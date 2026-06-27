"use client";

import Link from "next/link";
import { useState } from "react";
import { useEvents } from "@/hooks/useEvents";
import { Event } from "@/types/event.types";

export default function EventCard({ event }: { event: Event }) {
    const { toggleLike, toggleFavorite } = useEvents();

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

    async function handleLike(e: React.MouseEvent) {
        e.preventDefault();

        // 1. Atualização Otimista (Muda a interface instantaneamente)
        setIsLiked((prev) => !prev);
        setLikesCount((prev) => (isLiked ? prev - 1 : prev + 1));

        try {
            // 2. Chama a API
            await toggleLike(event.id, isLiked);
        } catch (error) {
            // 3. Se der erro, desfaz a ação na interface
            setIsLiked((prev) => !prev);
            setLikesCount((prev) => (isLiked ? prev + 1 : prev - 1));
            console.error("Erro ao processar a curtida:", error);
        }
    }

    async function handleFavorite(e: React.MouseEvent) {
        e.preventDefault();

        // 1. Atualização Otimista
        setIsFavorited((prev) => !prev);

        try {
            // 2. Chama a API
            await toggleFavorite(event.id, isFavorited);
        } catch (error) {
            // 3. Se der erro, desfaz a ação
            setIsFavorited((prev) => !prev);
            console.error("Erro ao favoritar o evento:", error);
        }
    }

    return (
        <div className="flex items-start gap-3 md:gap-4 bg-white rounded-2xl md:rounded-3xl border border-gray-100 p-4 sm:p-5 md:p-6 shadow-sm hover:shadow-md hover:border-gray-200 transition-all group/card">
            <Link
                href={`/events/${event.id}`}
                className="flex-1 flex gap-4 md:gap-6 items-start group"
            >
                {/* Thumbnail */}
                <div className="w-24 h-24 sm:w-32 sm:h-32 md:w-48 md:h-48 rounded-xl md:rounded-2xl bg-gray-100 overflow-hidden shrink-0">
                    {image ? (
                        <img
                            src={image}
                            alt={event.eventName}
                            className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
                        />
                    ) : (
                        <div className="w-full h-full flex items-center justify-center">
                            <span
                                className="material-symbols-outlined text-gray-300 md:text-[40px]"
                                style={{ fontSize: 24 }}
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
                            <span className="text-[10px] md:text-xs font-bold text-[#b91c1c] uppercase tracking-wider bg-red-50 px-2 py-0.5 rounded-full">
                                {category}
                            </span>
                        )}
                        <span
                            className={`text-[10px] md:text-xs font-bold px-2 py-0.5 rounded-full ${event.price === 0
                                    ? "text-green-600 bg-green-50"
                                    : "text-blue-600 bg-blue-50"
                                }`}
                        >
                            {price}
                        </span>
                    </div>

                    {/* Nome */}
                    <h3 className="mt-1.5 md:mt-3 text-base md:text-2xl font-bold text-gray-900 group-hover:text-[#b91c1c] transition-colors leading-tight line-clamp-2">
                        {event.eventName}
                    </h3>

                    {/* Descrição */}
                    <p className="mt-1 md:mt-2 text-xs md:text-sm text-gray-500 line-clamp-1 md:line-clamp-2 leading-relaxed">
                        {event.description}
                    </p>

                    {/* Meta */}
                    <div className="flex items-center gap-2 sm:gap-4 text-gray-400 text-xs md:text-sm mt-2 md:mt-4 flex-wrap">
                        <span className="flex items-center gap-1">
                            <span
                                className="material-symbols-outlined md:text-[18px]"
                                style={{ fontSize: 14 }}
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
                    className="flex flex-col items-center cursor-pointer gap-0.5 text-gray-400 hover:text-[#b91c1c] transition-colors"
                    aria-label={isLiked ? "Descurtir evento" : "Curtir evento"}
                >
                    <span
                        className={`material-symbols-outlined transition-colors ${isLiked ? "text-[#b91c1c]" : ""
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
                        className={`material-symbols-outlined transition-colors ${isFavorited ? "text-yellow-400" : ""
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