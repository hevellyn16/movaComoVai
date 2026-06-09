import { Event } from "@/types/event.types";

export default function FeaturedEvent({ event }: { event: Event }) {
	const now = new Date();
	const start = new Date(event.startsAt);
	const end = new Date(event.endsAt);
	const status =
		now >= start && now <= end ? "Em andamento" : now < start ? "Agendado" : null;

	const image = event.pictures?.[0]?.pictureUrl;
	const isFree = event.price === 0;
	const location = `${event.venue?.name}, ${event.venue?.neighborhood}`;
	const date = new Date(event.startsAt).toLocaleDateString("pt-BR");
	const time = new Date(event.startsAt).toLocaleTimeString("pt-BR", {
		hour: "2-digit",
		minute: "2-digit",
	});

	return (
		<div className="relative w-full rounded-2xl overflow-hidden h-64 bg-mova-dark">
			{image && (
				<img
					src={image}
					alt={event.eventName}
					className="absolute inset-0 w-full h-full object-cover"
				/>
			)}
			<div className="absolute inset-0 bg-gradient-to-t from-black/80 to-transparent z-10" />

			{/* Tags */}
			<div className="absolute top-3 left-3 z-20 flex gap-2">
				{isFree && (
					<span className="bg-green-500 text-white text-xs font-bold px-2 py-1 rounded-full">
						Gratuito
					</span>
				)}
			</div>

			{/* Favoritar */}
			<button className="absolute top-3 right-3 z-20 bg-white/20 backdrop-blur-sm rounded-full p-1.5">
				<span
					className="material-symbols-outlined text-white"
					style={{ fontSize: 20 }}
				>
					{event.isFavorited ? "favorite" : "favorite_border"}
				</span>
			</button>

			{/* Info */}
			<div className="absolute bottom-0 left-0 z-20 p-4">
				{status && (
					<span className="text-xs text-gray-300 mb-1 block">{status}</span>
				)}
				<h2 className="text-white text-2xl font-bold leading-tight mb-2">
					{event.eventName}
				</h2>
				<div className="flex items-center gap-3 text-gray-300 text-xs">
					<span className="flex items-center gap-1">
						<span className="material-symbols-outlined" style={{ fontSize: 14 }}>
							calendar_today
						</span>
						{date}, {time}
					</span>
					<span className="flex items-center gap-1">
						<span className="material-symbols-outlined" style={{ fontSize: 14 }}>
							location_on
						</span>
						{location}
					</span>
				</div>
			</div>
		</div>
	);
}
