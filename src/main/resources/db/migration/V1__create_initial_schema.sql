
CREATE TABLE schema_version_check (
    id BIGINT NOT NULL PRIMARY KEY,
    description VARCHAR(100) NOT NULL
);

INSERT INTO schema_version_check (id, description)
VALUES (1, 'Queueless database initialized');
