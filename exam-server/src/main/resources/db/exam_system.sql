-- ============================================
-- 在线考试系统 - 数据库初始化脚本
-- ============================================

CREATE DATABASE IF NOT EXISTS exam_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE exam_system;

-- 1. 班级表
DROP TABLE IF EXISTS `class`;
CREATE TABLE `class` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '班级ID',
    `class_name` VARCHAR(100) NOT NULL COMMENT '班级名称',
    `grade` VARCHAR(50) DEFAULT NULL COMMENT '年级',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '班级描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级表';

-- 2. 用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码',
    `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
    `role` VARCHAR(20) NOT NULL DEFAULT 'STUDENT' COMMENT '角色: STUDENT-学生, TEACHER-教师, ADMIN-管理员',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `class_id` BIGINT DEFAULT NULL COMMENT '所属班级ID',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_role` (`role`),
    KEY `idx_class_id` (`class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 3. 试题表
DROP TABLE IF EXISTS `question`;
CREATE TABLE `question` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '试题ID',
    `type` VARCHAR(20) NOT NULL COMMENT '试题类型: SINGLE-单选, MULTI-多选, JUDGE-判断, ESSAY-简答',
    `title` TEXT NOT NULL COMMENT '题目内容',
    `options` JSON DEFAULT NULL COMMENT '选项(JSON格式): [{"label":"A","content":"选项内容"}]',
    `answer` TEXT NOT NULL COMMENT '正确答案(单选/判断存label, 多选存"AB", 简答存参考答案)',
    `analysis` TEXT DEFAULT NULL COMMENT '答案解析',
    `score` INT DEFAULT 5 COMMENT '默认分值',
    `difficulty` INT DEFAULT 3 COMMENT '难度: 1-简单, 2-较易, 3-中等, 4-较难, 5-困难',
    `teacher_id` BIGINT DEFAULT NULL COMMENT '创建教师ID',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_type` (`type`),
    KEY `idx_teacher_id` (`teacher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试题表';

-- 4. 试卷表
DROP TABLE IF EXISTS `paper`;
CREATE TABLE `paper` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '试卷ID',
    `paper_name` VARCHAR(200) NOT NULL COMMENT '试卷名称',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '试卷描述',
    `duration` INT NOT NULL DEFAULT 120 COMMENT '考试时长(分钟)',
    `total_score` INT NOT NULL DEFAULT 100 COMMENT '试卷总分',
    `pass_score` INT NOT NULL DEFAULT 60 COMMENT '及格分数',
    `status` VARCHAR(20) DEFAULT 'DRAFT' COMMENT '状态: DRAFT-草稿, PUBLISHED-已发布, CLOSED-已关闭',
    `teacher_id` BIGINT DEFAULT NULL COMMENT '创建教师ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_teacher_id` (`teacher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试卷表';

-- 5. 试卷-试题关联表
DROP TABLE IF EXISTS `paper_question`;
CREATE TABLE `paper_question` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    `paper_id` BIGINT NOT NULL COMMENT '试卷ID',
    `question_id` BIGINT NOT NULL COMMENT '试题ID',
    `score` INT DEFAULT 5 COMMENT '该题在试卷中的分值',
    `sort_order` INT DEFAULT 0 COMMENT '排序号',
    PRIMARY KEY (`id`),
    KEY `idx_paper_id` (`paper_id`),
    KEY `idx_question_id` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试卷试题关联表';

-- 6. 考试安排表
DROP TABLE IF EXISTS `exam_arrangement`;
CREATE TABLE `exam_arrangement` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '安排ID',
    `paper_id` BIGINT NOT NULL COMMENT '试卷ID',
    `class_id` BIGINT NOT NULL COMMENT '班级ID',
    `exam_name` VARCHAR(200) DEFAULT NULL COMMENT '考试名称',
    `start_time` DATETIME NOT NULL COMMENT '考试开始时间',
    `end_time` DATETIME NOT NULL COMMENT '考试结束时间',
    `teacher_id` BIGINT DEFAULT NULL COMMENT '安排教师ID',
    `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态: PENDING-未开始, ONGOING-进行中, FINISHED-已结束',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_paper_id` (`paper_id`),
    KEY `idx_class_id` (`class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试安排表';

-- 7. 考试记录表
DROP TABLE IF EXISTS `exam_record`;
CREATE TABLE `exam_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `arrangement_id` BIGINT DEFAULT NULL COMMENT '考试安排ID',
    `paper_id` BIGINT NOT NULL COMMENT '试卷ID',
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `status` VARCHAR(20) DEFAULT 'IN_PROGRESS' COMMENT '状态: IN_PROGRESS-进行中, FINISHED-已完成',
    `start_time` DATETIME DEFAULT NULL COMMENT '开始考试时间',
    `end_time` DATETIME DEFAULT NULL COMMENT '交卷时间',
    `total_score` INT DEFAULT 0 COMMENT '总分',
    `objective_score` INT DEFAULT 0 COMMENT '客观题得分',
    `is_passed` TINYINT DEFAULT 0 COMMENT '是否及格: 0-否, 1-是',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_paper_id` (`paper_id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_arrangement_id` (`arrangement_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试记录表';

-- 8. 答题详情表
DROP TABLE IF EXISTS `answer_detail`;
CREATE TABLE `answer_detail` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '详情ID',
    `exam_record_id` BIGINT NOT NULL COMMENT '考试记录ID',
    `question_id` BIGINT NOT NULL COMMENT '试题ID',
    `student_answer` TEXT DEFAULT NULL COMMENT '学生答案',
    `is_correct` TINYINT DEFAULT 0 COMMENT '是否正确: 0-错误, 1-正确',
    `score` INT DEFAULT 0 COMMENT '得分',
    `marked` TINYINT DEFAULT 0 COMMENT '是否已批阅(简答题): 0-未批阅, 1-已批阅',
    PRIMARY KEY (`id`),
    KEY `idx_exam_record_id` (`exam_record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='答题详情表';

-- ============================================
-- 初始化数据：管理员账号 admin / 123456
-- ============================================
INSERT INTO `user` (`username`, `password`, `real_name`, `role`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', '系统管理员', 'ADMIN', 1),
('teacher01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', '张老师', 'TEACHER', 1),
('student01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', '李同学', 'STUDENT', 1);

INSERT INTO `class` (`class_name`, `grade`, `description`) VALUES
('软件工程2101班', '2021级', '软件工程专业');
