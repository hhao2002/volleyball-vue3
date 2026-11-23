package cn.edu.hnzj.volleyball;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.edu.hnzj.volleyball.mapper")
public class VolleyballApplication {

    public static void main(String[] args) {
        SpringApplication.run(VolleyballApplication.class, args);
    }
}
