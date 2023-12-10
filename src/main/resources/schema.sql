CREATE SCHEMA IF NOT EXISTS rewards_app;

-- Set time zone
SET TIME ZONE 'Europe/Bucharest';

-- Use the recycling_app schema
SET search_path TO rewards_app;

DROP TABLE IF EXISTS    rewards_app.users,
                        rewards_app.roles,
                        rewards_app.user_roles,
                        rewards_app.account_verifications,
                        rewards_app.reset_pass_verifications,
                        rewards_app.tfa_verifications,
                        rewards_app.user_social_media,
                        rewards_app.recycled_materials,
                        rewards_app.recycling_centers,
                        rewards_app.materials_to_recycle,
                        rewards_app.user_recycling_activities,
                        rewards_app.reward_points,
                        rewards_app.vouchers,
                        rewards_app.educational_resources,
                        rewards_app.user_saved_resources,
                        rewards_app.challenges,
                        rewards_app.user_challenges,
                        rewards_app.leaderboard;

-- Create users table
CREATE TABLE users (
    user_id        BIGSERIAL PRIMARY KEY,
    first_name     VARCHAR(50) NOT NULL,
    last_name      VARCHAR(50) NOT NULL,
    email          VARCHAR(100) NOT NULL UNIQUE,
    password       VARCHAR(255) NOT NULL,
    city           VARCHAR(100) NOT NULL,
    address        VARCHAR(255),
    phone          VARCHAR(15),
    bio            TEXT,
    notif_enabled  BOOLEAN DEFAULT TRUE,
    enabled        BOOLEAN DEFAULT FALSE,
    non_locked     BOOLEAN DEFAULT TRUE,
    using_mfa      BOOLEAN DEFAULT FALSE,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    image_url      VARCHAR(255)
);

-- Create roles table
CREATE TABLE roles (
    role_id        BIGSERIAL PRIMARY KEY,
    name           VARCHAR(50) NOT NULL UNIQUE,
    permission     VARCHAR(255) NOT NULL
);

