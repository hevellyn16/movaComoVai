export default function Header() {
	return (
		<header className="w-full bg-white shadow-sm px-6 py-3 flex items-center justify-between gap-4">
			<span className="text-mova-red font-bold text-xl whitespace-nowrap">
				MoVa Sobral
			</span>

			<div className="flex-1 max-w-xl">
				<div className="flex items-center gap-2 bg-[#F1F1F1] rounded-full px-4 py-2">
					<span
						className="material-symbols-outlined text-gray-400"
						style={{ fontSize: 20 }}
					>
						search
					</span>
					<input
						type="text"
						placeholder="Buscar evento cultural em Sobral..."
						className="bg-transparent outline-none text-sm text-gray-500 w-full font-sans"
					/>
				</div>
			</div>

			<div className="flex items-center gap-4">
				<button className="cursor-pointer">
					<span
						className="material-symbols-outlined text-mova-dark hover:text-mova-red transition-colors cursor-pointer"
						style={{ fontSize: 24 }}
					>
						notifications
					</span>
				</button>
				<button className="cursor-pointer">
					<span
						className="material-symbols-outlined text-mova-dark hover:text-mova-red transition-colors cursor-pointer"
						style={{ fontSize: 24 }}
					>
						account_circle
					</span>
				</button>
			</div>
		</header>
	);
}
