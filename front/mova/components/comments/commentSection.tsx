"use client";

import { useState, useEffect } from "react";
import { useComments } from "@/hooks/useComments";
import { Comment } from "@/types/comment.types";
import CommentItem from "./commentItem";

interface CommentSectionProps {
    eventId: string;
    // Mantemos a prop opcional para não quebrar o componente pai (EventDetail)
    initialComments?: Comment[];
}

export default function CommentSection({
    eventId,
}: CommentSectionProps) {
    const { fetchCommentsByEventId, createComment, isLoading } = useComments();

    const [comments, setComments] = useState<Comment[]>([]);
    const [newComment, setNewComment] = useState("");
    const [isSubmitting, setIsSubmitting] = useState(false);

    // Busca os comentários reais do backend assim que o componente carrega
    useEffect(() => {
        const loadComments = async () => {
            try {
                // Traz a primeira página com até 50 comentários
                const data = await fetchCommentsByEventId(eventId, 0, 50);
                setComments(data.content);
            } catch (error) {
                console.error("Erro ao carregar comentários da API", error);
            }
        };

        loadComments();
    }, [eventId, fetchCommentsByEventId]);

    async function handleSubmit() {
        if (!newComment.trim()) return;

        setIsSubmitting(true);
        try {
            // Chama a API real (o DTO espera 'content')
            const createdComment = await createComment(eventId, { 
                content: newComment.trim() 
            });

            // Adiciona o novo comentário (com ID real do banco) no topo da lista
            setComments((prev) => [createdComment, ...prev]);
            setNewComment("");
        } catch (error) {
            console.error("Falha ao publicar comentário:", error);
            alert("Não foi possível publicar seu comentário. Tente novamente.");
        } finally {
            setIsSubmitting(false);
        }
    }

    return (
        <div className="mt-8 rounded-3xl border border-gray-200 bg-white p-6 shadow-sm">
            <h3 className="text-base font-bold text-gray-900 mb-5">
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
                        disabled={isSubmitting}
                        placeholder="Deixe um comentário..."
                        className="flex-1 text-sm bg-gray-50 border cursor-pointer border-gray-200 rounded-full px-4 py-2 outline-none focus:border-[#b91c1c] transition-colors disabled:opacity-50"
                    />
                    <button
                        onClick={handleSubmit}
                        disabled={!newComment.trim() || isSubmitting}
                        className="text-xs font-bold cursor-pointer text-white bg-[#b91c1c] px-4 py-2 rounded-full disabled:opacity-40 hover:bg-[#991b1b] transition-colors flex items-center gap-1"
                    >
                        {isSubmitting ? "Enviando..." : "Publicar"}
                    </button>
                </div>
            </div>

            {/* Lista e Status de Loading */}
            {isLoading ? (
                <div className="py-10 flex justify-center items-center">
                    <div className="animate-spin rounded-full h-6 w-6 border-t-2 border-b-2 border-[#b91c1c]"></div>
                </div>
            ) : comments.length === 0 ? (
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
                <div className="space-y-1">
                    {comments.map((comment) => (
                        <CommentItem key={comment.id} comment={comment} />
                    ))}
                </div>
            )}
        </div>
    );
}