-- Create user_roles table
CREATE TABLE user_roles (
    id             BIGSERIAL PRIMARY KEY,
    user_id        BIGINT NOT NULL,
    role_id        BIGINT NOT NULL,
    CONSTRAINT uq_user_roles_user UNIQUE (user_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

-- Create account_verifications table
CREATE TABLE account_verifications (
    id             BIGSERIAL PRIMARY KEY,
    user_id        BIGINT NOT NULL,
    url            VARCHAR(255) DEFAULT NULL,
    CONSTRAINT uq_account_verifications_user UNIQUE (user_id),
    CONSTRAINT uq_account_verifications_url UNIQUE (url),
    CONSTRAINT fk_account_verifications_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Create reset_pass_verifications table
CREATE TABLE reset_pass_verifications (
    id                BIGSERIAL PRIMARY KEY,
    user_id           BIGINT NOT NULL,
    url               VARCHAR(255) DEFAULT NULL,
    expiration_date   TIMESTAMP NOT NULL,
    CONSTRAINT uq_reset_pass_verifications_user UNIQUE (user_id),
    CONSTRAINT uq_reset_pass_verifications_url UNIQUE (url),
    CONSTRAINT fk_reset_pass_verifications_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Create tfa_verifications table
CREATE TABLE tfa_verifications (
    id                BIGSERIAL PRIMARY KEY,
    user_id           BIGINT NOT NULL,
    code              VARCHAR(10) NOT NULL,
    expiration_date   TIMESTAMP NOT NULL,
    CONSTRAINT uq_tfa_verifications_user UNIQUE (user_id),
    CONSTRAINT uq_tfa_verifications_code UNIQUE (code),
    CONSTRAINT fk_tfa_verifications_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Create user_social_media table
CREATE TABLE user_social_media (
    user_id         BIGSERIAL PRIMARY KEY REFERENCES users(user_id) ON DELETE CASCADE,
    social_media    VARCHAR(50),
    CONSTRAINT chk_user_social_media CHECK (social_media IN ('Facebook', 'Google', 'Twitter', 'Instagram'))
);

-- Create recycled_materials table
CREATE TABLE recycled_materials (
    material_id     BIGSERIAL PRIMARY KEY,
    name            VARCHAR(50) NOT NULL UNIQUE
);

-- Create recycling_centers table
CREATE TABLE recycling_centers (
    center_id       BIGSERIAL PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    county          VARCHAR(50),
    city            VARCHAR(50),
    address         VARCHAR(255) NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create materials_to_recycle table to establish the one-to-many relationship
CREATE TABLE materials_to_recycle (
    center_id       BIGINT REFERENCES recycling_centers(center_id) ON DELETE CASCADE,
    material_id     BIGINT REFERENCES recycled_materials(material_id) ON DELETE CASCADE,
    PRIMARY KEY(center_id, material_id)
);

-- Create user_recycling_activities table
CREATE TABLE user_recycling_activities (
    activity_id     BIGSERIAL PRIMARY KEY,
    user_id         BIGINT REFERENCES users(user_id) ON DELETE CASCADE,
    center_id       BIGINT REFERENCES recycling_centers(center_id) ON DELETE SET NULL,
    material_id     BIGINT REFERENCES recycled_materials(material_id) ON DELETE SET NULL,
    amount          DECIMAL(10, 2) NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_recycling_activities_user_id FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_user_recycling_activities_center_id FOREIGN KEY (center_id) REFERENCES recycling_centers(center_id) ON DELETE SET NULL,
    CONSTRAINT fk_user_recycling_activities_material_id FOREIGN KEY (material_id) REFERENCES recycled_materials(material_id) ON DELETE SET NULL
);

-- Create reward_points table
CREATE TABLE reward_points (
    user_id         BIGINT PRIMARY KEY REFERENCES users(user_id) ON DELETE CASCADE,
    total_points    INTEGER DEFAULT 0,
    last_updated    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reward_points_user_id FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Create vouchers table
CREATE TABLE vouchers (
    voucher_id      BIGSERIAL PRIMARY KEY,
    user_id         BIGINT REFERENCES users(user_id) ON DELETE CASCADE,
    total_points    INTEGER NOT NULL,
    expiration_date TIMESTAMP NOT NULL,
    unique_code     VARCHAR(20) UNIQUE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_vouchers_user_id FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Create educational_resources table
CREATE TABLE educational_resources (
    resource_id     BIGSERIAL PRIMARY KEY,
    title           VARCHAR(255) NOT NULL,
    content         TEXT NOT NULL,
    resource_type   VARCHAR(50) NOT NULL,
    likes_count     INTEGER DEFAULT 0,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create user_saved_resources table
CREATE TABLE user_saved_resources (
    user_id         BIGINT REFERENCES users(user_id) ON DELETE CASCADE,
    resource_id     BIGINT REFERENCES educational_resources(resource_id) ON DELETE CASCADE,
    PRIMARY KEY(user_id, resource_id),
    CONSTRAINT fk_user_saved_resources_user_id FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_user_saved_resources_resource_id FOREIGN KEY (resource_id) REFERENCES educational_resources(resource_id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Create challenges table
CREATE TABLE challenges (
    challenge_id    BIGSERIAL PRIMARY KEY,
    title           VARCHAR(255) NOT NULL,
    description     TEXT,
    points_reward   INTEGER,
    target_amount   INTEGER,
    material_id     BIGINT REFERENCES recycled_materials(material_id) ON DELETE SET NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create user_challenges table
CREATE TABLE user_challenges (
    user_id         BIGINT REFERENCES users(user_id) ON DELETE CASCADE,
    challenge_id    BIGINT REFERENCES challenges(challenge_id) ON DELETE CASCADE,
    progress        INTEGER DEFAULT 0,
    completed_at    TIMESTAMP,
    PRIMARY KEY(user_id, challenge_id),
    CONSTRAINT fk_user_challenges_user_id FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_user_challenges_challenge_id FOREIGN KEY (challenge_id) REFERENCES challenges(challenge_id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- -- Create badges table
-- CREATE TABLE badges (
--                         badge_id        BIGSERIAL PRIMARY KEY,
--                         name            VARCHAR(50) NOT NULL UNIQUE,
--                         description     TEXT
-- );
--
-- -- Create user_badges table
-- CREATE TABLE user_badges (
--                              user_id         BIGINT PRIMARY KEY REFERENCES users(user_id) ON DELETE CASCADE,
--                              badge_id        BIGINT REFERENCES badges(badge_id) ON DELETE SET NULL,
--                              awarded_at      TIMESTAMP DEFAULT NULL,
--                              CONSTRAINT fk_user_badges_user_id FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
--                              CONSTRAINT fk_user_badges_badge_id FOREIGN KEY (badge_id) REFERENCES badges(badge_id) ON DELETE SET NULL
-- );


-- Create leaderboard table
CREATE TABLE leaderboard (
    user_id         BIGINT PRIMARY KEY REFERENCES users(user_id) ON DELETE CASCADE,
    total_points    INTEGER DEFAULT 0,
    CONSTRAINT fk_leaderboard_user_id FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Index creation
CREATE INDEX idx_user_recycling_activities_user_id ON user_recycling_activities(user_id);
CREATE INDEX idx_user_recycling_activities_center_id ON user_recycling_activities(center_id);
CREATE INDEX idx_user_recycling_activities_material_id ON user_recycling_activities(material_id);
CREATE INDEX idx_user_saved_resources_user_id ON user_saved_resources(user_id);
CREATE INDEX idx_user_saved_resources_resource_id ON user_saved_resources(resource_id);
CREATE INDEX idx_user_challenges_user_id ON user_challenges(user_id);
CREATE INDEX idx_user_challenges_challenge_id ON user_challenges(challenge_id);
CREATE INDEX idx_leaderboard_total_points ON leaderboard(total_points);

-- Insert records into the 'roles' table
INSERT INTO roles (name, permission)
VALUES
    ('ROLE_USER', 'READ:ALL,UPDATE:USER_PROFILE,CREATE:RECYCLING_ACTIVITY,READ:SAVED_RESOURCES,CREATE:SAVED_RESOURCES,JOIN:CHALLENGE'),
    ('ROLE_ADMIN', 'READ:ALL,CREATE:ALL,UPDATE:ALL,DELETE:ALL,MANAGE:USER_PROFILE,MANAGE:USER_ACTIVITIES'),
    ('ROLE_SYSADMIN', 'READ:ALL,CREATE:ALL,UPDATE:ALL,DELETE:ALL,MANAGE:ROLES_PERMISSIONS,ACCESS:ADVANCED_FEATURES');

