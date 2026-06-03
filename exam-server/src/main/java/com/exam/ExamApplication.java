package com.exam;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.exam.mapper")
public class ExamApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExamApplication.class, args);
        System.out.println("========================================");
        System.out.println("  在线考试系统启动成功！");
        System.out.println("  API文档: http://localhost:8080/doc.html");
        System.out.println("========================================");
    }
}
