-- Seed Data for movaComoVai
-- Arquivo expandido com mais dados para popular o banco.

-- 1. Users (10 usuários)
INSERT INTO users (id, name, email, password, user_type, created_at, is_active, username, bio, location, is_private, push_notifications, email_notifications) VALUES
('11111111-1111-1111-1111-111111111111', 'Admin Silva', 'admin@mova.com', '$2a$10$xyz...', 'ADMIN', CURRENT_TIMESTAMP, TRUE, 'adminsilva', 'Administrador do sistema', 'Sobral, Ceará', FALSE, TRUE, TRUE),
('22222222-2222-2222-2222-222222222222', 'Maria Silva', 'maria@exemplo.com', '$2a$10$xyz...', 'COMMON', CURRENT_TIMESTAMP, TRUE, 'mariasilva', 'Amante de música e arte', 'Fortaleza, Ceará', FALSE, TRUE, TRUE),
('33333333-3333-3333-3333-333333333333', 'João Souza', 'joao@exemplo.com', '$2a$10$xyz...', 'COMMON', CURRENT_TIMESTAMP, TRUE, 'joaosouza', 'Explorador urbano e food lover', 'Juazeiro do Norte, Ceará', FALSE, TRUE, TRUE),
('44444444-4444-4444-4444-444444444444', 'Ana Paula', 'ana@exemplo.com', '$2a$10$xyz...', 'COMMON', CURRENT_TIMESTAMP, TRUE, 'anapaula_arte', 'Estudante de artes cênicas', 'Sobral, Ceará', FALSE, TRUE, TRUE),
('55555555-5555-5555-5555-555555555555', 'Carlos Eduardo', 'carlos@exemplo.com', '$2a$10$xyz...', 'COMMON', CURRENT_TIMESTAMP, TRUE, 'carlos_ed', 'Fã de esportes radicais e corridas', 'Crato, Ceará', FALSE, TRUE, FALSE),
('66666666-6666-6666-6666-666666666666', 'Fernanda Lima', 'fernanda@exemplo.com', '$2a$10$xyz...', 'COMMON', CURRENT_TIMESTAMP, TRUE, 'nanda_lima', 'Gosto de provar comidas novas', 'Fortaleza, Ceará', TRUE, FALSE, TRUE),
('77777777-7777-7777-7777-777777777777', 'Rafael Costa', 'rafael@exemplo.com', '$2a$10$xyz...', 'COMMON', CURRENT_TIMESTAMP, TRUE, 'rafa_costa', 'Músico amador nas horas vagas', 'Sobral, Ceará', FALSE, TRUE, TRUE),
('88888888-8888-8888-8888-888888888888', 'Juliana Alves', 'juliana@exemplo.com', '$2a$10$xyz...', 'COMMON', CURRENT_TIMESTAMP, TRUE, 'jualves', 'Sempre em busca da próxima festa', 'Caucaia, Ceará', FALSE, TRUE, TRUE),
('99999999-9999-9999-9999-999999999999', 'Lucas Mendes', 'lucas@exemplo.com', '$2a$10$xyz...', 'COMMON', CURRENT_TIMESTAMP, TRUE, 'mendes_lucas', 'Desenvolvedor e gamer', 'Sobral, Ceará', TRUE, TRUE, FALSE),
('00000000-0000-0000-0000-000000000000', 'Beatriz Rocha', 'beatriz@exemplo.com', '$2a$10$xyz...', 'COMMON', CURRENT_TIMESTAMP, TRUE, 'biarocha', 'Produtora cultural independente', 'Fortaleza, Ceará', FALSE, TRUE, TRUE);

-- 2. Tags (10 tags)
INSERT INTO tags (id, tag_name) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Música'),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Teatro'),
('cccccccc-cccc-cccc-cccc-cccccccccccc', 'Gastronomia'),
('dddddddd-dddd-dddd-dddd-dddddddddddd', 'Esporte'),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'Tecnologia'),
('ffffffff-ffff-ffff-ffff-ffffffffffff', 'Dança'),
('1a1a1a1a-1a1a-1a1a-1a1a-1a1a1a1a1a1a', 'Stand-up Comedy'),
('2b2b2b2b-2b2b-2b2b-2b2b-2b2b2b2b2b2b', 'Cinema'),
('3c3c3c3c-3c3c-3c3c-3c3c-3c3c3c3c3c3c', 'Literatura'),
('4d4d4d4d-4d4d-4d4d-4d4d-4d4d4d4d4d4d', 'Artes Visuais');

