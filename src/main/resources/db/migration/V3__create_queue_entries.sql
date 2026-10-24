
CREATE TABLE queue_entries (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    token_number INT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'WAITING',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    CONSTRAINT fk_queue_user
        FOREIGN KEY (user_id) REFERENCES users(id),

    CONSTRAINT fk_queue_service
        FOREIGN KEY (service_id) REFERENCES services(id),

    UNIQUE KEY uk_queue_service_token (service_id, token_number),

    INDEX idx_queue_user (user_id),
    INDEX idx_queue_status_created (status, created_at)
);
