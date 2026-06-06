interface ButtonProps {
	label: string;
	icon?: string;
	type?: "button" | "submit" | "reset";
	onClick?: () => void;
	className?: string;
}

export default function Button({
	label,
	icon,
	type = "button",
	onClick,
	className = "w-full",
}: ButtonProps) {
	return (
		<button
			type={type}
			onClick={onClick}
			className={`py-3 px-4 bg-primary text-on-primary rounded-lg font-sans font-semibold text-sm hover:bg-primary-container transition-colors shadow-sm active:scale-[0.98] duration-150 flex items-center justify-center gap-2 cursor-pointer ${className}`}
		>
			{label}
			{icon && (
				<span className="material-symbols-outlined" style={{ fontSize: 18 }}>
					{icon}
				</span>
			)}
		</button>
	);
}
