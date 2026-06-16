import { Comment } from "@/types/comment.types";

// Comentários indexados por event_id
export const commentsByEvent: Record<string, Comment[]> = {
	"1": [
		{
			id: "c1",
			userId: "user-10",
			eventId: "1",
			comment: "Fui no ano passado e foi incrível! A estrutura estava ótima e a música então... esse ano promete mais ainda.",
			createdAt: "2026-06-15T14:30:00.000Z",
			likesCount: 8,
			isLiked: false,
			user: {
				id: "user-10",
				name: "Mariana Feitosa",
				email: "mariana@email.com",
				userType: "COMMON",
				createdAt: "2025-01-10T00:00:00.000Z",
			},
			answers: [
				{
					id: "a1",
					commentId: "c1",
					userId: "user-11",
					answer: "Concordo demais! Já garanti meu lugar esse ano.",
					createdAt: "2026-06-15T15:00:00.000Z",
					user: {
						id: "user-11",
						name: "Rafael Mendes",
						email: "rafael@email.com",
						userType: "COMMON",
						createdAt: "2025-03-05T00:00:00.000Z",
					},
				},
			],
		},
		{
			id: "c2",
			userId: "user-12",
			eventId: "1",
			comment: "Algum ponto de ônibus próximo ao Arco do Triunfo para quem vem do Derby?",
			createdAt: "2026-06-16T09:00:00.000Z",
			likesCount: 3,
			isLiked: true,
			user: {
				id: "user-12",
				name: "Luana Braga",
				email: "luana@email.com",
				userType: "COMMON",
				createdAt: "2025-05-20T00:00:00.000Z",
			},
			answers: [
				{
					id: "a2",
					commentId: "c2",
					userId: "user-13",
					answer: "Tem o ponto na Av. Dom José, bem pertinho. Qualquer linha do Centro passa lá.",
					createdAt: "2026-06-16T10:15:00.000Z",
					user: {
						id: "user-13",
						name: "Carlos Sousa",
						email: "carlos@email.com",
						userType: "COMMON",
						createdAt: "2025-02-14T00:00:00.000Z",
					},
				},
			],
		},
	],
	"2": [
		{
			id: "c3",
			userId: "user-14",
			eventId: "2",
			comment: "O Museu Dom José é um cenário lindo pra esse tipo de evento. Vou levar a família!",
			createdAt: "2026-06-14T20:00:00.000Z",
			likesCount: 5,
			isLiked: false,
			user: {
				id: "user-14",
				name: "Patrícia Alves",
				email: "patricia@email.com",
				userType: "COMMON",
				createdAt: "2025-04-01T00:00:00.000Z",
			},
			answers: [],
		},
	],
	"3": [],
	"4": [
		{
			id: "c4",
			userId: "user-15",
			eventId: "4",
			comment: "Quadrilha esse ano vai ter? É o melhor momento do forró de raiz de Sobral!",
			createdAt: "2026-06-10T18:00:00.000Z",
			likesCount: 12,
			isLiked: false,
			user: {
				id: "user-15",
				name: "João Damasceno",
				email: "joao@email.com",
				userType: "COMMON",
				createdAt: "2024-11-30T00:00:00.000Z",
			},
			answers: [
				{
					id: "a3",
					commentId: "c4",
					userId: "user-4",
					answer: "Sim! Já confirmamos três grupos de quadrilha. Vai ser uma festa e tanto.",
					createdAt: "2026-06-10T19:30:00.000Z",
					user: {
						id: "user-4",
						name: "Organizador Mova",
						email: "org@mova.com",
						userType: "ADMIN",
						createdAt: "2024-01-01T00:00:00.000Z",
					},
				},
			],
		},
	],
};