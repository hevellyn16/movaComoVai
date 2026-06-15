import Link from "next/link";
import { Event } from "@/types/event.types";

export default function EventCard({ event }: { event: Event }) {
	const image = event.pictures?.[0]?.pictureUrl;
	const category = event.tags?.[0]?.tagname;
	const location = `${event.venue?.name}, ${event.venue?.neighborhood}`;
	const date = new Date(event.startsAt).toLocaleDateString("pt-BR");
	const price = event.price === 0 ? "Gratuito" : `R$ ${event.price.toFixed(2)}`;

	return (
		<div className="flex gap-4 items-start py-4 border-b border-gray-100">
			<Link
				href={`/events/${event.id}`}
				className="flex-1 flex gap-4 items-start group"
			>
				<div className="w-24 h-24 rounded-xl bg-gray-200 overflow-hidden shrink-0">
					{image && (
						<img
							src={image}
							alt={event.eventName}
							className="w-full h-full object-cover"
						/>
					)}
				</div>
				<div className="flex-1">
					<div className="flex items-center gap-2">
						{category && (
							<span className="text-xs font-bold text-mova-red bg-red-50 px-2 py-0.5 rounded-full">
								{category}
							</span>
						)}
						<span className="text-xs font-bold text-green-600 bg-green-50 px-2 py-0.5 rounded-full">
							{price}
						</span>
					</div>
					<div className="flex justify-between items-start mt-1">
						<h3 className="text-base font-bold text-mova-dark">{event.eventName}</h3>
					</div>
					<p className="text-xs text-gray-500 mt-1 line-clamp-2">
						{event.description}
					</p>
					<div className="flex items-center gap-3 text-gray-400 text-xs mt-2">
						<span className="flex items-center gap-1">
							<span className="material-symbols-outlined" style={{ fontSize: 12 }}>
								calendar_today
							</span>
							{date}
						</span>
						<span className="flex items-center gap-1">
							<span className="material-symbols-outlined" style={{ fontSize: 12 }}>
								location_on
							</span>
							{location}
						</span>
					</div>
				</div>
			</Link>
			<button type="button" className="text-gray-400 hover:text-mova-red">
				<span
					className="material-symbols-outlined"
					style={{ fontSize: 20 }}
				>
					{event.isFavorited ? "favorite" : "favorite_border"}
				</span>
			</button>
		</div>
	);
}
