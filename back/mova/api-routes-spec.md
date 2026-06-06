# Especificação de Rotas da API

Base URL sugerida: `http://localhost:8080`

Observação: as rotas abaixo foram inferidas a partir das migrations do banco, dos controllers atuais e dos requisitos funcionais informados. As rotas marcadas como **Implementada** já existem no backend atual. As rotas marcadas como **Necessária** representam a cobertura funcional esperada para os requisitos e para as tabelas criadas nas migrations.

Legenda de acesso:

- `Público`: sem autenticação.
- `Usuário`: requer JWT de usuário comum autenticado.
- `Admin`: requer JWT com role ADMIN.
- `Ambos`: acessível para usuário comum e admin autenticados.

## 1. Autenticação e Usuários

### Rotas implementadas

| Método | Rota                                  |        Acesso | Finalidade                                       |
| ------ | ------------------------------------- | ------------: | ------------------------------------------------ |
| POST   | `/auth/login`                         |       Público | Autenticar usuário com e-mail/senha e gerar JWT. |
| POST   | `/users`                              |       Público | Cadastrar novo usuário.                          |
| POST   | `/users/forgot-password`              |       Público | Iniciar fluxo de recuperação de senha.           |
| PUT    | `/users/reset-password?token={token}` |       Público | Redefinir senha com token.                       |
| PUT    | `/users`                              | Usuário/Admin | Atualizar dados do usuário autenticado.          |
| DELETE | `/users`                              | Usuário/Admin | Excluir usuário autenticado.                     |
| GET    | `/users/{id}`                         |         Admin | Buscar usuário por ID.                           |
| GET    | `/users/email/{email}`                |         Admin | Buscar usuário por e-mail.                       |
| GET    | `/users`                              |         Admin | Listar usuários com paginação.                   |
| PUT    | `/users/{id}/admin`                   |         Admin | Promover usuário para admin.                     |

### Rotas necessárias para RF01 e RF02

| Método | Rota                            |        Acesso | Finalidade                                          |
| ------ | ------------------------------- | ------------: | --------------------------------------------------- |
| POST   | `/auth/sso/google`              |       Público | Login/cadastro via Google SSO.                      |
| POST   | `/auth/sso/apple`               |       Público | Login/cadastro via Apple SSO.                       |
| GET    | `/users/me`                     | Usuário/Admin | Retornar perfil do usuário autenticado.             |
| PATCH  | `/users/me/onboarding`          | Usuário/Admin | Salvar preferências iniciais e concluir onboarding. |
| GET    | `/users/me/preferences`         | Usuário/Admin | Consultar preferências cadastradas do usuário.      |
| PUT    | `/users/me/preferences`         | Usuário/Admin | Atualizar preferências/tags do perfil.              |
| DELETE | `/users/me/preferences/{tagId}` | Usuário/Admin | Remover uma preferência de tag.                     |

## 2. Tags

Tabelas relacionadas na migration: `tags`, `users_tags`, `event_tags`.

### Rotas necessárias

| Método | Rota                             |        Acesso | Finalidade                                                        |
| ------ | -------------------------------- | ------------: | ----------------------------------------------------------------- |
| GET    | `/tags`                          | Usuário/Admin | Listar tags disponíveis para onboarding, filtros e administração. |
| GET    | `/tags/{id}`                     | Usuário/Admin | Consultar tag por ID.                                             |
| POST   | `/tags`                          |         Admin | Criar nova tag.                                                   |
| PUT    | `/tags/{id}`                     |         Admin | Atualizar tag.                                                    |
| DELETE | `/tags/{id}`                     |         Admin | Excluir tag.                                                      |
| POST   | `/users/me/tags`                 | Usuário/Admin | Associar tags de interesse ao usuário.                            |
| DELETE | `/users/me/tags/{tagId}`         | Usuário/Admin | Desassociar uma tag de interesse.                                 |
| POST   | `/events/{eventId}/tags`         |         Admin | Associar tags a um evento.                                        |
| DELETE | `/events/{eventId}/tags/{tagId}` |         Admin | Remover tag de um evento.                                         |

## 3. Locais / Venues

Tabela relacionada na migration: `venues`.

### Rotas necessárias

| Método | Rota                                       |        Acesso | Finalidade                             |
| ------ | ------------------------------------------ | ------------: | -------------------------------------- |
| GET    | `/venues`                                  | Usuário/Admin | Listar locais com paginação e filtros. |
| GET    | `/venues/{id}`                             | Usuário/Admin | Consultar local por ID.                |
| POST   | `/venues`                                  |         Admin | Cadastrar local.                       |
| PUT    | `/venues/{id}`                             |         Admin | Atualizar local.                       |
| DELETE | `/venues/{id}`                             |         Admin | Excluir local.                         |
| GET    | `/venues/search?neighborhood=&city=&name=` | Usuário/Admin | Busca textual/filtros de locais.       |

