"use client";

import { useState, useEffect } from "react";
import EventCard from "@/components/feed/eventCard";
import { useEvents } from "@/hooks/useEvents"; 
import { useTags } from "@/hooks/useTags";    
import { Event } from "@/types/event.types";

export default function FeedPage() {
    const [activeTag, setActiveTag] = useState("Tudo");
    const [events, setEvents] = useState<Event[]>([]);
    const { fetchAllEvents, isLoading: isEventsLoading, error: eventsError } = useEvents();
    const { tags, isLoading: isTagsLoading, fetchAllTags } = useTags();

    useEffect(() => {
        const loadFeedData = async () => {
            // Inicia a busca de tags
            fetchAllTags();
            
            try {
                const data = await fetchAllEvents(0, 20);
                setEvents(data.content);
            } catch (err) {
                console.error("Falha ao carregar eventos", err);
            }
        };

        loadFeedData();
    }, []);
    const ALL_TAGS = ["Tudo", ...tags.map((t) => t.tagName)];

    const filtered =
        activeTag === "Tudo"
            ? events
            : events.filter((e) =>
                  e.tags?.some((t) => t.tagname === activeTag)
              );

    if (isEventsLoading || isTagsLoading) {
        return (
            <div className="flex-1 p-6 max-w-3xl mx-auto flex flex-col justify-center items-center py-32">
                <div className="animate-spin rounded-full h-10 w-10 border-t-2 border-b-2 border-[#b91c1c] mb-4"></div>
                <p className="text-gray-500 font-medium animate-pulse">Carregando a pulsação cultural de Sobral...</p>
            </div>
        );
    }

    if (eventsError) {
        return (
            <div className="flex-1 p-6 max-w-3xl mx-auto text-center py-32">
                <p className="text-red-500 font-medium">Ops! {eventsError}</p>
                <button 
                    onClick={() => window.location.reload()} 
                    className="mt-4 text-[#b91c1c] font-bold hover:underline cursor-pointer"
                >
                    Tentar novamente
                </button>
            </div>
        );
    }

    return (
        <div className="flex-1 p-6 max-w-3xl mx-auto">
            {/* Cabeçalho */}
            <div className="mb-5">
                <h1 className="text-2xl font-bold text-[#b91c1c]">Eventos em Sobral</h1>
                <p className="text-sm text-gray-500 font-medium">O que está acontecendo por aqui</p>
            </div>

            {/* Filtro por tag */}
            <div className="flex gap-2 overflow-x-auto pb-2 mb-6 scrollbar-hide">
                {ALL_TAGS.map((tag) => (
                    <button
                        key={tag}
                        onClick={() => setActiveTag(tag)}
                        className={`cursor-pointer text-xs px-4 py-2 rounded-full font-bold whitespace-nowrap transition-colors ${
                            tag === activeTag
                                ? "bg-[#b91c1c] text-white shadow-md"
                                : "bg-gray-100 text-gray-600 hover:bg-gray-200"
                        }`}
                    >
                        {tag}
                    </button>
                ))}
            </div>

            {/* Lista de eventos */}
            {filtered.length === 0 ? (
                <div className="py-20 text-center bg-gray-50 rounded-2xl border border-gray-100 mt-4">
                    <span
                        className="material-symbols-outlined text-gray-300 block mb-3"
                        style={{ fontSize: 56 }}
                    >
                        event_busy
                    </span>
                    <p className="text-gray-500 font-medium">
                        Nenhum evento encontrado para essa categoria.
                    </p>
                </div>
            ) : (
                <div className="space-y-6">
                    {filtered.map((event) => (
                        <EventCard key={event.id} event={event} />
                    ))}
                </div>
            )}
        </div>
    );
}