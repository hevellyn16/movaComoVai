import Header from "@/components/layout/header";
import Sidebar from "@/components/layout/sidebar";


export default function MainLayout({
	children,
}: {
	children: React.ReactNode;
}) {
	return (
		<div className="flex flex-col min-h-screen">
			<Header />
			<div className="flex flex-1">
				<Sidebar isAdmin />
				{children}
			</div>
		</div>
	);
}
