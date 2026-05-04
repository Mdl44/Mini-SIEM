CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100),
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) DEFAULT 'ANALYST',
    rank_level INT DEFAULT 1,
    days_survived INT DEFAULT 0,
    max_hp INT DEFAULT 3,
    current_hp INT DEFAULT 3,
    max_senior_calls INT DEFAULT 2,
    current_senior_calls INT DEFAULT 2
);

CREATE TABLE blacklisted_ips (
    id SERIAL PRIMARY KEY,
    ip_address VARCHAR(45) UNIQUE NOT NULL,
    added_by_user_id INT,
    date_added TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user FOREIGN KEY (added_by_user_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE monitored_systems (
    id SERIAL PRIMARY KEY,
    system_name VARCHAR(100) NOT NULL,
    ip_address VARCHAR(45) UNIQUE NOT NULL,
    os_type VARCHAR(50),
    criticality_level VARCHAR(20) DEFAULT 'MEDIUM'
);

CREATE TABLE incidents (
    id SERIAL PRIMARY KEY,
    source_ip VARCHAR(45) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    is_malicious BOOLEAN NOT NULL,
    generation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    analyst_id INT, 
    status VARCHAR(30) DEFAULT 'OPEN',
    
    CONSTRAINT fk_incident_analyst FOREIGN KEY (analyst_id) REFERENCES users(id) ON DELETE SET NULL
);


CREATE TABLE security_events (
    id SERIAL PRIMARY KEY,
    incident_id INT NOT NULL,
    system_id INT NOT NULL,
    event_time TIMESTAMP NOT NULL,
    severity VARCHAR(20) NOT NULL,
    event_type VARCHAR(20) NOT NULL,

    network_attacker_ip VARCHAR(45),
    network_port INT,
    network_protocol VARCHAR(10),

    login_username VARCHAR(100),
    login_status VARCHAR(20),
    login_source_ip VARCHAR(45),

    -- Relațiile (Foreign Keys)
    CONSTRAINT fk_event_incident FOREIGN KEY (incident_id) REFERENCES incidents(id) ON DELETE CASCADE,
    CONSTRAINT fk_event_system FOREIGN KEY (system_id) REFERENCES monitored_systems(id) ON DELETE CASCADE
);