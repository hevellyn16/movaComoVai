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
			<div className="flex flex-1 pb-16 md:pb-0 relative">
				<Sidebar />
				<div className="flex-1 w-full max-w-full overflow-x-hidden">
					{children}
				</div>
			</div>
		</div>
	);
}
