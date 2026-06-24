"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { useState } from "react";
import { useAuth } from "@/hooks/useAuth";

interface NavItem {
	label: string;
	icon: string;
	href: string;
}

const userNavItems: NavItem[] = [
	{ label: "Feed", icon: "feed", href: "/feed" },
	{ label: "Favoritos", icon: "bookmark", href: "/favoritos" }
];

const adminNavItems: NavItem[] = [
	{ label: "Feed", icon: "feed", href: "/feed" },
	{ label: "Favoritos", icon: "bookmark", href: "/favoritos" },
	{ label: "Gestão", icon: "admin_panel_settings", href: "/gestao" }
];

const bottomNavItems: NavItem[] = [
	{ label: "Configurações", icon: "settings", href: "/configuracoes" },
	{ label: "Ajuda", icon: "help", href: "/ajuda" },
];

export default function Sidebar() {
	const pathname = usePathname();
	const { user } = useAuth();
	
	const isAdmin = user?.userType === "ADMIN";
	const navItems = isAdmin ? adminNavItems : userNavItems;
	const [isOpen, setIsOpen] = useState(false);

	return (
		<>
			{/* Botão hamburger — visível apenas em telas pequenas */}
			<button
				type="button"
				onClick={() => setIsOpen(true)}
				className="md:hidden fixed bottom-4 left-4 z-50 bg-[#b91c1c] text-white p-3 rounded-full shadow-lg hover:bg-[#991b1b] transition-colors cursor-pointer"
				aria-label="Abrir menu"
			>
				<span className="material-symbols-outlined" style={{ fontSize: 24 }}>
					menu
				</span>
			</button>

			{/* Overlay escuro — visível apenas quando sidebar está aberta no mobile */}
			{isOpen && (
				<div
					className="md:hidden fixed inset-0 bg-black/40 z-40 transition-opacity"
					onClick={() => setIsOpen(false)}
				/>
			)}

			{/* Sidebar */}
			<aside
				className={`
					fixed md:sticky top-0 left-0 z-50 md:z-auto
					w-56 h-screen bg-white flex flex-col justify-between py-6 px-3 border-r border-gray-100
					transition-transform duration-300 ease-in-out
					${isOpen ? "translate-x-0" : "-translate-x-full"} md:translate-x-0
					overflow-y-auto
				`}
			>
				{/* Botão fechar — visível apenas no mobile */}
				<div className="md:hidden flex justify-end mb-4">
					<button
						type="button"
						onClick={() => setIsOpen(false)}
						className="p-1 rounded-lg hover:bg-gray-100 transition-colors cursor-pointer"
						aria-label="Fechar menu"
					>
						<span className="material-symbols-outlined text-gray-500" style={{ fontSize: 24 }}>
							close
						</span>
					</button>
				</div>

				<nav className="flex flex-col gap-1">
					{navItems.map((item) => {
						const isActive = pathname === item.href;
						return (
							<Link
								key={item.href}
								href={item.href}
								onClick={() => setIsOpen(false)}
								className={`flex items-center gap-3 px-3 py-2 rounded-lg text-sm font-medium transition-colors
                ${
				isActive
				? "bg-mova-yellow text-mova-dark"
				: "text-mova-dark hover:bg-mova-yellow hover:text-mova-dark"
				}`}
							>
								<span className="material-symbols-outlined" style={{ fontSize: 20 }}>
									{item.icon}
								</span>
								{item.label}
							</Link>
						);
					})}
				</nav>

				<nav className="flex flex-col gap-1">
					{bottomNavItems.map((item) => {
						const isActive = pathname === item.href;
						return (
							<Link
								key={item.href}
								href={item.href}
								onClick={() => setIsOpen(false)}
								className={`flex items-center gap-3 px-3 py-2 rounded-lg text-sm font-medium transition-colors
                ${
				isActive
				? "bg-mova-yellow text-mova-dark"
				: "text-mova-dark hover:bg-mova-yellow hover:text-mova-dark"
				}`}
							>
								<span className="material-symbols-outlined" style={{ fontSize: 20 }}>
									{item.icon}
								</span>
								{item.label}
							</Link>
						);
					})}
				</nav>
			</aside>
		</>
	);
}
