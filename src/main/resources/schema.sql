-- Tabla: profiles
CREATE TABLE IF NOT EXISTS profiles (
    id                  BIGSERIAL PRIMARY KEY,
    username            VARCHAR(100)   NOT NULL UNIQUE,
    full_name           VARCHAR(255),
    bio                 TEXT,
    profile_pic_url     TEXT,
    is_verified         BOOLEAN        DEFAULT FALSE,
    is_business         BOOLEAN        DEFAULT FALSE,
    category            VARCHAR(100),
    external_url        TEXT,
    followers_count     BIGINT,
    following_count     BIGINT,
    posts_count         INT,
    avg_engagement_rate DECIMAL(5,2),
    last_fetched_at     TIMESTAMP,
    saved_at            TIMESTAMP      NOT NULL
);

-- Tabla: posts
CREATE TABLE IF NOT EXISTS posts (
    id                  BIGSERIAL PRIMARY KEY,
    instagram_id        VARCHAR(100),
    username            VARCHAR(100)   NOT NULL,
    source_url          TEXT           NOT NULL,
    short_code          VARCHAR(50),
    type                VARCHAR(20),
    caption             TEXT,
    hashtags            TEXT[],
    hashtag_count       INT            DEFAULT 0,
    mentions            TEXT[],
    likes_count         BIGINT,
    comments_count      BIGINT,
    views_count         BIGINT,
    engagement_rate     DECIMAL(5,2),
    media_count         INT,
    duration_seconds    INT,
    download_urls       TEXT[],
    posted_at           TIMESTAMP,
    posted_hour         INT,
    posted_day_of_week  INT,
    saved_at            TIMESTAMP      NOT NULL
);

-- Tabla: pipeline_executions
CREATE TABLE IF NOT EXISTS pipeline_executions (
    id              BIGSERIAL PRIMARY KEY,
    username        VARCHAR(100)  NOT NULL,
    status          VARCHAR(20)   NOT NULL,
    posts_fetched   INT           DEFAULT 0,
    posts_saved     INT           DEFAULT 0,
    error_message   TEXT,
    executed_at     TIMESTAMP     NOT NULL
);

-- Indices para consultas analiticas
CREATE INDEX IF NOT EXISTS idx_posts_username       ON posts(username);
CREATE INDEX IF NOT EXISTS idx_posts_type           ON posts(type);
CREATE INDEX IF NOT EXISTS idx_posts_posted_at      ON posts(posted_at);
CREATE INDEX IF NOT EXISTS idx_posts_engagement     ON posts(engagement_rate DESC);
CREATE INDEX IF NOT EXISTS idx_posts_posted_hour    ON posts(posted_hour);
CREATE INDEX IF NOT EXISTS idx_executions_username  ON pipeline_executions(username);
