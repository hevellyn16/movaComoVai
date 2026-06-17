"use client";

import { useState } from "react";
import { Comment, Answer } from "@/types/comment.types";
import AnswerItem from "@/app/respostas/answerItem";

function timeAgo(dateString: string) {
	const diff = Date.now() - new Date(dateString).getTime();
	const mins = Math.floor(diff / 60000);
	if (mins < 60) return `${mins}min`;
	const hours = Math.floor(mins / 60);
	if (hours < 24) return `${hours}h`;
	return `${Math.floor(hours / 24)}d`;
}

export default function CommentItem({ comment }: { comment: Comment }) {
	const [isLiked, setIsLiked] = useState(comment.isLiked ?? false);
	const [likesCount, setLikesCount] = useState(comment.likesCount ?? 0);
	const [showAnswers, setShowAnswers] = useState(false);
	const [showReplyForm, setShowReplyForm] = useState(false);
	const [replyText, setReplyText] = useState("");
	const [answers, setAnswers] = useState<Answer[]>(comment.answers ?? []);

	function handleLike() {
		setIsLiked((prev) => !prev);
		setLikesCount((prev) => (isLiked ? prev - 1 : prev + 1));
		// TODO: POST /comments/:id/like
	}

	function handleReply() {
		if (!replyText.trim()) return;

		// Otimista: adiciona localmente antes da API responder
		const newAnswer: Answer = {
			id: `a-local-${Date.now()}`,
			commentId: comment.id,
			userId: "current-user",
			answer: replyText.trim(),
			createdAt: new Date().toISOString(),
			user: {
				id: "current-user",
				name: "Você",
				email: "",
				userType: "COMMON",
				createdAt: new Date().toISOString(),
			},
		};

		setAnswers((prev) => [...prev, newAnswer]);
		setReplyText("");
		setShowReplyForm(false);
		setShowAnswers(true);
		// TODO: POST /answers { commentId, answer }
	}

	return (
		<div className="py-4 border-b border-gray-100 last:border-0">
			<div className="flex gap-3">
				{/* Avatar */}
				<div className="w-8 h-8 rounded-full bg-gray-200 flex items-center justify-center shrink-0">
					<span className="text-xs font-bold text-gray-500">
						{comment.user?.name?.charAt(0).toUpperCase() ?? "?"}
					</span>
				</div>

				<div className="flex-1 min-w-0">
					{/* Cabeçalho */}
					<div className="flex items-baseline gap-2">
						<span className="text-sm font-bold text-mova-dark">
							{comment.user?.name ?? "Usuário"}
						</span>
						<span className="text-xs text-gray-400">{timeAgo(comment.createdAt)}</span>
					</div>

					{/* Texto */}
					<p className="text-sm text-gray-600 mt-1 leading-relaxed">
						{comment.comment}
					</p>

					{/* Ações */}
					<div className="flex items-center gap-4 mt-2">
						{/* Curtir — comment_likes */}
						<button
							onClick={handleLike}
							className="flex items-center gap-1 text-xs font-semibold cursor-pointer text-gray-400 hover:text-mova-red transition-colors"
							aria-label={isLiked ? "Descurtir comentário" : "Curtir comentário"}
						>
							<span
								className={`material-symbols-outlined ${isLiked ? "text-mova-red" : ""}`}
								style={{ fontSize: 15 }}
							>
								{isLiked ? "favorite" : "favorite_border"}
							</span>
							{likesCount > 0 && <span>{likesCount}</span>}
						</button>

						{/* Responder — answers */}
						<button
							onClick={() => {
								setShowReplyForm((prev) => !prev);
								if (!showAnswers && answers.length > 0) setShowAnswers(true);
							}}
							className="text-xs font-semibold cursor-pointer text-gray-400 hover:text-mova-dark transition-colors"
						>
							Responder
						</button>

						{/* Mostrar/ocultar respostas */}
						{answers.length > 0 && (
							<button
								onClick={() => setShowAnswers((prev) => !prev)}
								className="text-xs font-semibold cursor-pointer text-mova-red hover:underline"
							>
								{showAnswers
									? "Ocultar respostas"
									: `${answers.length} ${answers.length === 1 ? "resposta" : "respostas"}`}
							</button>
						)}
					</div>

					{/* Formulário de resposta */}
					{showReplyForm && (
						<div className="mt-3 flex gap-2">
							<input
								type="text"
								value={replyText}
								onChange={(e) => setReplyText(e.target.value)}
								onKeyDown={(e) => e.key === "Enter" && handleReply()}
								placeholder="Escreva sua resposta..."
								className="flex-1 text-sm bg-gray-50 border cursor-pointer border-gray-200 rounded-full px-4 py-1.5 outline-none focus:border-mova-red transition-colors"
								autoFocus
							/>
							<button
								onClick={handleReply}
								disabled={!replyText.trim()}
								className="text-xs font-bold cursor-pointer text-white bg-mova-red px-4 py-1.5 rounded-full disabled:opacity-40 hover:bg-red-700 transition-colors"
							>
								Enviar
							</button>
						</div>
					)}

					{/* Lista de respostas */}
					{showAnswers && answers.length > 0 && (
						<div className="mt-2 pl-4 border-l-2 border-gray-100 space-y-1">
							{answers.map((answer) => (
								<AnswerItem key={answer.id} answer={answer} />
							))}
						</div>
					)}
				</div>
			</div>
		</div>
	);
}