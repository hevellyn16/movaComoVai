import { Answer } from "@/types/comment.types";

function timeAgo(dateString: string) {
	const diff = Date.now() - new Date(dateString).getTime();
	const mins = Math.floor(diff / 60000);
	if (mins < 60) return `${mins}min`;
	const hours = Math.floor(mins / 60);
	if (hours < 24) return `${hours}h`;
	return `${Math.floor(hours / 24)}d`;
}

export default function AnswerItem({ answer }: { answer: Answer }) {
	return (
		<div className="flex gap-3 pt-3">
			{/* Avatar */}
			<div className="w-7 h-7 rounded-full bg-gray-200 flex items-center justify-center shrink-0">
				<span className="text-xs font-bold text-gray-500">
					{answer.user?.name?.charAt(0).toUpperCase() ?? "?"}
				</span>
			</div>

			<div className="flex-1 min-w-0">
				<div className="flex items-baseline gap-2">
					<span className="text-xs font-bold text-mova-dark">
						{answer.user?.name ?? "Usuário"}
					</span>
					{answer.user?.userType === "ADMIN" && (
						<span className="text-[10px] font-bold text-mova-red bg-red-50 px-1.5 py-0.5 rounded-full">
							Organiz.
						</span>
					)}
					<span className="text-[10px] text-gray-400">{timeAgo(answer.createdAt)}</span>
				</div>
				<p className="text-sm text-gray-600 mt-0.5 leading-relaxed">{answer.answer}</p>
			</div>
		</div>
	);
}