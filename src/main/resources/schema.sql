-- Tabla: profiles
CREATE TABLE IF NOT EXISTS profiles (
    id              BIGSERIAL       PRIMARY KEY,
    username        VARCHAR(100)    NOT NULL UNIQUE,
    full_name       VARCHAR(255),
    bio             TEXT,
    profile_pic_url TEXT,
    followers_count BIGINT,
    following_count BIGINT,
    posts_count     INT,
    is_verified     BOOLEAN         DEFAULT FALSE,
    active          BOOLEAN         DEFAULT TRUE,
    saved_at        TIMESTAMP       NOT NULL
);

-- Tabla: posts
CREATE TABLE IF NOT EXISTS posts (
    id          BIGSERIAL       PRIMARY KEY,
    username    VARCHAR(100)    NOT NULL,
    source_url  TEXT            NOT NULL UNIQUE,
    media_url   TEXT,
    media_type  VARCHAR(20),
    caption     TEXT,
    active      BOOLEAN         DEFAULT TRUE,
    saved_at    TIMESTAMP       NOT NULL
);

-- Indices
CREATE INDEX IF NOT EXISTS idx_profiles_username ON profiles(username);
CREATE INDEX IF NOT EXISTS idx_profiles_active   ON profiles(active);
CREATE INDEX IF NOT EXISTS idx_posts_username    ON posts(username);
CREATE INDEX IF NOT EXISTS idx_posts_active      ON posts(active);
