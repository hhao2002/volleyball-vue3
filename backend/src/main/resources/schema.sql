DROP TABLE IF EXISTS vp;
DROP TABLE IF EXISTS announcements;
DROP TABLE IF EXISTS score_records;
DROP TABLE IF EXISTS match_sets;
DROP TABLE IF EXISTS matches;
DROP TABLE IF EXISTS members;
DROP TABLE IF EXISTS teams;
DROP TABLE IF EXISTS applications;
DROP TABLE IF EXISTS login_logs;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL,
  password VARCHAR(100) NOT NULL,
  nickname VARCHAR(50),
  email VARCHAR(100),
  role VARCHAR(20) NOT NULL DEFAULT 'USER',
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_login_at DATETIME
);
CREATE UNIQUE INDEX uk_users_username ON users (username);
CREATE INDEX idx_users_role ON users (role);

CREATE TABLE login_logs (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT,
  username VARCHAR(50),
  ip VARCHAR(64),
  user_agent VARCHAR(255),
  success TINYINT NOT NULL DEFAULT 1,
  message VARCHAR(100),
  login_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_login_logs_time ON login_logs (login_time);
CREATE INDEX idx_login_logs_user ON login_logs (user_id);

CREATE TABLE applications (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  type VARCHAR(20) NOT NULL,
  reason VARCHAR(255),
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  handler_id BIGINT,
  handle_remark VARCHAR(255),
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  handled_at DATETIME
);
CREATE INDEX idx_applications_user ON applications (user_id);
CREATE INDEX idx_applications_status ON applications (status);

CREATE TABLE teams (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  coach VARCHAR(50),
  color VARCHAR(20) DEFAULT '#FF6B2C',
  created_by BIGINT,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX uk_teams_name ON teams (name);

CREATE TABLE members (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  team_id BIGINT,
  name VARCHAR(50) NOT NULL,
  role VARCHAR(20) DEFAULT '主攻',
  number INT,
  court_position INT,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_members_team ON members (team_id);

CREATE TABLE matches (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  sponsor VARCHAR(100),
  rules VARCHAR(255),
  state VARCHAR(20) NOT NULL DEFAULT 'READY',
  creator_id BIGINT NOT NULL,
  left_team_id BIGINT NOT NULL,
  right_team_id BIGINT NOT NULL,
  best_of INT NOT NULL DEFAULT 5,
  points_per_set INT NOT NULL DEFAULT 25,
  final_set_points INT NOT NULL DEFAULT 15,
  current_set INT NOT NULL DEFAULT 1,
  left_sets INT NOT NULL DEFAULT 0,
  right_sets INT NOT NULL DEFAULT 0,
  left_points INT NOT NULL DEFAULT 0,
  right_points INT NOT NULL DEFAULT 0,
  winner_team_id BIGINT,
  start_time DATETIME,
  end_time DATETIME,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  event_seq INT NOT NULL DEFAULT 0
);
CREATE INDEX idx_matches_state ON matches (state);
CREATE INDEX idx_matches_creator ON matches (creator_id);
CREATE INDEX idx_matches_left_team ON matches (left_team_id);
CREATE INDEX idx_matches_right_team ON matches (right_team_id);

CREATE TABLE match_sets (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  match_id BIGINT NOT NULL,
  set_no INT NOT NULL,
  left_points INT NOT NULL DEFAULT 0,
  right_points INT NOT NULL DEFAULT 0,
  winner_team_id BIGINT
);
CREATE UNIQUE INDEX uk_match_sets ON match_sets (match_id, set_no);

CREATE TABLE score_records (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  match_id BIGINT NOT NULL,
  seq INT NOT NULL,
  set_no INT NOT NULL,
  team_id BIGINT NOT NULL,
  member_id BIGINT,
  type VARCHAR(20) NOT NULL DEFAULT 'ATTACK',
  created_by BIGINT,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_score_records_match ON score_records (match_id, seq);

CREATE TABLE announcements (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(100) NOT NULL,
  content TEXT,
  type VARCHAR(20) NOT NULL DEFAULT 'MANUAL',
  match_id BIGINT,
  pinned TINYINT NOT NULL DEFAULT 0,
  created_by BIGINT,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_announcements_created ON announcements (created_at);

CREATE TABLE vp (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  match_id BIGINT NOT NULL,
  member_id BIGINT,
  team_id BIGINT NOT NULL,
  type VARCHAR(10) NOT NULL,
  points INT NOT NULL DEFAULT 0,
  max_streak INT NOT NULL DEFAULT 0,
  streak_count INT NOT NULL DEFAULT 0,
  evaluate VARCHAR(255)
);
CREATE UNIQUE INDEX uk_vp_match_type ON vp (match_id, type);