-- 3. Users Tags (Interesses)
INSERT INTO users_tags (user_id, tag_id) VALUES
('22222222-2222-2222-2222-222222222222', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'),
('22222222-2222-2222-2222-222222222222', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb'),
('33333333-3333-3333-3333-333333333333', 'cccccccc-cccc-cccc-cccc-cccccccccccc'),
('44444444-4444-4444-4444-444444444444', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb'),
('44444444-4444-4444-4444-444444444444', 'ffffffff-ffff-ffff-ffff-ffffffffffff'),
('55555555-5555-5555-5555-555555555555', 'dddddddd-dddd-dddd-dddd-dddddddddddd'),
('66666666-6666-6666-6666-666666666666', 'cccccccc-cccc-cccc-cccc-cccccccccccc'),
('77777777-7777-7777-7777-777777777777', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'),
('88888888-8888-8888-8888-888888888888', '1a1a1a1a-1a1a-1a1a-1a1a-1a1a1a1a1a1a'),
('99999999-9999-9999-9999-999999999999', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee');

-- 4. Venues (5 locais)
INSERT INTO venues (id, name, number, city, street, neighborhood, landmark, has_parking_lot, has_accessibility, has_bathroom, has_foods_and_drinks) VALUES
('1111aaaa-1111-aaaa-1111-aaaaaaaaaaaa', 'Centro Dragão do Mar', 'S/N', 'Fortaleza', 'R. Dragão do Mar', 'Centro', 'Perto da praia de Iracema', TRUE, TRUE, TRUE, TRUE),
('2222bbbb-2222-bbbb-2222-bbbbbbbbbbbb', 'Theatro José de Alencar', '120', 'Fortaleza', 'Praça José de Alencar', 'Centro', 'Ao lado da estação de metrô', FALSE, TRUE, TRUE, FALSE),
('3333cccc-3333-cccc-3333-cccccccccccc', 'Arco do Triunfo (Praça)', 'S/N', 'Sobral', 'Boulevard João Barbosa', 'Centro', 'Em frente à igreja matriz', TRUE, TRUE, FALSE, TRUE),
('4444dddd-4444-dddd-4444-dddddddddddd', 'Estádio do Junco', '450', 'Sobral', 'Rua Jonh Sanford', 'Junco', 'Próximo ao mercado', TRUE, TRUE, TRUE, TRUE),
('5555eeee-5555-eeee-5555-eeeeeeeeeeee', 'Centro de Convenções', '1000', 'Sobral', 'Av. Dr. Arimatéia Monte e Silva', 'Campo dos Velhos', 'Perto do shopping', TRUE, TRUE, TRUE, TRUE);

-- 5. Events (10 eventos)
INSERT INTO events (id, user_id, venue_id, event_name, description, content_rating, price, starts_at, ends_at) VALUES
('1e1e1e1e-1e1e-1e1e-1e1e-1e1e1e1e1e1e', '00000000-0000-0000-0000-000000000000', '1111aaaa-1111-aaaa-1111-aaaaaaaaaaaa', 'Festival de Jazz de Inverno', 'Um incrível festival de jazz com artistas locais e internacionais de altíssimo nível.', 'LIVRE', 80.00, CURRENT_TIMESTAMP + INTERVAL '10 days', CURRENT_TIMESTAMP + INTERVAL '12 days'),
('2e2e2e2e-2e2e-2e2e-2e2e-2e2e2e2e2e2e', '00000000-0000-0000-0000-000000000000', '2222bbbb-2222-bbbb-2222-bbbbbbbbbbbb', 'Peça: O Auto da Compadecida', 'Adaptação teatral da famosa obra de Ariano Suassuna com elenco original.', '12', 40.00, CURRENT_TIMESTAMP + INTERVAL '5 days', CURRENT_TIMESTAMP + INTERVAL '5 days' + INTERVAL '3 hours'),
('3e3e3e3e-3e3e-3e3e-3e3e-3e3e3e3e3e3e', '33333333-3333-3333-3333-333333333333', '3333cccc-3333-cccc-3333-cccccccccccc', 'Feira Gastronômica de Rua', 'Food trucks, cerveja artesanal e muita comida boa no Arco.', 'LIVRE', 0.00, CURRENT_TIMESTAMP + INTERVAL '2 days', CURRENT_TIMESTAMP + INTERVAL '2 days' + INTERVAL '8 hours'),
('4e4e4e4e-4e4e-4e4e-4e4e-4e4e4e4e4e4e', '11111111-1111-1111-1111-111111111111', '4444dddd-4444-dddd-4444-dddddddddddd', 'Campeonato Cearense de Futebol', 'Final emocionante no Estádio do Junco. Venha torcer pro Guarany!', 'LIVRE', 25.00, CURRENT_TIMESTAMP + INTERVAL '7 days', CURRENT_TIMESTAMP + INTERVAL '7 days' + INTERVAL '4 hours'),
('5e5e5e5e-5e5e-5e5e-5e5e-5e5e5e5e5e5e', '00000000-0000-0000-0000-000000000000', '5555eeee-5555-eeee-5555-eeeeeeeeeeee', 'Campus Party Sobral', 'O maior evento de tecnologia e inovação agora no Ceará.', 'LIVRE', 150.00, CURRENT_TIMESTAMP + INTERVAL '20 days', CURRENT_TIMESTAMP + INTERVAL '23 days'),
('6e6e6e6e-6e6e-6e6e-6e6e-6e6e6e6e6e6e', '88888888-8888-8888-8888-888888888888', '1111aaaa-1111-aaaa-1111-aaaaaaaaaaaa', 'Noite de Stand-Up Comedy', 'Os melhores comediantes locais juntos para te fazer chorar de rir.', '16', 50.00, CURRENT_TIMESTAMP + INTERVAL '14 days', CURRENT_TIMESTAMP + INTERVAL '14 days' + INTERVAL '2 hours'),
('7e7e7e7e-7e7e-7e7e-7e7e-7e7e7e7e7e7e', '00000000-0000-0000-0000-000000000000', '3333cccc-3333-cccc-3333-cccccccccccc', 'Apresentação de Dança ao Ar Livre', 'Grupos de dança contemporânea se apresentam na praça.', 'LIVRE', 0.00, CURRENT_TIMESTAMP + INTERVAL '1 days', CURRENT_TIMESTAMP + INTERVAL '1 days' + INTERVAL '3 hours'),
('8e8e8e8e-8e8e-8e8e-8e8e-8e8e8e8e8e8e', '22222222-2222-2222-2222-222222222222', '2222bbbb-2222-bbbb-2222-bbbbbbbbbbbb', 'Orquestra Sinfônica do Ceará', 'Repertório clássico e erudito imperdível.', 'LIVRE', 35.00, CURRENT_TIMESTAMP + INTERVAL '30 days', CURRENT_TIMESTAMP + INTERVAL '30 days' + INTERVAL '2 hours'),
('9e9e9e9e-9e9e-9e9e-9e9e-9e9e9e9e9e9e', '99999999-9999-9999-9999-999999999999', '5555eeee-5555-eeee-5555-eeeeeeeeeeee', 'Campeonato de e-Sports', 'Torneio regional de League of Legends com grandes prêmios.', '10', 20.00, CURRENT_TIMESTAMP + INTERVAL '15 days', CURRENT_TIMESTAMP + INTERVAL '16 days'),
('0e0e0e0e-0e0e-0e0e-0e0e-0e0e0e0e0e0e', '00000000-0000-0000-0000-000000000000', '1111aaaa-1111-aaaa-1111-aaaaaaaaaaaa', 'Exposição de Arte Moderna', 'Exposição de artistas nordestinos contemporâneos.', 'LIVRE', 15.00, CURRENT_TIMESTAMP + INTERVAL '8 days', CURRENT_TIMESTAMP + INTERVAL '30 days');

-- 6. Event Tags
INSERT INTO event_tags (event_id, tag_id) VALUES
('1e1e1e1e-1e1e-1e1e-1e1e-1e1e1e1e1e1e', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'),
('2e2e2e2e-2e2e-2e2e-2e2e-2e2e2e2e2e2e', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb'),
('3e3e3e3e-3e3e-3e3e-3e3e-3e3e3e3e3e3e', 'cccccccc-cccc-cccc-cccc-cccccccccccc'),
('4e4e4e4e-4e4e-4e4e-4e4e-4e4e4e4e4e4e', 'dddddddd-dddd-dddd-dddd-dddddddddddd'),
('5e5e5e5e-5e5e-5e5e-5e5e-5e5e5e5e5e5e', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee'),
('6e6e6e6e-6e6e-6e6e-6e6e-6e6e6e6e6e6e', '1a1a1a1a-1a1a-1a1a-1a1a-1a1a1a1a1a1a'),
('7e7e7e7e-7e7e-7e7e-7e7e-7e7e7e7e7e7e', 'ffffffff-ffff-ffff-ffff-ffffffffffff'),
('8e8e8e8e-8e8e-8e8e-8e8e-8e8e8e8e8e8e', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'),
('9e9e9e9e-9e9e-9e9e-9e9e-9e9e9e9e9e9e', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee'),
('0e0e0e0e-0e0e-0e0e-0e0e-0e0e0e0e0e0e', '4d4d4d4d-4d4d-4d4d-4d4d-4d4d4d4d4d4d');

-- 7. Event Pictures
INSERT INTO event_pictures (id, event_id, picture_url) VALUES
(gen_random_uuid(), '1e1e1e1e-1e1e-1e1e-1e1e-1e1e1e1e1e1e', 'https://images.unsplash.com/photo-1511192336575-5a79af67a629?auto=format&fit=crop&w=800&q=80'),
(gen_random_uuid(), '2e2e2e2e-2e2e-2e2e-2e2e-2e2e2e2e2e2e', 'https://images.unsplash.com/photo-1507676184212-d0330a15673c?auto=format&fit=crop&w=800&q=80'),
(gen_random_uuid(), '3e3e3e3e-3e3e-3e3e-3e3e-3e3e3e3e3e3e', 'https://images.unsplash.com/photo-1555939594-58d7cb561ad1?auto=format&fit=crop&w=800&q=80'),
(gen_random_uuid(), '4e4e4e4e-4e4e-4e4e-4e4e-4e4e4e4e4e4e', 'https://images.unsplash.com/photo-1522778119026-d647f0596c20?auto=format&fit=crop&w=800&q=80'),
(gen_random_uuid(), '5e5e5e5e-5e5e-5e5e-5e5e-5e5e5e5e5e5e', 'https://images.unsplash.com/photo-1540575467063-178a50c2df87?auto=format&fit=crop&w=800&q=80');

-- 8. Event Likes
INSERT INTO event_likes (event_id, user_id) VALUES
('1e1e1e1e-1e1e-1e1e-1e1e-1e1e1e1e1e1e', '22222222-2222-2222-2222-222222222222'),
('1e1e1e1e-1e1e-1e1e-1e1e-1e1e1e1e1e1e', '77777777-7777-7777-7777-777777777777'),
('2e2e2e2e-2e2e-2e2e-2e2e-2e2e2e2e2e2e', '44444444-4444-4444-4444-444444444444'),
('3e3e3e3e-3e3e-3e3e-3e3e-3e3e3e3e3e3e', '66666666-6666-6666-6666-666666666666'),
('5e5e5e5e-5e5e-5e5e-5e5e-5e5e5e5e5e5e', '99999999-9999-9999-9999-999999999999');

-- 9. Favorites
INSERT INTO favorites (event_id, user_id) VALUES
('1e1e1e1e-1e1e-1e1e-1e1e-1e1e1e1e1e1e', '77777777-7777-7777-7777-777777777777'),
('5e5e5e5e-5e5e-5e5e-5e5e-5e5e5e5e5e5e', '99999999-9999-9999-9999-999999999999'),
('6e6e6e6e-6e6e-6e6e-6e6e-6e6e6e6e6e6e', '88888888-8888-8888-8888-888888888888');

-- 10. Comments
INSERT INTO comments (id, user_id, event_id, comment) VALUES
('c1c1c1c1-c1c1-c1c1-c1c1-c1c1c1c1c1c1', '77777777-7777-7777-7777-777777777777', '1e1e1e1e-1e1e-1e1e-1e1e-1e1e1e1e1e1e', 'Mano, o line-up desse ano tá bizarro de bom. Vejo vocês lá!'),
('c2c2c2c2-c2c2-c2c2-c2c2-c2c2c2c2c2c2', '44444444-4444-4444-4444-444444444444', '2e2e2e2e-2e2e-2e2e-2e2e-2e2e2e2e2e2e', 'Comprei meu ingresso hoje cedo. Suassuna nunca decepciona.'),
('c3c3c3c3-c3c3-c3c3-c3c3-c3c3c3c3c3c3', '66666666-6666-6666-6666-666666666666', '3e3e3e3e-3e3e-3e3e-3e3e-3e3e3e3e3e3e', 'Alguém sabe se vai ter food truck de comida mexicana?'),
('c4c4c4c4-c4c4-c4c4-c4c4-c4c4c4c4c4c4', '99999999-9999-9999-9999-999999999999', '5e5e5e5e-5e5e-5e5e-5e5e-5e5e5e5e5e5e', 'A palestra de IA vai ser que dia exatamente?');

-- 11. Answers
INSERT INTO answers (id, comment_id, user_id, answer) VALUES
(gen_random_uuid(), 'c3c3c3c3-c3c3-c3c3-c3c3-c3c3c3c3c3c3', '33333333-3333-3333-3333-333333333333', 'Pelo que vi na página oficial do Instagram, vai ter sim!'),
(gen_random_uuid(), 'c4c4c4c4-c4c4-c4c4-c4c4-c4c4c4c4c4c4', '11111111-1111-1111-1111-111111111111', 'Na sexta-feira as 14h.');

-- 12. Event Schedules
INSERT INTO event_schedules (id, event_id, schedule_time, title, description) VALUES
(gen_random_uuid(), '1e1e1e1e-1e1e-1e1e-1e1e-1e1e1e1e1e1e', CURRENT_TIMESTAMP + INTERVAL '10 days' + INTERVAL '20 hours', 'Abertura dos Portões', 'Chegue cedo para evitar filas.'),
(gen_random_uuid(), '1e1e1e1e-1e1e-1e1e-1e1e-1e1e1e1e1e1e', CURRENT_TIMESTAMP + INTERVAL '10 days' + INTERVAL '22 hours', 'Show Principal - Trompete de Ouro', 'Atração Internacional'),
(gen_random_uuid(), '5e5e5e5e-5e5e-5e5e-5e5e-5e5e5e5e5e5e', CURRENT_TIMESTAMP + INTERVAL '20 days' + INTERVAL '9 hours', 'Credenciamento', 'Retire seu crachá e kit do participante.');

-- 13. Tickets
INSERT INTO tickets (id, user_id, event_id, ticket_number, status, payment_method, total_price, service_fee, qr_code_hash) VALUES
(gen_random_uuid(), '22222222-2222-2222-2222-222222222222', '1e1e1e1e-1e1e-1e1e-1e1e-1e1e1e1e1e1e', 'TK-JAZZ-001', 'PAID', 'PIX', 80.00, 8.00, 'hash_jazz_001'),
(gen_random_uuid(), '77777777-7777-7777-7777-777777777777', '1e1e1e1e-1e1e-1e1e-1e1e-1e1e1e1e1e1e', 'TK-JAZZ-002', 'PENDING', 'BOLETO', 80.00, 8.00, 'hash_jazz_002'),
(gen_random_uuid(), '44444444-4444-4444-4444-444444444444', '2e2e2e2e-2e2e-2e2e-2e2e-2e2e2e2e2e2e', 'TK-TEATRO-001', 'PAID', 'CREDIT_CARD', 40.00, 4.00, 'hash_teatro_001'),
(gen_random_uuid(), '99999999-9999-9999-9999-999999999999', '5e5e5e5e-5e5e-5e5e-5e5e-5e5e5e5e5e5e', 'TK-CAMPUS-001', 'PAID', 'PIX', 150.00, 15.00, 'hash_campus_001');
