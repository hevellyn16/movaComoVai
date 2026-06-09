import EventCard from "@/components/feed/eventCard";
import FeaturedEvent from "@/components/feed/featuredEvent";
import { featuredEvent, nextEvents } from "@/mocks/feed.mocks";
import Link from "next/link";

const category = ["Tudo", "Música", "Teatro"];

export default function FeedPage() {
	return (
		<div className="flex-1 p-6 max-w-3xl items-center mx-auto">
			{/* Eventos em Destaque */}
			<div className="flex justify-between items-center mb-3">
				<div>
					<h1 className="text-xl font-bold text-mova-dark">Eventos em Destaque</h1>
					<p className="text-sm text-gray-400">O pulsar cultural de Sobral hoje</p>
				</div>
				<Link
					href="/events"
					className="text-sm font-semibold text-mova-red cursor-pointer hover:underline"
				>
					Ver Todos
				</Link>
			</div>

			<FeaturedEvent event={featuredEvent} />

			{/* Próximos Eventos */}
			<div className="mt-8 flex justify-between items-center mb-1">
				<h2 className="text-base font-bold text-mova-dark">Próximos Eventos</h2>
				<div className="flex gap-2">
					{category.map((cat) => (
						<button
							key={cat}
							className={`text-xs px-3 py-1 rounded-full font-semibold transition-colors ${
								cat === "Tudo"
									? "bg-mova-dark text-white"
									: "bg-gray-100 text-gray-500 hover:bg-gray-200"
							}`}
						>
							{cat}
						</button>
					))}
				</div>
			</div>

			{nextEvents.map((event) => (
				<EventCard key={event.id} event={event} />
			))}
		</div>
	);
}
