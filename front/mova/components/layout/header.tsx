"use client";

import Link from "next/link";
import { useAuth } from "@/hooks/useAuth";

export default function Header() {
	const { user } = useAuth();

	return (
		<header className="w-full bg-white shadow-sm px-6 py-3 flex items-center justify-between gap-4">
			<Link href="/feed">
				<span className="text-mova-red font-bold text-xl whitespace-nowrap">
					MoVa Sobral
				</span>
			</Link>

			<div className="flex-1 max-w-xl hidden sm:block">
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
				<Link href="/configuracoes">
					{user?.avatarUrl ? (
						<img
							src={user.avatarUrl}
							alt="Foto de perfil"
							className="h-8 w-8 rounded-full object-cover border border-gray-200 hover:ring-2 hover:ring-[#b91c1c] transition-all cursor-pointer"
						/>
					) : (
						<div className="h-8 w-8 rounded-full bg-gray-200 flex items-center justify-center hover:ring-2 hover:ring-[#b91c1c] transition-all cursor-pointer">
							<span className="text-sm font-bold text-gray-500">
								{user?.name?.charAt(0)?.toUpperCase() || "?"}
							</span>
						</div>
					)}
				</Link>
			</div>
		</header>
	);
}
