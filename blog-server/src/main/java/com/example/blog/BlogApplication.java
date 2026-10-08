package com.example.blog;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
@MapperScan("com.example.blog.mapper")
public class BlogApplication {
    public static void main(String[] args) { SpringApplication.run(BlogApplication.class, args); }
}
