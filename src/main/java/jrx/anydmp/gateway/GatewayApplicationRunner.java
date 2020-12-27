package jrx.anydmp.gateway;

import jrx.anydmp.gateway.servie.IRouteService;
import jrx.anydmp.gateway.servie.ISentinelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class GatewayApplicationRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(GatewayApplicationRunner.class);

    @Autowired
    private ISentinelService sentinelService;

    @Autowired
    private IRouteService routeService;

    @Override
    public void run(ApplicationArguments args) {
        logger.info("============配置初始化加载开始============");
        try {
            routeService.loadOnStartup();
            sentinelService.loadOnStartup();
        } catch (Exception e) {
            logger.error("============配置初始化加载失败============", e);
        }
        logger.info("============配置初始化加载结束============");
    }

}
