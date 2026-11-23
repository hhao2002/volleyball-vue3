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
