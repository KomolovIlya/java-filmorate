CREATE TABLE IF NOT EXISTS mpa_ratings
(
    id
    INTEGER
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    name
    VARCHAR
(
    50
) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS genres
(
    id
    INTEGER
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    name
    VARCHAR
(
    50
) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS users
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    email
    VARCHAR
(
    255
) NOT NULL UNIQUE,
    login VARCHAR
(
    255
) NOT NULL UNIQUE,
    name VARCHAR
(
    255
) NOT NULL,
    birthday DATE
    );

CREATE TABLE IF NOT EXISTS films
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    name
    VARCHAR
(
    255
) NOT NULL,
    description VARCHAR
(
    200
),
    release_date DATE,
    duration INTEGER,
    mpa_id INTEGER REFERENCES mpa_ratings
(
    id
) ON DELETE SET NULL
    );

CREATE TABLE IF NOT EXISTS film_genres
(
    film_id
    BIGINT
    REFERENCES
    films
(
    id
) ON DELETE CASCADE,
    genre_id INTEGER REFERENCES genres
(
    id
)
  ON DELETE CASCADE,
    PRIMARY KEY
(
    film_id,
    genre_id
)
    );

CREATE TABLE IF NOT EXISTS film_likes
(
    film_id
    BIGINT
    REFERENCES
    films
(
    id
) ON DELETE CASCADE,
    user_id BIGINT REFERENCES users
(
    id
)
  ON DELETE CASCADE,
    PRIMARY KEY
(
    film_id,
    user_id
)
    );

CREATE TABLE IF NOT EXISTS friends
(
    user_id
    BIGINT
    REFERENCES
    users
(
    id
) ON DELETE CASCADE,
    friend_id BIGINT REFERENCES users
(
    id
)
  ON DELETE CASCADE,
    is_confirmed BOOLEAN DEFAULT FALSE,
    PRIMARY KEY
(
    user_id,
    friend_id
)
    );