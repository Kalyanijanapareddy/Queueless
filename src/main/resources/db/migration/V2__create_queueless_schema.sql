
-- Queueless initial application schema
-- Database: queueless_db

CREATE TABLE roles (
    id BIGINT NOT NULL AUTO_INCREMENT,
    role_name VARCHAR(40) NOT NULL,
    description VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT uk_roles_role_name UNIQUE (role_name)
) ENGINE=InnoDB;

CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(180) NOT NULL,
    phone VARCHAR(20),
    password_hash VARCHAR(255) NOT NULL,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    role_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT uk_users_phone UNIQUE (phone),
    CONSTRAINT fk_users_role FOREIGN KEY (role_id)
        REFERENCES roles(id)
) ENGINE=InnoDB;

CREATE TABLE service_categories (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(120) NOT NULL,
    description VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id),
    CONSTRAINT uk_categories_name UNIQUE (name),
    CONSTRAINT uk_categories_slug UNIQUE (slug)
) ENGINE=InnoDB;

CREATE TABLE organizations (
    id BIGINT NOT NULL AUTO_INCREMENT,
    category_id BIGINT NOT NULL,
    name VARCHAR(180) NOT NULL,
    description TEXT,
    registration_number VARCHAR(100),
    email VARCHAR(180),
    phone VARCHAR(20),
    website VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uk_organizations_registration
        UNIQUE (registration_number),
    CONSTRAINT fk_organizations_category
        FOREIGN KEY (category_id) REFERENCES service_categories(id),
    INDEX idx_organizations_category (category_id),
    INDEX idx_organizations_name (name)
) ENGINE=InnoDB;

CREATE TABLE branches (
    id BIGINT NOT NULL AUTO_INCREMENT,
    organization_id BIGINT NOT NULL,
    name VARCHAR(150) NOT NULL,
    branch_code VARCHAR(60),
    address_line VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    postal_code VARCHAR(15),
    latitude DECIMAL(10,7),
    longitude DECIMAL(10,7),
    phone VARCHAR(20),
    opening_time TIME,
    closing_time TIME,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uk_branch_code
        UNIQUE (organization_id, branch_code),
    CONSTRAINT fk_branches_organization
        FOREIGN KEY (organization_id) REFERENCES organizations(id),
    INDEX idx_branches_city (city)
) ENGINE=InnoDB;

CREATE TABLE facilities (
    id BIGINT NOT NULL AUTO_INCREMENT,
    branch_id BIGINT NOT NULL,
    name VARCHAR(120) NOT NULL,
    description VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id),
    CONSTRAINT uk_facility_branch_name UNIQUE (branch_id, name),
    CONSTRAINT fk_facilities_branch
        FOREIGN KEY (branch_id) REFERENCES branches(id)
) ENGINE=InnoDB;

CREATE TABLE professionals (
    id BIGINT NOT NULL AUTO_INCREMENT,
    branch_id BIGINT NOT NULL,
    full_name VARCHAR(150) NOT NULL,
    designation VARCHAR(100),
    qualification VARCHAR(180),
    specialization VARCHAR(150),
    email VARCHAR(180),
    phone VARCHAR(20),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id),
    CONSTRAINT fk_professionals_branch
        FOREIGN KEY (branch_id) REFERENCES branches(id),
    INDEX idx_professionals_branch (branch_id),
    INDEX idx_professionals_name (full_name)
) ENGINE=InnoDB;

CREATE TABLE services (
    id BIGINT NOT NULL AUTO_INCREMENT,
    branch_id BIGINT NOT NULL,
    professional_id BIGINT,
    name VARCHAR(180) NOT NULL,
    description TEXT,
    estimated_duration_minutes INT NOT NULL DEFAULT 15,
    base_fee DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id),
    CONSTRAINT fk_services_branch
        FOREIGN KEY (branch_id) REFERENCES branches(id),
    CONSTRAINT fk_services_professional
        FOREIGN KEY (professional_id) REFERENCES professionals(id),
    INDEX idx_services_branch (branch_id),
    INDEX idx_services_name (name)
) ENGINE=InnoDB;

-- Initial roles
INSERT INTO roles (role_name, description) VALUES
('ADMIN', 'Platform administrator'),
('PROVIDER', 'Organization or service provider administrator'),
('STAFF', 'Branch staff member'),
('USER', 'Customer using Queueless');

-- Initial homepage categories
INSERT INTO service_categories (name, slug, description) VALUES
('Medical', 'medical', 'Hospitals, clinics and healthcare services'),
('Banking', 'banking', 'Banks, branches and banking services'),
('Government', 'government', 'Government offices and public services'),
('Education', 'education', 'Educational institutions and services'),
('Others', 'others', 'Other organizations and public services');

-- Remove the temporary table if it was created by the earlier test migration.
DROP TABLE IF EXISTS schema_version_check;
