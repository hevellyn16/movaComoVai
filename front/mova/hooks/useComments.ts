import { commentService } from "@/services/comments.service";
import { CommentResponseDTO, CommentCreateDTO, Comment } from "@/types/comment.types";
import { useState, useCallback } from "react";
import { useAuth } from "./useAuth";

const mapDtoToComment = (dto: CommentResponseDTO, currentUserId?: string): Comment => {
    return {
        id: dto.id,
        userId: dto.userId,
        eventId: dto.eventId,
        comment: dto.content,
        createdAt: dto.createdAt,
        updatedAt: dto.updatedAt,
        likesCount: 0,
        isLiked: false, 
        answers: [],
    };
};

export const useComments = () => {
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const { user } = useAuth();

    const handleRequest = async <T>(request: () => Promise<T>): Promise<T> => {
        setIsLoading(true);
        setError(null);
        try {
            return await request();
        } catch (err: any) {
            const errorMessage = err.response?.data?.message || "Ocorreu um erro ao processar o comentário.";
            setError(errorMessage);
            throw err;
        } finally {
            setIsLoading(false);
        }
    };


    const fetchCommentsByEventId = useCallback(async (eventId: string, page = 0, size = 20): Promise<{ content: Comment[]; totalPages: number }> => {
        return handleRequest(async () => {
            const data = await commentService.findByEventId(eventId, page, size);
            return {
                content: data.content.map(dto => mapDtoToComment(dto, user?.id)),
                totalPages: data.totalPages
            };
        });
    }, [user?.id]);

    const fetchCommentById = async (id: string): Promise<Comment> => {
        return handleRequest(async () => {
            const dto = await commentService.findById(id);
            return mapDtoToComment(dto, user?.id);
        });
    };

    const createComment = async (eventId: string, data: CommentCreateDTO): Promise<Comment> => {
        return handleRequest(async () => {
            const newDto = await commentService.create(eventId, data);
            return mapDtoToComment(newDto, user?.id);
        });
    };

    const updateComment = async (commentId: string, data: CommentCreateDTO): Promise<Comment> => {
        return handleRequest(async () => {
            const updatedDto = await commentService.update(commentId, data);
            return mapDtoToComment(updatedDto, user?.id);
        });
    };

    const deleteComment = async (commentId: string): Promise<void> => {
        return handleRequest(() => commentService.delete(commentId));
    };


    const toggleCommentLike = async (commentId: string, currentlyLiked: boolean): Promise<void> => {
        return handleRequest(async () => {
            if (currentlyLiked) {
                await commentService.unlikeComment(commentId);
            } else {
                await commentService.likeComment(commentId);
            }
        });
    };

    return {
        isLoading,
        error,
        clearError: () => setError(null),
        fetchCommentsByEventId,
        fetchCommentById,
        createComment,
        updateComment,
        deleteComment,
        toggleCommentLike,
    };
};