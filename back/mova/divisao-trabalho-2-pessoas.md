# Divisão de Trabalho entre 2 Pessoas

Este documento organiza a implementação do sistema em duas frentes de trabalho para reduzir conflito de código e garantir dependência clara entre as entregas.

## 1. Objetivo da Divisão

A ideia é separar o sistema em dois blocos complementares:

- **Pessoa 1** fica responsável pela base estrutural e pelo domínio principal de cadastro e administração.
- **Pessoa 2** fica responsável pela camada de experiência do usuário, recomendação e interação.

A divisão foi pensada para que:

- cada pessoa trabalhe em áreas diferentes do código;
- as dependências sejam sequenciais;
- a entrega de uma frente destrave a outra;
- o risco de conflito de merge seja o menor possível.

## 2. Estratégia de Separação

A divisão fica mais eficiente se cada pessoa assumir grupos de entidades que se relacionam entre si, em vez de dividir só por tela ou camada funcional.

### Pessoa 1 - Plataforma, catálogo e administração

Responsável pela base do sistema e pelos dados mestres:

- users
- auth
- tags
- venues
- events
- event_tags
- event_pictures

### Pessoa 2 - Personalização, interação e inteligência

Responsável pelas features que dependem da base e dão valor ao usuário final:

- users_tags
- onboarding e preferências
- contexto do usuário
- favoritos
- presença
- recomendação
- busca
- planos de atividades
- comentários e respostas

## 3. Divisão por Entidades e Relações

## Pessoa 1 - Users, Auth, Tags, Venues e Eventos Base

### Entidades principais

- `users`
- `tags`
- `venues`
- `events`
- `event_tags`
- `event_pictures`

### Relações que ela controla

- usuário -> tags de interesse
- evento -> venue
- evento -> tags
- evento -> imagens
- usuário -> autenticação e recuperação de senha

### Requisitos cobertos

- RF01: cadastro, login e recuperação de senha.
- RF04: painel administrativo de eventos.
- RF05: cadastro detalhado de eventos.
- Parte estrutural de RF06: catálogo de eventos como base de recomendação.
- RNF05: segurança e JWT na base de autenticação.

### Entregas técnicas

- Entidade e persistência de usuários.
- Entidade e persistência de tags.
- Entidade e persistência de venues.
- Entidade e persistência de events.
- Relacionamento event_tags.
- Relacionamento event_pictures.
- DTOs de login, cadastro, onboarding e preferências.
- Controllers e services de cadastro e administração.
- Regras de autorização e segurança.

### Rotas sob responsabilidade

- `POST /auth/login`
- `POST /auth/sso/google`
- `POST /auth/sso/apple`
- `POST /users`
- `POST /users/forgot-password`
- `PUT /users/reset-password`
- `PUT /users`
- `DELETE /users`
- `GET /users/{id}`
- `GET /users/email/{email}`
- `GET /users`
- `PUT /users/{id}/admin`
- `GET /tags`
- `GET /tags/{id}`
- `POST /tags`
- `PUT /tags/{id}`
- `DELETE /tags/{id}`
- `GET /venues`
- `GET /venues/{id}`
- `POST /venues`
- `PUT /venues/{id}`
- `DELETE /venues/{id}`
- `GET /venues/search?neighborhood=&city=&name=`
- `GET /events`
- `GET /events/{id}`
- `POST /events`
- `PUT /events/{id}`
- `DELETE /events/{id}`
- `GET /events/search?q=&dateFrom=&dateTo=&priceMin=&priceMax=&neighborhood=`
- `GET /events/today`
- `GET /events/upcoming`
- `POST /events/{eventId}/pictures`
- `DELETE /events/{eventId}/pictures/{pictureId}`
- `POST /events/{eventId}/tags`
- `DELETE /events/{eventId}/tags/{tagId}`

### Dependências que ela entrega para a outra pessoa

- IDs de usuário e tags.
- Contrato de eventos, venues e imagens.
- Relacionamentos de eventos e tags já estáveis.
- Estrutura de autenticação já estável.

## Pessoa 2 - Users_tags, Preferências, Interação e Recomendação

### Entidades principais

- `users_tags`
- `event_likes`
- `favorites`
- `attendances`
- `comments`
- `comment_likes`
- `comment_pictures`
- `answers`

### Relações que ela controla

- usuário -> tags de interesse
- usuário -> evento favorito
- usuário -> presença em evento
- evento -> comentários
- comentário -> respostas
- comentário -> likes

### Requisitos cobertos

- RF02: onboarding e preferências.
- RF03: contexto da saída.
- RF06: algoritmo de recomendação.
- RF07: planos de atividades.
- RF08: feed ordenado por relevância.
- RF09: favoritos e presença.
- RF10: busca e filtros avançados.
- RNF01: desempenho da recomendação.
- RNF02: isolamento da regra de negócio.
- RNF03: integridade relacional e ACID.
- RNF04: integração com mapas.

### Entregas técnicas

- Relacionamento users_tags.
- Serviços e controllers de onboarding e preferências.
- Serviços e controllers de recomendação.
- Serviços e controllers de interação.
- Busca, filtros e integração com mapa.

### Rotas sob responsabilidade

