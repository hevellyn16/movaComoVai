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
	const allItemsMobile = [...navItems, ...bottomNavItems];

	return (
		<>
			{/* Mobile Bottom Navigation */}
			<nav className="md:hidden fixed bottom-0 left-0 w-full bg-white border-t border-gray-200 flex items-center justify-around py-1.5 px-2 z-50 pb-[max(0.375rem,env(safe-area-inset-bottom))] shadow-[0_-4px_6px_-1px_rgba(0,0,0,0.05)]">
				{allItemsMobile.map((item) => {
					const isActive = pathname === item.href;
					return (
						<Link
							key={item.href}
							href={item.href}
							className={`flex flex-col items-center justify-center w-[72px] p-1.5 rounded-xl transition-colors ${
								isActive ? "text-[#b91c1c]" : "text-gray-400 hover:text-gray-900"
							}`}
						>
							<div className={`flex items-center justify-center w-12 h-8 rounded-full mb-1 transition-colors ${isActive ? "bg-red-50" : "bg-transparent"}`}>
								<span className="material-symbols-outlined" style={{ fontSize: 24 }}>
									{item.icon}
								</span>
							</div>
							<span className={`text-[10px] font-bold truncate w-full text-center transition-colors ${isActive ? "text-[#b91c1c]" : "text-gray-500"}`}>
								{item.label}
							</span>
						</Link>
					);
				})}
			</nav>

			{/* Sidebar Desktop */}
			<aside
				className="hidden md:flex sticky top-0 left-0 z-10 w-56 h-[calc(100vh-64px)] bg-white flex-col justify-between py-6 px-3 border-r border-gray-100 overflow-y-auto"
			>
				<nav className="flex flex-col gap-1">
					{navItems.map((item) => {
						const isActive = pathname === item.href;
						return (
							<Link
								key={item.href}
								href={item.href}
								className={`flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-bold transition-colors ${
									isActive
										? "bg-red-50 text-[#b91c1c]"
										: "text-gray-600 hover:bg-gray-100"
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
								className={`flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-bold transition-colors ${
									isActive
										? "bg-red-50 text-[#b91c1c]"
										: "text-gray-600 hover:bg-gray-100"
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
