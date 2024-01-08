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
                        rewards_app.materials,
                        rewards_app.recycling_centers,
                        rewards_app.materials_to_recycle,
                        rewards_app.user_recycling_activities,
                        rewards_app.reward_points,
                        rewards_app.vouchers,
                        rewards_app.voucher_history,
                        rewards_app.voucher_types,
                        rewards_app.educational_resources,
                        rewards_app.user_saved_resources,
                        rewards_app.challenges,
                        rewards_app.user_challenges,
                        rewards_app.leaderboard;

-- Users Table
CREATE TABLE users (
    user_id        BIGSERIAL PRIMARY KEY,
    first_name     VARCHAR(50) NOT NULL,
    last_name      VARCHAR(50) NOT NULL,
    email          VARCHAR(100) NOT NULL UNIQUE,
    password       VARCHAR(255) NOT NULL,
    county         VARCHAR(255) NOT NULL,
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

-- Roles Table
CREATE TABLE roles (
    role_id        BIGSERIAL PRIMARY KEY,
    name           VARCHAR(50) NOT NULL UNIQUE,
    permission     VARCHAR(255) NOT NULL
);

-- User Roles Mapping Table
CREATE TABLE user_roles (
    id             BIGSERIAL PRIMARY KEY,
    user_id        BIGINT NOT NULL,
    role_id        BIGINT NOT NULL,
    CONSTRAINT uq_user_roles_user UNIQUE (user_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

-- Account Verifications Table
CREATE TABLE account_verifications (
    id             BIGSERIAL PRIMARY KEY,
    user_id        BIGINT NOT NULL,
    url            VARCHAR(255) DEFAULT NULL,
    CONSTRAINT uq_account_verifications_user UNIQUE (user_id),
    CONSTRAINT uq_account_verifications_url UNIQUE (url),
    CONSTRAINT fk_account_verifications_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Password Reset Verifications Table
CREATE TABLE reset_pass_verifications (
    id                BIGSERIAL PRIMARY KEY,
    user_id           BIGINT NOT NULL,
    url               VARCHAR(255) DEFAULT NULL,
    expiration_date   TIMESTAMP NOT NULL,
    CONSTRAINT uq_reset_pass_verifications_user UNIQUE (user_id),
    CONSTRAINT uq_reset_pass_verifications_url UNIQUE (url),
    CONSTRAINT fk_reset_pass_verifications_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Two Factor Authentication Verifications Table
CREATE TABLE tfa_verifications (
    id                BIGSERIAL PRIMARY KEY,
    user_id           BIGINT NOT NULL,
    code              VARCHAR(10) NOT NULL,
    expiration_date   TIMESTAMP NOT NULL,
    CONSTRAINT uq_tfa_verifications_user UNIQUE (user_id),
    CONSTRAINT uq_tfa_verifications_code UNIQUE (code),
    CONSTRAINT fk_tfa_verifications_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Materials Table
CREATE TABLE materials (
    material_id     BIGSERIAL PRIMARY KEY,
    name            VARCHAR(50) NOT NULL UNIQUE,
    reward_points   BIGINT DEFAULT 0
);



-- Create recycling_centers table
CREATE TABLE recycling_centers (
    center_id       BIGSERIAL PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    contact         VARCHAR(25),
    county          VARCHAR(50) NOT NULL,
    city            VARCHAR(50) NOT NULL,
    address         VARCHAR(255) NOT NULL,
    opening_hour    TIME NOT NULL,
    closing_hour    TIME NOT NULL,
    always_open     BOOLEAN DEFAULT FALSE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    image_url       VARCHAR(255)
);

-- Materials Accepted by Centers Table
CREATE TABLE materials_to_recycle (
    center_id       BIGINT REFERENCES recycling_centers(center_id) ON DELETE CASCADE,
    material_id     BIGINT REFERENCES materials(material_id) ON DELETE CASCADE,
    PRIMARY KEY(center_id, material_id)
);

-- User Recycling Activities Table
CREATE TABLE user_recycling_activities (
    activity_id     BIGSERIAL PRIMARY KEY,
    user_id         BIGINT REFERENCES users(user_id) ON DELETE CASCADE,
    center_id       BIGINT REFERENCES recycling_centers(center_id) ON DELETE SET NULL,
    material_id     BIGINT REFERENCES materials(material_id) ON DELETE SET NULL,
    amount          BIGINT NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_recycling_activities_user_id FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_user_recycling_activities_center_id FOREIGN KEY (center_id) REFERENCES recycling_centers(center_id) ON DELETE SET NULL,
    CONSTRAINT fk_user_recycling_activities_material_id FOREIGN KEY (material_id) REFERENCES materials(material_id) ON DELETE SET NULL
);

-- Reward Points Table
CREATE TABLE reward_points (
    user_id         BIGINT PRIMARY KEY REFERENCES users(user_id) ON DELETE CASCADE,
    total_points    BIGINT DEFAULT 0,
    last_updated    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reward_points_user_id FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Vouchers Table
CREATE TABLE vouchers (
    voucher_id      BIGSERIAL PRIMARY KEY,
    user_id         BIGINT REFERENCES users(user_id) ON DELETE CASCADE,
    voucher_type_id BIGINT REFERENCES voucher_types(voucher_type_id) ON DELETE RESTRICT,
    unique_code     VARCHAR(20) UNIQUE NOT NULL,
    redeemed        BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at      TIMESTAMP NOT NULL,
    CONSTRAINT fk_vouchers_user_id FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_vouchers_voucher_type_id FOREIGN KEY (voucher_type_id) REFERENCES voucher_types(voucher_type_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

-- Voucher Types Table
CREATE TABLE voucher_types (
    voucher_type_id   BIGSERIAL PRIMARY KEY,
    name              VARCHAR(50) NOT NULL,
    threshold_points  INT NOT NULL
);

-- Voucher History Table
CREATE TABLE voucher_history (
    voucher_id      BIGINT REFERENCES vouchers(voucher_id) ON DELETE CASCADE,
    redeem_date     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_voucher_history_voucher_id FOREIGN KEY (voucher_id) REFERENCES vouchers(voucher_id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Educational Resources Table
CREATE TABLE educational_resources (
    resource_id     BIGSERIAL PRIMARY KEY,
    title           VARCHAR(255) NOT NULL,
    content         TEXT NOT NULL,
    content_type_id BIGINT REFERENCES content_types(content_type_id) ON DELETE RESTRICT,
    likes_count     BIGINT DEFAULT 0,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User Saved Educational Resources Table
CREATE TABLE user_saved_resources (
    user_id         BIGINT REFERENCES users(user_id) ON DELETE CASCADE,
    resource_id     BIGINT REFERENCES educational_resources(resource_id) ON DELETE CASCADE,
    PRIMARY KEY(user_id, resource_id),
    CONSTRAINT fk_user_saved_resources_user_id FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_user_saved_resources_resource_id FOREIGN KEY (resource_id) REFERENCES educational_resources(resource_id) ON DELETE CASCADE ON UPDATE CASCADE
);


-- Educational Resource Content Types Table
CREATE TABLE content_types (
    content_type_id BIGSERIAL PRIMARY KEY,
    type_name      VARCHAR(50) NOT NULL UNIQUE
);


-- Educational Resource Categories Table
CREATE TABLE categories (
    category_id    BIGSERIAL PRIMARY KEY,
    category_name  VARCHAR(50) NOT NULL UNIQUE
);


-- Educational Resource Categories Mapping Table
CREATE TABLE resource_categories (
    resource_id    BIGINT REFERENCES educational_resources(resource_id) ON DELETE CASCADE,
    category_id    BIGINT REFERENCES categories(category_id) ON DELETE CASCADE,
    PRIMARY KEY(resource_id, category_id)
);


-- User Engagement with Educational Resources Table
CREATE TABLE user_engagement (
    user_id        BIGINT REFERENCES users(user_id) ON DELETE CASCADE,
    resource_id    BIGINT REFERENCES educational_resources(resource_id) ON DELETE CASCADE,
    like_status    BOOLEAN DEFAULT FALSE,
    share_status   BOOLEAN DEFAULT FALSE,
    saved_status   BOOLEAN DEFAULT FALSE,
    PRIMARY KEY(user_id, resource_id),
    CONSTRAINT fk_user_engagement_user_id FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_user_engagement_resource_id FOREIGN KEY (resource_id) REFERENCES educational_resources(resource_id) ON DELETE CASCADE ON UPDATE CASCADE
);


-- Index creation
CREATE INDEX idx_user_recycling_activities_user_id ON user_recycling_activities(user_id);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_user_recycling_activities_center_id ON user_recycling_activities(center_id);
CREATE INDEX idx_user_recycling_activities_material_id ON user_recycling_activities(material_id);
CREATE INDEX idx_user_saved_resources_user_id ON user_saved_resources(user_id);
CREATE INDEX idx_user_saved_resources_resource_id ON user_saved_resources(resource_id);

-- Insert records into the 'roles' table
INSERT INTO roles (name, permission)
VALUES
    ('ROLE_USER', 'READ:VOUCHERS,READ:CENTERS,READ:ACTIVITIES,UPDATE:PROFILE,CREATE:ACTIVITY,CREATE:SAVED_RESOURCES,JOIN:CHALLENGE'),
    ('ROLE_ADMIN', 'READ:ALL,CREATE:CENTER,CREATE:RESOURCE,UPDATE:CENTER,UPDATE:PROFILE,DELETE:ALL'),
    ('ROLE_SYSADMIN', 'READ:ALL,CREATE:ALL,UPDATE:ALL,DELETE:ALL,MANAGE:ROLES,MANAGE:USER_STATUS');


-- Insert some sample data with points per unit
INSERT INTO materials (name, reward_points)
VALUES
    ('PLASTIC', 10),
    ('ALUMINIUM', 6),
    ('METALS', 5),
    ('GLASS', 6),
    ('PAPER', 10),
    ('ELECTRONICS', 12);