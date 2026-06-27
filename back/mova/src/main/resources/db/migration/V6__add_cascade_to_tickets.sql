ALTER TABLE tickets DROP CONSTRAINT fk_tickets_event;
ALTER TABLE tickets ADD CONSTRAINT fk_tickets_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE;
