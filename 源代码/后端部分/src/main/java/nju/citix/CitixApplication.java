package nju.citix;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author DW
 */
@SpringBootApplication
@MapperScan("nju.citix.dao")
@EnableScheduling
public class CitixApplication {

    public static void main(String[] args) {
        SpringApplication.run(CitixApplication.class, args);
    }

}
