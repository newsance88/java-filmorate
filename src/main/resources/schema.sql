CREATE TABLE IF NOT EXISTS mpa (
  id SERIAL PRIMARY KEY,
  name VARCHAR(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS genres (
  id SERIAL PRIMARY KEY,
  name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
  id SERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(500) NOT NULL,
  release_date TIMESTAMP NOT NULL,
  duration INT NOT NULL,
  mpa_id INT,
  CONSTRAINT films_mpa_fk FOREIGN KEY (mpa_id) REFERENCES mpa (id)
);

CREATE TABLE IF NOT EXISTS film_genres (
  film_id INT NOT NULL,
  genre_id INT NOT NULL,
  CONSTRAINT film_genres_pk PRIMARY KEY (film_id, genre_id),
  CONSTRAINT film_genres_films_fk FOREIGN KEY (film_id) REFERENCES films(id),
  CONSTRAINT film_genres_genres_fk FOREIGN KEY (genre_id) REFERENCES genres(id)
);

CREATE TABLE IF NOT EXISTS users (
  id SERIAL PRIMARY KEY,
  email VARCHAR(100) NOT NULL,
  login VARCHAR(150) NOT NULL,
  name VARCHAR(150),
  birthday DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS friends (
  request_user_id INT NOT NULL,
  accept_friend_id INT NOT NULL,
  CONSTRAINT users_friends_pk PRIMARY KEY (request_user_id, accept_friend_id),
  CONSTRAINT users_friends_request_fk FOREIGN KEY (request_user_id) REFERENCES users(id),
  CONSTRAINT users_friends_accept_fk FOREIGN KEY (accept_friend_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS likes (
  film_id INT NOT NULL,
  user_id INT NOT NULL,
  CONSTRAINT likes_pk PRIMARY KEY (film_id, user_id),
  CONSTRAINT likes_fk FOREIGN KEY (film_id) REFERENCES films(id),
  CONSTRAINT likes_user_fk FOREIGN KEY (user_id) REFERENCES users(id)
);
