"use client";

import { useState } from "react";
import { Comment } from "@/types/comment.types";
import CommentItem from "./commentItem";

interface CommentSectionProps {
	eventId: string;
	initialComments: Comment[];
}

export default function CommentSection({
	eventId,
	initialComments,
}: CommentSectionProps) {
	const [comments, setComments] = useState<Comment[]>(initialComments);
	const [newComment, setNewComment] = useState("");

	function handleSubmit() {
		if (!newComment.trim()) return;

		// Atualização otimista enquanto API não responde
		const created: Comment = {
			id: `c-local-${Date.now()}`,
			userId: "current-user",
			eventId,
			comment: newComment.trim(),
			createdAt: new Date().toISOString(),
			likesCount: 0,
			isLiked: false,
			answers: [],
			user: {
				id: "current-user",
				name: "Você",
				email: "",
				userType: "COMMON",
				createdAt: new Date().toISOString(),
			},
		};

		setComments((prev) => [created, ...prev]);
		setNewComment("");
		// TODO: POST /comments { eventId, comment }
	}

	return (
		<div className="mt-8 rounded-3xl border border-gray-200 bg-white p-6 shadow-sm">
			<h3 className="text-base font-bold text-mova-dark mb-5">
				Comentários
				{comments.length > 0 && (
					<span className="ml-2 text-sm font-normal text-gray-400">
						({comments.length})
					</span>
				)}
			</h3>

			{/* Novo comentário */}
			<div className="flex gap-3 mb-6">
				<div className="w-8 h-8 rounded-full bg-gray-200 flex items-center justify-center shrink-0">
					<span className="material-symbols-outlined text-gray-400" style={{ fontSize: 16 }}>
						person
					</span>
				</div>
				<div className="flex-1 flex gap-2">
					<input
						type="text"
						value={newComment}
						onChange={(e) => setNewComment(e.target.value)}
						onKeyDown={(e) => e.key === "Enter" && handleSubmit()}
						placeholder="Deixe um comentário..."
						className="flex-1 text-sm bg-gray-50 border cursor-pointer border-gray-200 rounded-full px-4 py-2 outline-none focus:border-mova-red transition-colors"
					/>
					<button
						onClick={handleSubmit}
						disabled={!newComment.trim()}
						className="text-xs font-bold cursor-pointer text-white bg-mova-red px-4 py-2 rounded-full disabled:opacity-40 hover:bg-red-700 transition-colors"
					>
						Publicar
					</button>
				</div>
			</div>

			{/* Lista */}
			{comments.length === 0 ? (
				<div className="py-10 text-center">
					<span
						className="material-symbols-outlined text-gray-200 block mb-2"
						style={{ fontSize: 40 }}
					>
						chat_bubble_outline
					</span>
					<p className="text-sm text-gray-400">
						Nenhum comentário ainda. Seja o primeiro!
					</p>
				</div>
			) : (
				<div>
					{comments.map((comment) => (
						<CommentItem key={comment.id} comment={comment} />
					))}
				</div>
			)}
		</div>
	);
}