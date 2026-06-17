"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";

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

interface SidebarProps {
	isAdmin?: boolean;
}

export default function Sidebar({ isAdmin = false }: SidebarProps) {
	const pathname = usePathname();
	const navItems = isAdmin ? adminNavItems : userNavItems;

	return (
		<aside className="w-56 min-h-screen bg-white flex flex-col justify-between py-6 px-3 border-r border-gray-100">
			<nav className="flex flex-col gap-1">
				{navItems.map((item) => {
					const isActive = pathname === item.href;
					return (
						<Link
							key={item.href}
							href={item.href}
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

				{/* Botão Criar Evento (só admin) */}
				{isAdmin && (
					<Link
						href="/createEvent"
						className="mt-4 w-full py-3 px-4 bg-mova-red text-white rounded-lg font-semibold text-sm flex items-center justify-center gap-2 hover:bg-mova-red-dark transition-colors"
					>
						Criar Evento
					</Link>
				)}
			</nav>

			<nav className="flex flex-col gap-1">
				{bottomNavItems.map((item) => {
					const isActive = pathname === item.href;
					return (
						<Link
							key={item.href}
							href={item.href}
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
	);
}
