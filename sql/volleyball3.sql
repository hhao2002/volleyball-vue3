-- 排球比赛系统 3.0 · MySQL 8 初始化脚本
-- 由 backend/src/main/resources/schema.sql 与 data.sql 生成
-- 注意：会重建业务表，请勿在有正式数据的环境直接执行

CREATE DATABASE IF NOT EXISTS volleyball3 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE USER IF NOT EXISTS 'volleyball'@'%' IDENTIFIED BY 'volleyball';
GRANT ALL PRIVILEGES ON volleyball3.* TO 'volleyball'@'%';
FLUSH PRIVILEGES;
USE volleyball3;
SET NAMES utf8mb4;

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

INSERT INTO users (id, username, password, nickname, email, role, status, created_at, last_login_at) VALUES
(1, 'admin', '$2b$10$1N.S/DNglUv.YXle/b6w3.7w5SsbxEEz2bD6MR4ZIec.nWXQ3Acvy', '系统管理员', 'admin@volleyball.cn', 'ADMIN', 'ACTIVE', '2026-11-18 09:00:00', '2026-11-23 08:30:00'),
(2, 'scorer', '$2b$10$qFE7bDHtA.mnXtgcObR5/.zxkiqDYueP/Pb75KIONgu0Mcuegd1/6', '计分员小王', 'scorer@volleyball.cn', 'SCORER', 'ACTIVE', '2026-11-18 09:10:00', '2026-11-22 19:20:00'),
(3, 'user', '$2b$10$Mv1/dTqghi1VfOdoI.9cQ..b15CdOKnWQKWj1AagCLF1qDzqlTUsS', '排球爱好者', 'user@volleyball.cn', 'USER', 'ACTIVE', '2026-11-19 14:00:00', '2026-11-23 10:05:00');

INSERT INTO login_logs (id, user_id, username, ip, user_agent, success, message, login_time) VALUES
(1, 1, 'admin', '127.0.0.1', 'Chrome / Windows', 1, '登录成功', '2026-11-23 08:30:00'),
(2, 2, 'scorer', '127.0.0.1', 'Edge / Windows', 1, '登录成功', '2026-11-22 19:20:00'),
(3, 3, 'user', '127.0.0.1', 'Chrome / Windows', 1, '登录成功', '2026-11-23 10:05:00');

INSERT INTO applications (id, user_id, type, reason, status, created_at) VALUES
(1, 3, 'SCORER', '熟悉排球规则，希望能参与计分工作', 'PENDING', '2026-11-21 15:20:00');

INSERT INTO teams (id, name, coach, color, created_by, created_at) VALUES
(1, '中国女排', '蔡斌', '#FF6B2C', 2, '2026-11-18 10:00:00'),
(2, '美国女排', '基拉里', '#2F6BFF', 2, '2026-11-18 10:05:00');

INSERT INTO members (id, team_id, name, role, number, court_position) VALUES
(1, 1, '朱婷', '主攻', 2, 2),
(2, 1, '李盈莹', '主攻', 12, 4),
(3, 1, '袁心玥', '副攻', 1, 3),
(4, 1, '王媛媛', '副攻', 7, 5),
(5, 1, '丁霞', '二传', 16, 1),
(6, 1, '王梦洁', '自由人', 18, 6),
(7, 2, '普拉默', '主攻', 3, 2),
(8, 2, '威尔海特', '主攻', 9, 4),
(9, 2, '迪克森', '副攻', 6, 3),
(10, 2, '汉考克', '二传', 11, 1),
(11, 2, '德鲁斯', '接应', 5, 5),
(12, 2, '怀特', '自由人', 21, 6),
(13, NULL, '龚翔宇', '接应', 6, NULL),
(14, NULL, '王缓缓', '副攻', 4, NULL),
(15, NULL, '金烨', '主攻', 8, NULL),
(16, NULL, '刁琳宇', '二传', 14, NULL),
(17, NULL, '倪非凡', '自由人', 17, NULL),
(18, NULL, '杨涵玉', '副攻', 10, NULL),
(19, NULL, '郑益昕', '接应', 15, NULL),
(20, NULL, '高意', '副攻', 13, NULL);

INSERT INTO matches (id, name, sponsor, rules, state, creator_id, left_team_id, right_team_id, best_of, points_per_set, final_set_points, current_set, left_sets, right_sets, left_points, right_points, winner_team_id, start_time, end_time, created_at, event_seq) VALUES
(1, '新生排球友谊赛（演示）', '校排球社', '1局1胜 · 5分制 · 决胜局5分', 'FINISHED', 2, 1, 2, 1, 5, 5, 1, 1, 0, 5, 3, 1, '2026-11-20 19:00:00', '2026-11-20 19:18:00', '2026-11-20 18:30:00', 8);

INSERT INTO match_sets (id, match_id, set_no, left_points, right_points, winner_team_id) VALUES
(1, 1, 1, 5, 3, 1);

INSERT INTO score_records (id, match_id, seq, set_no, team_id, member_id, type, created_by, created_at) VALUES
(1, 1, 1, 1, 1, 1, 'ATTACK', 2, '2026-11-20 19:05:00'),
(2, 1, 2, 1, 2, 7, 'ATTACK', 2, '2026-11-20 19:06:00'),
(3, 1, 3, 1, 1, 1, 'ATTACK', 2, '2026-11-20 19:08:00'),
(4, 1, 4, 1, 1, 2, 'ATTACK', 2, '2026-11-20 19:10:00'),
(5, 1, 5, 1, 1, 2, 'ATTACK', 2, '2026-11-20 19:11:00'),
(6, 1, 6, 1, 2, 11, 'ATTACK', 2, '2026-11-20 19:13:00'),
(7, 1, 7, 1, 2, 7, 'ATTACK', 2, '2026-11-20 19:15:00'),
(8, 1, 8, 1, 1, NULL, 'OUT', 2, '2026-11-20 19:17:00');

INSERT INTO announcements (id, title, content, type, match_id, pinned, created_by, created_at) VALUES
(1, '欢迎来到排球比赛系统 3.0', '全新 Vue3 版本上线：实时比分、拖拽排阵、MVP/SVP 自动评选，一起为排球喝彩！', 'MANUAL', NULL, 1, 1, '2026-11-18 09:30:00'),
(2, '比赛结束：新生排球友谊赛（演示）', '中国女排 1:0 战胜 美国女排。MVP：李盈莹（得分2 · 最高连续2分 · 连续得分1次），SVP：普拉默（得分2 · 最高连续1分 · 连续得分0次）。', 'MATCH_END', 1, 0, 2, '2026-11-20 19:18:00');

INSERT INTO vp (id, match_id, member_id, team_id, type, points, max_streak, streak_count, evaluate) VALUES
(1, 1, 2, 1, 'MVP', 2, 2, 1, '得分2 · 最高连续2分 · 连续得分1次'),
(2, 1, 7, 2, 'SVP', 2, 1, 0, '得分2 · 最高连续1分 · 连续得分0次');
