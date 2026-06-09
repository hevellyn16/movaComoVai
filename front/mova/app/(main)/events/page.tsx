import EventCard from "@/components/feed/eventCard";
import { nextEvents, featuredEvent } from "@/mocks/feed.mocks";

const allEvents = [featuredEvent, ...nextEvents];
const categories = ["Tudo", "Música", "Teatro"];

export default function EventsPage() {
	return (
		<div className="flex-1 p-6 max-w-3xl mx-auto">
			<div className="flex justify-between items-center mb-6">
				<h1 className="text-xl font-bold text-mova-dark">Todos os Eventos</h1>
			</div>

			{/* Filtros */}
			<div className="flex gap-2 mb-4">
				{categories.map((cat) => (
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

			{/* Lista */}
			{allEvents.map((event) => (
				<EventCard key={event.id} event={event} />
			))}
		</div>
	);
}
