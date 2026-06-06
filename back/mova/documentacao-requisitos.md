# Documentação de Requisitos do Sistema

## 1. Visão Geral

Este documento consolida os requisitos funcionais, não funcionais e a estória de usuário principal do sistema. O objetivo é descrever, de forma estruturada, o comportamento esperado da aplicação para apoiar o desenvolvimento, validação e evolução do produto.

## 2. Requisitos Funcionais

### 2.1 Módulo de Gestão de Usuários e Perfis

**RF01**: O sistema deve permitir o cadastro e autenticação de usuários via e-mail/senha e Single Sign-On (SSO - Google/Apple), facilitando o acesso rápido de turistas.

**RF02**: O sistema deve solicitar e armazenar as preferências do usuário no primeiro acesso (onboarding), permitindo a seleção de tags de interesse, como cultura local, vida noturna, gastronomia e gratuito.

**RF03**: O sistema deve permitir que o usuário defina o contexto atual da sua saída, como sozinho, casal, em grupo ou com crianças, para refinar as recomendações daquela sessão.

### 2.2 Módulo de Gestão de Eventos e Locais (Backoffice/Admin)

**RF04**: O sistema deve prover um painel administrativo para o cadastro de eventos culturais e atividades ao ar livre em Sobral.

**RF05**: No cadastro de eventos, o sistema deve exigir o preenchimento de campos detalhados para suprir a deficiência das plataformas atuais, incluindo título, descrição completa, tags de categoria, data e hora de início e fim, endereço geolocalizado, preço, classificação indicativa e infraestrutura do local, como acessibilidade e estacionamento.

### 2.3 Módulo de Recomendação e Planos (Core Business)

**RF06**: O sistema deve executar um algoritmo de recomendação que cruze as tags do perfil do usuário e o contexto da saída com as características dos eventos disponíveis.

**RF07**: O sistema deve gerar e sugerir Planos de Atividades, agrupando eventos ou locais próximos geograficamente ou logicamente, como sugerir um café próximo ao local de um evento cultural que termina à tarde.

**RF08**: O sistema deve exibir o feed de recomendações ordenado por relevância, com percentual de match com o perfil.

### 2.4 Módulo de Interação

**RF09**: O sistema deve permitir que o usuário adicione eventos aos Favoritos ou confirme presença, retroalimentando a base de dados de preferências do usuário.

**RF10**: O sistema deve permitir a busca textual livre e filtragem avançada, por data, preço e bairro em Sobral, para usuários que não desejam usar a recomendação automática.

## 3. Requisitos Não Funcionais

### 3.1 Desempenho

**RNF01 (Desempenho - Latência)**: O algoritmo de recomendação deve processar e retornar o feed de planos personalizados em um tempo máximo de 2 segundos para 95% das requisições, considerando p95.

**Justificativa**: Como a geração de planos envolve cruzamento de múltiplos dados relacionais, como usuário, tags, eventos e locais, uma modelagem de banco de dados eficiente e possível uso de cache em memória são necessários para não degradar a experiência do usuário mobile.

### 3.2 Arquitetura e Manutenibilidade

**RNF02 (Arquitetura e Manutenibilidade)**: O backend deve ser desenvolvido utilizando o padrão de API RESTful, aplicando princípios de Clean Architecture e SOLID, mantendo a regra de negócios da recomendação estritamente isolada da camada de roteamento e controllers.

**Justificativa**: Garante que o motor de recomendação possa evoluir no futuro, por exemplo migrando para um microserviço dedicado, sem impactar o restante do monólito.

### 3.3 Confiabilidade e Integridade

**RNF03 (Confiabilidade e Integridade)**: O banco de dados relacional, como PostgreSQL, deve garantir propriedades ACID nas transações, especialmente no mapeamento de interesses dos usuários e na integridade dos relacionamentos entre eventos e locais.

**Justificativa**: Evita dados órfãos, como sugerir um evento vinculado a um local que foi removido do sistema.

### 3.4 Interoperabilidade

**RNF04 (Interoperabilidade)**: A aplicação mobile e web deve ser capaz de integrar-se via API com serviços de mapas de terceiros, como Google Maps API, para renderizar a localização e as rotas dos planos de atividades sugeridos.

**Justificativa**: Essencial para a orientação de visitantes e turistas na cidade de Sobral.

### 3.5 Segurança

**RNF05 (Segurança)**: A comunicação entre o client, web ou mobile, e o servidor deve ser criptografada via HTTPS, e a autenticação das rotas protegidas deve ser gerida via tokens JWT com tempo de expiração definido.

**Justificativa**: Protege os dados pessoais dos usuários e previne acessos indevidos aos endpoints da API.

## 4. Estória de Usuário

### 4.1 Frente do Cartão

**Título**: Recomendação Personalizada de Eventos

**Prioridade**: 1 - Alta

**Para quem?** Usuário final, turista ou residente

**O que?** Desejo visualizar um feed de eventos sugeridos que cruzem com os meus interesses cadastrados.

**Por que?** Para que eu possa decidir rapidamente o que fazer na cidade de Sobral sem precisar ler sobre eventos que não me interessam.

**Pontos**: 5, usando escala Fibonacci para indicar complexidade média, pois envolve lógica de banco de dados e algoritmo.

### 4.2 Verso do Cartão - Critérios de Aceitação

- O sistema deve carregar o feed de recomendações assim que o login for efetuado com sucesso.
- O feed deve exibir apenas eventos que tenham pelo menos 1 tag correspondente às preferências do usuário.
- Eventos com mais tags em comum com o usuário devem aparecer no topo da lista.
- Se o usuário não tiver preferências cadastradas, o sistema deve sugerir os Eventos Mais Populares como fallback.
- O feed deve ser renderizado em menos de 3 segundos, respeitando o RNF01.

## 5. Observações de Negócio

- O sistema está centrado em Sobral como contexto geográfico principal.
- As preferências de usuário, o contexto da saída e as interações com eventos devem retroalimentar o motor de recomendação.
- O cadastro de eventos deve ser mais completo do que soluções concorrentes, com foco em utilidade para turistas e residentes.
- O fluxo de recomendação deve priorizar relevância e contexto, não apenas proximidade geográfica.

## 6. Estrutura Funcional Resumida

- Usuários e perfis.
- Onboarding com preferências.
- Contexto atual da saída.
- Cadastro administrativo de eventos e locais.
- Algoritmo de recomendação.
- Planos de atividades.
- Favoritos e confirmação de presença.
- Busca textual e filtros avançados.

## 7. Rastreabilidade Inicial

### Requisitos Funcionais

- RF01 -> autenticação e cadastro de usuários.
- RF02 -> onboarding e preferências de tags.
- RF03 -> contexto da saída.
- RF04 -> painel administrativo de eventos.
- RF05 -> cadastro detalhado de eventos e locais.
- RF06 -> motor de recomendação.
- RF07 -> geração de planos de atividades.
- RF08 -> feed ordenado por relevância.
- RF09 -> favoritos e presença.
- RF10 -> busca e filtragem avançada.

### Requisitos Não Funcionais

- RNF01 -> desempenho do feed e planos.
- RNF02 -> arquitetura e separação de responsabilidades.
- RNF03 -> integridade transacional e relacional.
- RNF04 -> integração com mapas.
- RNF05 -> segurança e autenticação.

## 8. Conclusão

Esta documentação consolida a base de requisitos do sistema e serve como referência para implementação, validação e evolução do produto. Ela pode ser refinada posteriormente com regras de negócio adicionais, critérios de aceite mais detalhados e mapeamento para casos de uso, endpoints e entidades de domínio.