- `GET /users/me`
- `PATCH /users/me/onboarding`
- `GET /users/me/preferences`
- `PUT /users/me/preferences`
- `DELETE /users/me/preferences/{tagId}`
- `POST /users/me/tags`
- `DELETE /users/me/tags/{tagId}`
- `POST /users/me/context`
- `GET /users/me/context`
- `PUT /users/me/context`
- `POST /users/me/onboarding/complete`
- `GET /venues`
- `GET /venues/{id}`
- `POST /events/{eventId}/favorites`
- `DELETE /events/{eventId}/favorites`
- `GET /users/me/favorites`
- `POST /events/{eventId}/attendances`
- `DELETE /events/{eventId}/attendances`
- `GET /users/me/attendances`
- `POST /events/{eventId}/likes`
- `DELETE /events/{eventId}/likes`
- `GET /events/{eventId}/comments`
- `POST /events/{eventId}/comments`
- `PUT /comments/{commentId}`
- `DELETE /comments/{commentId}`
- `POST /comments/{commentId}/likes`
- `DELETE /comments/{commentId}/likes`
- `POST /comments/{commentId}/pictures`
- `DELETE /comments/{commentId}/pictures/{pictureId}`
- `POST /comments/{commentId}/answers`
- `PUT /answers/{answerId}`
- `DELETE /answers/{answerId}`
- `GET /recommendations/feed`
- `GET /recommendations/plans`
- `GET /recommendations/plans/{id}`
- `POST /recommendations/rebuild`
- `GET /recommendations/explore`
- `GET /search/events?q=`
- `GET /search/venues?q=`
- `GET /search/feed?q=&dateFrom=&dateTo=&priceMin=&priceMax=&neighborhood=`
- `GET /maps/geocode?address=`
- `GET /maps/route?origin=&destination=`
- `GET /venues/{id}/location`

### Dependências que ela recebe da outra pessoa

- Users, tags e preferências já definidos.
- Contexto do usuário pronto para leitura.
- Contrato de autenticação e autorização.
- Repositórios de usuários e tags para cruzar com eventos.

## 4. Ordem de Implementação Sem Conflito

### Fase 1 - Pessoa 1 primeiro

A Pessoa 1 deve começar pela base de plataforma e catálogo:

1. usuários;
2. autenticação;
3. tags;
4. venues;
5. events;
6. event_tags;
7. event_pictures.

Essa fase entrega o catálogo e a base de administração que a outra pessoa vai consumir.

### Fase 2 - Pessoa 2 depois da base pronta

A Pessoa 2 começa quando já existirem:

- usuários, tags, venues e events persistidos;
- contratos de autenticação definidos;
- relacionamentos principais do catálogo prontos.

Depois disso, ela implementa:

1. users_tags;
2. onboarding e preferências;
3. contexto da saída;
4. favoritos e presença;
5. comentários e respostas;
6. busca avançada;
7. recomendação e planos;
8. integração com mapas.

## 5. Dependências Entre as Pessoas

A dependência foi organizada para ser clara:

- **Pessoa 1 entrega a base.**
- **Pessoa 2 consome essa base.**

Fluxo de dependência:

1. Pessoa 1 finaliza users, auth, tags, venues, events, event_tags e event_pictures.
2. Pessoa 2 usa esse catálogo para montar users_tags, onboarding e contexto.
3. Pessoa 2 finaliza favorites, comments, attendances e demais relações do usuário.
4. Pessoa 2 então implementa feed, planos, busca e mapas em cima da base já pronta.

## 6. Como Evitar Conflito de Código

### Separação por pacotes

- Pessoa 1 mexe principalmente em:
  - users
  - tags
  - venues
  - events
  - event_tags
  - event_pictures
  - auth
  - segurança

- Pessoa 2 mexe principalmente em:
  - users_tags
  - onboarding
  - contexto
  - favorites
  - attendances
  - comments
  - recommendation
  - search
  - maps

### Regra prática para não conflitar

- Pessoa 1 não deve alterar onboarding, contexto ou interações do usuário final.
- Pessoa 2 não deve alterar o catálogo base sem alinhar com a Pessoa 1.
- Os contratos de user, tag, venue e event devem sair primeiro da Pessoa 1.
- A Pessoa 2 deve reutilizar esse catálogo para montar preferências, interação e inteligência.

## 7. Sugestão de Distribuição por Requisitos

### Pessoa 1

- RF01
- RF04
- RF05

### Pessoa 2

- RF02
- RF03
- RF06
- RF07
- RF08
- RF09
- RF10

## 8. Resultado Esperado

Com essa divisão:

- a Pessoa 1 constrói a base de plataforma e catálogo;
- a Pessoa 2 constrói a personalização e a camada de valor para o usuário;
- o risco de conflito cai porque cada pessoa mexe em grupos de entidades diferentes;
- a dependência fica clara: a experiência do usuário usa o catálogo pronto pela Pessoa 1.

## 9. Resumo Curto

- **Pessoa 1**: users, auth, tags, venues, events e mídia dos eventos.
- **Pessoa 2**: users_tags, onboarding, contexto, favoritos, presença, comentários, busca e recomendação.
- **Dependência**: Pessoa 2 depende do catálogo base e da autenticação prontos pela Pessoa 1.