## 4. Eventos

Tabelas relacionadas nas migrations: `events`, `event_tags`, `event_pictures`.

### Rotas necessárias para RF04 e RF05

| Método | Rota                                                                    |        Acesso | Finalidade                                         |
| ------ | ----------------------------------------------------------------------- | ------------: | -------------------------------------------------- |
| GET    | `/events`                                                               | Usuário/Admin | Listar eventos com paginação, ordenação e filtros. |
| GET    | `/events/{id}`                                                          | Usuário/Admin | Consultar evento por ID.                           |
| POST   | `/events`                                                               |         Admin | Cadastrar evento com todos os campos obrigatórios. |
| PUT    | `/events/{id}`                                                          |         Admin | Atualizar evento.                                  |
| DELETE | `/events/{id}`                                                          |         Admin | Excluir evento.                                    |
| GET    | `/events/search?q=&dateFrom=&dateTo=&priceMin=&priceMax=&neighborhood=` | Usuário/Admin | Busca textual livre e filtros avançados.           |
| GET    | `/events/today`                                                         | Usuário/Admin | Listar eventos do dia.                             |
| GET    | `/events/upcoming`                                                      | Usuário/Admin | Listar eventos futuros.                            |
| POST   | `/events/{eventId}/pictures`                                            |         Admin | Adicionar imagens ao evento.                       |
| DELETE | `/events/{eventId}/pictures/{pictureId}`                                |         Admin | Remover imagem do evento.                          |

### Campos esperados no cadastro de evento

Baseado na migration, o payload de criação deve contemplar no mínimo:

- `eventName`
- `description`
- `contentRating`
- `price`
- `startsAt`
- `endsAt`
- `venueId`
- `tagIds`
- `pictures`

Se a regra de negócio exigir infraestrutura do local no evento, isso deve ser refletido no `venue` ou em um DTO agregado.

## 5. Preferências, Contexto e Onboarding

Relacionadas aos RF02 e RF03.

### Rotas necessárias

| Método | Rota                            |        Acesso | Finalidade                                                          |
| ------ | ------------------------------- | ------------: | ------------------------------------------------------------------- |
| POST   | `/users/me/context`             | Usuário/Admin | Registrar contexto atual da saída: sozinho, casal, grupo, crianças. |
| GET    | `/users/me/context`             | Usuário/Admin | Consultar contexto atual salvo.                                     |
| PUT    | `/users/me/context`             | Usuário/Admin | Atualizar contexto da sessão.                                       |
| POST   | `/users/me/onboarding/complete` | Usuário/Admin | Marcar onboarding como concluído.                                   |

## 6. Recomendação e Planos

Cobertura dos RF06, RF07 e RF08.

### Rotas necessárias

| Método | Rota                          |        Acesso | Finalidade                                                         |
| ------ | ----------------------------- | ------------: | ------------------------------------------------------------------ |
| GET    | `/recommendations/feed`       | Usuário/Admin | Retornar feed ordenado por relevância.                             |
| GET    | `/recommendations/plans`      | Usuário/Admin | Listar planos de atividades sugeridos.                             |
| GET    | `/recommendations/plans/{id}` | Usuário/Admin | Consultar um plano sugerido.                                       |
| POST   | `/recommendations/rebuild`    | Usuário/Admin | Reprocessar recomendações do usuário atual.                        |
| GET    | `/recommendations/explore`    | Usuário/Admin | Fallback de eventos mais populares quando não houver preferências. |

### Regras esperadas na resposta

- Ordenação por percentual de match.
- Fallback para itens populares quando não houver preferências.
- Retorno paginado para feed e planos.
- Possibilidade de incluir distância, bairro e tags em comum.

## 7. Interação com Eventos

Cobertura do RF09.

### Rotas necessárias

| Método | Rota                            |        Acesso | Finalidade                              |
| ------ | ------------------------------- | ------------: | --------------------------------------- |
| POST   | `/events/{eventId}/favorites`   | Usuário/Admin | Adicionar evento aos favoritos.         |
| DELETE | `/events/{eventId}/favorites`   | Usuário/Admin | Remover evento dos favoritos.           |
| GET    | `/users/me/favorites`           | Usuário/Admin | Listar favoritos do usuário.            |
| POST   | `/events/{eventId}/attendances` | Usuário/Admin | Confirmar presença em um evento.        |
| DELETE | `/events/{eventId}/attendances` | Usuário/Admin | Cancelar presença confirmada.           |
| GET    | `/users/me/attendances`         | Usuário/Admin | Listar eventos com presença confirmada. |
| POST   | `/events/{eventId}/likes`       | Usuário/Admin | Curtir evento.                          |
| DELETE | `/events/{eventId}/likes`       | Usuário/Admin | Remover like de evento.                 |

