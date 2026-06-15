import Link from "next/link";
import { notFound } from "next/navigation";
import { featuredEvent, nextEvents } from "@/mocks/feed.mocks";

const allEvents = [featuredEvent, ...nextEvents];

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

export default async function EventDetailPage({ params }: { params: Promise<{ id: string }> }) {
	const { id } = await params;
	const event = allEvents.find((item) => item.id === id);
	if (!event) notFound();

	const image = event.pictures?.[0]?.pictureUrl;
	const startDate = formatDate(event.startsAt);
	const startTime = formatTime(event.startsAt);
	const endTime = formatTime(event.endsAt);
	const price = event.price === 0 ? "Gratuito" : `R$ ${event.price.toFixed(2)}`;
	const venue = event.venue;
	const location = venue ? `${venue.name}, ${venue.neighborhood}` : "Local não informado";

	return (
		<div className="flex-1 p-6 max-w-4xl mx-auto">
			<div className="mb-6 flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
				<div>
					<Link href="/events" className="text-sm font-medium text-gray-500 hover:text-mova-dark">
						← Voltar para todos os eventos
					</Link>
					<h1 className="mt-3 text-3xl font-bold text-mova-dark">{event.eventName}</h1>
					<p className="mt-2 text-sm text-gray-500 max-w-2xl">{event.description}</p>
				</div>
				<div className="flex gap-2">
					<span className="rounded-full bg-green-100 text-green-700 px-3 py-1 text-xs font-semibold">
						{price}
					</span>
					<span className="rounded-full bg-red-100 text-mova-red px-3 py-1 text-xs font-semibold">
						{event.tags?.[0]?.tagname ?? "Evento"}
					</span>
				</div>
			</div>

			<div className="grid gap-6 lg:grid-cols-[2fr_1fr]">
				<div className="space-y-6">
					<div className="overflow-hidden rounded-3xl bg-gray-100 shadow-sm">
						{image ? (
							<img
								src={image}
								alt={event.eventName}
								className="w-full h-72 object-cover"
							/>
						) : (
							<div className="flex h-72 items-center justify-center bg-gray-200 text-gray-500">
								Imagem não disponível
							</div>
						)}
						<div className="p-6">
							<div className="flex flex-wrap gap-3 text-gray-500 text-xs font-semibold uppercase tracking-[0.18em]">
								<span>{startDate}</span>
								<span>{startTime} - {endTime}</span>
								<span>{location}</span>
							</div>
							<h2 className="mt-4 text-lg font-bold text-mova-dark">Sobre o Evento</h2>
							<p className="mt-3 text-sm leading-6 text-gray-600">{event.description}</p>
						</div>
					</div>

					<div className="grid gap-4">
						<div className="rounded-3xl border border-gray-200 bg-white p-6 shadow-sm">
							<h3 className="text-sm font-bold text-mova-dark">Detalhes</h3>
							<div className="mt-4 space-y-3 text-sm text-gray-600">
								<div className="flex items-center gap-3">
									<span className="material-symbols-outlined text-mova-red" style={{ fontSize: 18 }}>
										calendar_today
									</span>
									<span>{startDate}, {startTime}</span>
								</div>
								<div className="flex items-center gap-3">
									<span className="material-symbols-outlined text-mova-red" style={{ fontSize: 18 }}>
										location_on
									</span>
									<span>{location}</span>
								</div>
								<div className="flex items-center gap-3">
									<span className="material-symbols-outlined text-mova-red" style={{ fontSize: 18 }}>
										attach_money
									</span>
									<span>{price}</span>
								</div>
							</div>
						</div>
						<div className="rounded-3xl border border-gray-200 bg-white p-6 shadow-sm">
							<h3 className="text-sm font-bold text-mova-dark">Localização</h3>
							<div className="mt-4 text-sm text-gray-600 space-y-2">
								<p>{venue?.name}</p>
								<p>{venue?.street}, {venue?.neighborhood}</p>
								<p>{venue?.city}</p>
							</div>
						</div>
					</div>
				</div>
			</div>

			<div className="mt-8 grid gap-6 lg:grid-cols-[1.4fr_1fr]">
				<div className="rounded-3xl border border-gray-200 bg-white p-6 shadow-sm">
					<h3 className="text-base font-bold text-mova-dark">Programação Principal</h3>
					<div className="mt-4 space-y-4">
						<div className="flex items-start gap-4">
							<div className="mt-1 h-2.5 w-2.5 rounded-full bg-mova-red" />
							<div>
								<p className="text-sm font-semibold text-mova-dark">Abertura</p>
								<p className="text-sm text-gray-500">Início do evento e recepção dos participantes.</p>
							</div>
						</div>
						<div className="flex items-start gap-4">
							<div className="mt-1 h-2.5 w-2.5 rounded-full bg-mova-red" />
							<div>
								<p className="text-sm font-semibold text-mova-dark">Show principal</p>
								<p className="text-sm text-gray-500">Atração central com muita música e cultura local.</p>
							</div>
						</div>
						<div className="flex items-start gap-4">
							<div className="mt-1 h-2.5 w-2.5 rounded-full bg-mova-red" />
							<div>
								<p className="text-sm font-semibold text-mova-dark">Encerramento</p>
								<p className="text-sm text-gray-500">Finalização do evento com celebração e despedida.</p>
							</div>
						</div>
					</div>
				</div>

				<div className="rounded-3xl border border-gray-200 bg-white p-6 shadow-sm">
					<h3 className="text-base font-bold text-mova-dark">Comodidades</h3>
					<div className="mt-4 grid gap-3">
						{venue?.hasAccessibility && (
							<div className="rounded-2xl bg-gray-50 p-3 text-sm text-gray-600">Acessibilidade</div>
						)}
						{venue?.hasBathroom && (
							<div className="rounded-2xl bg-gray-50 p-3 text-sm text-gray-600">Banheiros</div>
						)}
						{venue?.hasFoodsAndDrinks && (
							<div className="rounded-2xl bg-gray-50 p-3 text-sm text-gray-600">Praça de Alimentação</div>
						)}
						{venue?.hasParkingLot && (
							<div className="rounded-2xl bg-gray-50 p-3 text-sm text-gray-600">Estacionamento</div>
						)}
					</div>
				</div>
			</div>

			<div className="mt-8 rounded-3xl border border-gray-200 bg-white p-6 shadow-sm">
				<div className="flex items-center justify-between">
					<h3 className="text-base font-bold text-mova-dark">Eventos Semelhantes</h3>
					<Link href="/events" className="text-sm font-semibold text-mova-red hover:underline">
						Ver todos
					</Link>
				</div>
				<div className="mt-5 grid gap-4 sm:grid-cols-2">
					{allEvents
						.filter((item) => item.id !== event.id)
						.map((item) => (
							<Link
								key={item.id}
								href={`/events/${item.id}`}
								className="rounded-3xl border border-gray-200 overflow-hidden bg-white shadow-sm transition hover:border-mova-red"
							>
								<div className="h-28 overflow-hidden bg-gray-100">
									{item.pictures?.[0]?.pictureUrl && (
										<img
											src={item.pictures[0].pictureUrl}
											alt={item.eventName}
											className="w-full h-full object-cover"
										/>
									)}
								</div>
								<div className="p-4">
									<p className="text-xs text-mova-red font-bold uppercase">{item.tags?.[0]?.tagname}</p>
									<h4 className="mt-2 text-sm font-semibold text-mova-dark">{item.eventName}</h4>
									<p className="mt-2 text-xs text-gray-500 line-clamp-2">{item.description}</p>
								</div>
							</Link>
						))}
				</div>
			</div>
		</div>
	);
}
