"use client";

import { useState } from "react";
import AnswerItem from "@/app/respostas/answerItem";
import { useComments } from "@/hooks/useComments";
import { useAuth } from "@/hooks/useAuth";
import { Answer, Comment } from "@/types/comment.types";

function timeAgo(dateString: string) {
  const diff = Date.now() - new Date(dateString).getTime();
  const mins = Math.floor(diff / 60000);
  if (mins < 60) return `${mins}min`;
  const hours = Math.floor(mins / 60);
  if (hours < 24) return `${hours}h`;
  return `${Math.floor(hours / 24)}d`;
}

export default function CommentItem({ comment }: { comment: Comment }) {
  const { toggleCommentLike, fetchAnswersByCommentId, createAnswer } = useComments();
  const { user } = useAuth();

  const [isLiked, setIsLiked] = useState(comment.isLiked ?? false);
  const [likesCount, setLikesCount] = useState(comment.likesCount ?? 0);
  const [showAnswers, setShowAnswers] = useState(false);
  const [showReplyForm, setShowReplyForm] = useState(false);
  const [replyText, setReplyText] = useState("");
  const [answers, setAnswers] = useState<Answer[]>(comment.answers ?? []);
  const [hasLoadedAnswers, setHasLoadedAnswers] = useState(false);

  async function handleLike() {
    // 1. Atualização Otimista (Interface responde na hora)
    setIsLiked((prev) => !prev);
    setLikesCount((prev) => (isLiked ? prev - 1 : prev + 1));

    try {
      // 2. Chama a API
      await toggleCommentLike(comment.id, isLiked);
    } catch (error) {
      // 3. Se der erro, desfaz a ação silenciosamente
      setIsLiked((prev) => !prev);
      setLikesCount((prev) => (isLiked ? prev + 1 : prev - 1));
      console.error("Erro ao curtir o comentário:", error);
    }
  }

  async function handleReply() {
    if (!replyText.trim()) return;

    try {
      const responseDto = await createAnswer(comment.id, replyText.trim());
      
      const newAnswer: Answer = {
        id: responseDto.id,
        commentId: comment.id,
        userId: responseDto.userId,
        answer: responseDto.answer,
        createdAt: responseDto.createdAt,
        user: user ? {
          id: user.id,
          name: user.name,
          email: user.email,
          userType: user.userType,
          avatarUrl: user.avatarUrl,
          createdAt: new Date().toISOString(),
        } : undefined,
      };

      setAnswers((prev) => [...prev, newAnswer]);
      setReplyText("");
      setShowReplyForm(false);
      setShowAnswers(true);
      setHasLoadedAnswers(true); // se criamos, assumimos que vamos mostrar a lista atualizada
    } catch (error) {
      console.error("Erro ao enviar resposta:", error);
      alert("Falha ao enviar resposta.");
    }
  }

  async function handleToggleAnswers() {
    if (!showAnswers && !hasLoadedAnswers) {
      try {
        const fetchedAnswers = await fetchAnswersByCommentId(comment.id);
        setAnswers(fetchedAnswers);
        setHasLoadedAnswers(true);
      } catch (error) {
        console.error("Erro ao buscar respostas:", error);
      }
    }
    setShowAnswers(!showAnswers);
  }

  return (
    <div className="py-4 border-b border-gray-100 last:border-0">
      <div className="flex gap-3">
        <div className="w-8 h-8 rounded-full bg-gray-200 flex items-center justify-center shrink-0 overflow-hidden">
          {comment.userAvatarUrl ? (
            <img src={comment.userAvatarUrl} alt="Avatar" className="w-full h-full object-cover" />
          ) : (
            <span className="text-xs font-bold text-gray-500">
              {(comment.userName || comment.user?.name || "?").charAt(0).toUpperCase()}
            </span>
          )}
        </div>

        <div className="flex-1 min-w-0">
          {/* Cabeçalho */}
          <div className="flex items-baseline gap-2">
            <span className="text-sm font-bold text-gray-900">
              {comment.userName || comment.user?.name || "Usuário"}
            </span>
            <span className="text-xs text-gray-400">
              {timeAgo(comment.createdAt)}
            </span>
          </div>

          {/* Texto */}
          <p className="text-sm text-gray-600 mt-1 leading-relaxed">
            {comment.comment}
          </p>

          {/* Ações */}
          <div className="flex items-center gap-4 mt-2">
            {/* Curtir */}
            <button
              onClick={handleLike}
              className="flex items-center gap-1 text-xs font-semibold cursor-pointer text-gray-400 hover:text-[#b91c1c] transition-colors"
              aria-label={
                isLiked ? "Descurtir comentário" : "Curtir comentário"
              }
            >
              <span
                className={`material-symbols-outlined transition-colors ${isLiked ? "text-[#b91c1c]" : ""}`}
                style={{ fontSize: 15 }}
              >
                {isLiked ? "favorite" : "favorite_border"}
              </span>
              {likesCount > 0 && <span>{likesCount}</span>}
            </button>

            {/* Responder */}
            <button
              onClick={() => {
                setShowReplyForm((prev) => !prev);
                if (!showAnswers) {
                   handleToggleAnswers();
                }
              }}
              className="text-xs font-semibold cursor-pointer text-gray-400 hover:text-gray-900 transition-colors"
            >
              Responder
            </button>

            {/* Mostrar/ocultar respostas */}
            <button
              onClick={handleToggleAnswers}
              className="text-xs font-semibold cursor-pointer text-[#b91c1c] hover:underline"
            >
              {showAnswers
                ? "Ocultar respostas"
                : (answers.length > 0 ? `${answers.length} ${answers.length === 1 ? "resposta" : "respostas"}` : "Ver respostas")}
            </button>
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
                className="flex-1 text-sm bg-gray-50 border cursor-pointer border-gray-200 rounded-full px-4 py-1.5 outline-none focus:border-[#b91c1c] transition-colors"
                autoFocus
              />
              <button
                onClick={handleReply}
                disabled={!replyText.trim()}
                className="text-xs font-bold cursor-pointer text-white bg-[#b91c1c] px-4 py-1.5 rounded-full disabled:opacity-40 hover:bg-[#991b1b] transition-colors"
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
