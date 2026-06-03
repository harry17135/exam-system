-- H2 数据库建表脚本 (MySQL 兼容模式)
CREATE TABLE IF NOT EXISTS `class` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `class_name` VARCHAR(100) NOT NULL,
    `grade` VARCHAR(50) DEFAULT NULL,
    `description` VARCHAR(500) DEFAULT NULL,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `real_name` VARCHAR(50) NOT NULL,
    `role` VARCHAR(20) NOT NULL DEFAULT 'STUDENT',
    `phone` VARCHAR(20) DEFAULT NULL,
    `email` VARCHAR(100) DEFAULT NULL,
    `class_id` BIGINT DEFAULT NULL,
    `avatar` VARCHAR(255) DEFAULT NULL,
    `status` TINYINT DEFAULT 1,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
);

CREATE TABLE IF NOT EXISTS `question` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `type` VARCHAR(20) NOT NULL,
    `title` CLOB NOT NULL,
    `options` CLOB DEFAULT NULL,
    `answer` CLOB NOT NULL,
    `analysis` CLOB DEFAULT NULL,
    `score` INT DEFAULT 5,
    `difficulty` INT DEFAULT 3,
    `teacher_id` BIGINT DEFAULT NULL,
    `status` TINYINT DEFAULT 1,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `paper` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `paper_name` VARCHAR(200) NOT NULL,
    `description` VARCHAR(500) DEFAULT NULL,
    `duration` INT NOT NULL DEFAULT 120,
    `total_score` INT NOT NULL DEFAULT 100,
    `pass_score` INT NOT NULL DEFAULT 60,
    `status` VARCHAR(20) DEFAULT 'DRAFT',
    `teacher_id` BIGINT DEFAULT NULL,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `paper_question` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `paper_id` BIGINT NOT NULL,
    `question_id` BIGINT NOT NULL,
    `score` INT DEFAULT 5,
    `sort_order` INT DEFAULT 0,
    PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `exam_arrangement` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `paper_id` BIGINT NOT NULL,
    `class_id` BIGINT NOT NULL,
    `exam_name` VARCHAR(200) DEFAULT NULL,
    `start_time` DATETIME NOT NULL,
    `end_time` DATETIME NOT NULL,
    `teacher_id` BIGINT DEFAULT NULL,
    `status` VARCHAR(20) DEFAULT 'PENDING',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `exam_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `arrangement_id` BIGINT DEFAULT NULL,
    `paper_id` BIGINT NOT NULL,
    `student_id` BIGINT NOT NULL,
    `status` VARCHAR(20) DEFAULT 'IN_PROGRESS',
    `start_time` DATETIME DEFAULT NULL,
    `end_time` DATETIME DEFAULT NULL,
    `total_score` INT DEFAULT 0,
    `objective_score` INT DEFAULT 0,
    `is_passed` TINYINT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `answer_detail` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `exam_record_id` BIGINT NOT NULL,
    `question_id` BIGINT NOT NULL,
    `student_answer` CLOB DEFAULT NULL,
    `is_correct` TINYINT DEFAULT 0,
    `score` INT DEFAULT 0,
    `marked` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`)
);
