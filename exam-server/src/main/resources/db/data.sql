-- 初始数据：密码均为 123456（BCrypt 加密）
-- 注：密码哈希由 Spring Boot DataInitializer 动态生成，此处插入基础数据
INSERT INTO `user` (`username`, `password`, `real_name`, `role`, `status`) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '系统管理员', 'ADMIN', 1),
('teacher01', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '张老师', 'TEACHER', 1),
('student01', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '李同学', 'STUDENT', 1);

INSERT INTO `class` (`class_name`, `grade`, `description`) VALUES
('软件工程2101班', '2021级', '软件工程专业');

-- 示例试题
INSERT INTO `question` (`type`, `title`, `options`, `answer`, `analysis`, `score`, `difficulty`, `teacher_id`, `status`) VALUES
('SINGLE', 'Java中，以下哪个关键字用于实现接口？', '[{"label":"A","content":"extends"},{"label":"B","content":"implements"},{"label":"C","content":"abstract"},{"label":"D","content":"interface"}]', 'B', 'implements关键字用于类实现接口，extends用于继承类，abstract用于抽象类，interface用于定义接口。', 5, 2, 2, 1),
('SINGLE', 'HTTP协议默认使用哪个端口？', '[{"label":"A","content":"21"},{"label":"B","content":"80"},{"label":"C","content":"443"},{"label":"D","content":"8080"}]', 'B', 'HTTP默认端口80，HTTPS默认443，FTP默认21。', 5, 1, 2, 1),
('MULTI', '以下哪些是Java的基本数据类型？', '[{"label":"A","content":"int"},{"label":"B","content":"String"},{"label":"C","content":"boolean"},{"label":"D","content":"float"}]', 'ACD', 'String是引用类型，不是基本数据类型。Java的8种基本类型：byte、short、int、long、float、double、char、boolean。', 10, 2, 2, 1),
('JUDGE', 'Spring Boot 默认使用 Tomcat 作为内嵌服务器。', NULL, 'A', 'Spring Boot Starter Web 默认内嵌 Tomcat 服务器，也可以通过排除依赖改用 Jetty 或 Undertow。', 5, 1, 2, 1),
('ESSAY', '请简述 MVC 设计模式的基本思想及各层职责。', NULL, 'MVC即Model-View-Controller。Model负责数据和业务逻辑，View负责界面展示，Controller负责接收用户请求并调用Model处理后返回View。', 'MVC将应用分为模型、视图、控制器三层，实现了关注点分离。', 10, 3, 2, 1);
