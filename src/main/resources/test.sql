MERGE INTO users (id, email, login, name, birthday) VALUES
    (1, 'test@example.com', 'testuser', 'Test User', '1990-01-01'),
    (2, 'friend@example.com', 'frienduser', 'Friend User', '1991-01-01'),
    (3, 'common@example.com', 'commonuser', 'Common User', '1992-01-01');

MERGE INTO films (id, name, description, release_date, duration, mpa_id) VALUES
    (1, 'Film1', 'Description1', '2022-01-01 00:00:00', 120, 1),
    (2, 'Film2', 'Description2', '2022-01-02 00:00:00', 150, 2);