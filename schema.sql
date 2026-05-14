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
    current_senior_calls INT DEFAULT 2,
    soc_credits INT DEFAULT 0
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

    CONSTRAINT fk_event_incident FOREIGN KEY (incident_id) REFERENCES incidents(id) ON DELETE CASCADE,
    CONSTRAINT fk_event_system FOREIGN KEY (system_id) REFERENCES monitored_systems(id) ON DELETE CASCADE
);

CREATE TABLE achievements (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    bonus_credits INT DEFAULT 0
);

CREATE TABLE  user_achievements (
    user_id INT REFERENCES users(id) ON DELETE CASCADE,
    achievement_id INT REFERENCES achievements(id) ON DELETE CASCADE,
    unlocked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, achievement_id) 
);

INSERT INTO achievements (name, description, bonus_credits) VALUES
('First Blood', 'Successfully block your first malicious traffic.', 50),
('The Flash', 'Block a CRITICAL threat in under 3 seconds.', 150),
('Iron Wall', 'Survive a full week (7 days) with absolute perfection.', 500),
('Paranoia', 'Block 3 legitimate employees in a single shift. Trust no one.', 50),
('Coffee Addict', 'Purchase your first Cyber-Coffee from the Dark Web Shop.', 0),
('Bribery', 'Buy an extra Senior Call from the Dark Web Shop.', 0),
('SLA Breacher', 'Fail to respond to a CRITICAL threat in time. Watch it burn.', 0),
('Rich Analyst', 'Accumulate a balance of $500 SOC Credits.', 200),
('Cyber Legend', 'Get promoted to Senior Lead Analyst (Rank 3).', 1000),
('You Are Fired', 'Lose all your health and get terminated from the SOC.', 0)
ON CONFLICT (name) DO NOTHING;


INSERT INTO users (id, username, email, password, rank_level, days_survived, max_hp, current_hp, max_senior_calls, current_senior_calls) VALUES
(1, 'mdl', 'mdl@soc.local', 'mdl', 2, 2, 4, 4, 2, 2),
(2, 'mdl2', 'mdl2@soc.local', 'mdl2', 1, 0, 3, 3, 1, 1),
(3, 'mdl3', 'mdl3@soc.local', 'mdl3', 1, 0, 3, 0, 1, 1)
ON CONFLICT (username) DO NOTHING;

SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));

INSERT INTO monitored_systems (id, system_name, ip_address, os_type, criticality_level) VALUES
(1, 'Main_Database', '10.0.0.5', 'Linux', 'CRITICAL'),
(2, 'Web_Server_01', '10.0.0.10', 'Linux', 'HIGH'),
(3, 'CEO_Laptop', '10.0.0.50', 'Windows', 'HIGH'),
(4, 'HR_Shared_Drive', '10.0.0.20', 'Windows', 'MEDIUM'),
(5, 'Guest_Wi-Fi_Router', '192.168.1.1', 'Linux', 'LOW'),
(6, 'Router', '10.0.0.1', 'Cisco IOS', 'CRITICAL'),
(7, 'Server_Linux', '10.0.0.2', 'Linux', 'CRITICAL'),
(8, 'Server_Windows', '10.0.0.3', 'Windows', 'LOW')
ON CONFLICT (ip_address) DO NOTHING;

SELECT setval('monitored_systems_id_seq', (SELECT MAX(id) FROM monitored_systems));


INSERT INTO blacklisted_ips (ip_address) VALUES
('45.33.32.156'), ('185.20.13.44'), ('89.123.45.67'), ('193.189.22.11'),
('212.93.100.5'), ('178.65.10.99'), ('62.100.40.10'), ('104.22.50.8'),
('91.10.200.1'), ('145.23.111.9'), ('78.99.12.33'), ('5.10.66.77'),
('112.54.33.22'), ('82.77.10.10'), ('203.0.113.45'), ('24.156.78.90'),
('67.202.100.15'), ('99.12.33.44'), ('200.150.10.5'), ('120.45.33.12'),
('31.220.40.10'), ('88.77.66.55')
ON CONFLICT (ip_address) DO NOTHING;
