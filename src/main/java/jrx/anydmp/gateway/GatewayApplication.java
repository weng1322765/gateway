package jrx.anydmp.gateway;


import jrx.anytxn.sadmin.client.config.EnableAdminClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 服务网关启动类
 *
 * @author zwg
 * @date 2018-03-06 15:48
 */
@EnableDiscoveryClient
@MapperScan("jrx.anydmp.gateway.mapper")
@SpringBootApplication
@EnableAdminClient
public class GatewayApplication {

    public static void main(String[] args) {
        new SpringApplication(GatewayApplication.class).run(args);
    }




}