## 8. Comentários e Respostas

Cobertura das tabelas `comments`, `comment_likes`, `comment_pictures`, `answers`.

### Rotas necessárias

| Método | Rota                                         |        Acesso | Finalidade                      |
| ------ | -------------------------------------------- | ------------: | ------------------------------- |
| GET    | `/events/{eventId}/comments`                 | Usuário/Admin | Listar comentários do evento.   |
| POST   | `/events/{eventId}/comments`                 | Usuário/Admin | Criar comentário.               |
| PUT    | `/comments/{commentId}`                      | Usuário/Admin | Editar comentário.              |
| DELETE | `/comments/{commentId}`                      | Usuário/Admin | Excluir comentário.             |
| POST   | `/comments/{commentId}/likes`                | Usuário/Admin | Curtir comentário.              |
| DELETE | `/comments/{commentId}/likes`                | Usuário/Admin | Descurtir comentário.           |
| POST   | `/comments/{commentId}/pictures`             | Usuário/Admin | Adicionar imagem ao comentário. |
| DELETE | `/comments/{commentId}/pictures/{pictureId}` | Usuário/Admin | Remover imagem do comentário.   |
| POST   | `/comments/{commentId}/answers`              | Usuário/Admin | Responder comentário.           |
| PUT    | `/answers/{answerId}`                        | Usuário/Admin | Editar resposta.                |
| DELETE | `/answers/{answerId}`                        | Usuário/Admin | Excluir resposta.               |

## 9. Busca e Filtros

Cobertura do RF10.

### Rotas necessárias

| Método | Rota                                                                  |        Acesso | Finalidade                      |
| ------ | --------------------------------------------------------------------- | ------------: | ------------------------------- |
| GET    | `/search/events?q=`                                                   | Usuário/Admin | Busca textual livre em eventos. |
| GET    | `/search/venues?q=`                                                   | Usuário/Admin | Busca textual livre em locais.  |
| GET    | `/search/feed?q=&dateFrom=&dateTo=&priceMin=&priceMax=&neighborhood=` | Usuário/Admin | Busca avançada combinada.       |

## 10. Integrações Externas

Cobertura do RNF04.

### Rotas necessárias

| Método | Rota                               |        Acesso | Finalidade                              |
| ------ | ---------------------------------- | ------------: | --------------------------------------- |
| GET    | `/maps/geocode?address=`           | Usuário/Admin | Converter endereço em coordenadas.      |
| GET    | `/maps/route?origin=&destination=` | Usuário/Admin | Calcular rota para um plano sugerido.   |
| GET    | `/venues/{id}/location`            | Usuário/Admin | Retornar dados de localização do local. |

## 11. Resumo de Entidades do Banco e Cobertura de Rotas

### Já cobertas hoje

- `users`
- autenticação JWT

### Cobertura ainda necessária para os requisitos e migrations

- `tags`
- `users_tags`
- `venues`
- `events`
- `event_tags`
- `event_pictures`
- `event_likes`
- `favorites`
- `comments`
- `comment_likes`
- `comment_pictures`
- `answers`
- recomendação e planos personalizados

## 12. Observações de arquitetura

- As rotas de recomendação devem ficar em controllers próprios, separadas da lógica de negócio.
- Os endpoints administrativos devem exigir role `ADMIN`.
- As rotas de usuário autenticado devem usar JWT.
- As rotas de busca e feed devem aceitar paginação e ordenação.
- As rotas de eventos e locais devem refletir a modelagem das migrations, especialmente `venue`, `tags`, `favorites`, `likes`, `comments` e `answers`.

## 13. Mapeamento rápido RF -> Rotas

- RF01: `/auth/login`, `/auth/sso/google`, `/auth/sso/apple`, `/users`
- RF02: `/users/me/onboarding`, `/users/me/preferences`, `/users/me/tags`
- RF03: `/users/me/context`
- RF04: `/events`, `/venues`
- RF05: `/events`, `/events/{id}`, `/events/{id}/tags`, `/events/{eventId}/pictures`
- RF06: `/recommendations/feed`, `/recommendations/rebuild`
- RF07: `/recommendations/plans`, `/recommendations/plans/{id}`
- RF08: `/recommendations/feed`
- RF09: `/events/{eventId}/favorites`, `/events/{eventId}/attendances`, `/users/me/favorites`
- RF10: `/search/events`, `/search/feed`, `/events/search`, `/venues/search`
