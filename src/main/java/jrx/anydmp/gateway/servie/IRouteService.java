package jrx.anydmp.gateway.servie;

import jrx.anydmp.gateway.entity.RouteInfo;
import jrx.anydmp.gateway.dto.RouteInfoDTO;

import java.util.List;

/**
 * 路由服务类
 *
 * @author zwg
 * @date 2018-09-20 17:50
 **/
public interface IRouteService {



    /**
     * 添加路由
     * @return
     */
    String addRoute(RouteInfo routeInfo);


    /**
     * 删除路由
     */
    String delRoute(String routeId);

    /**
     * 启动时加载配置
     */
    void loadOnStartup() throws Exception;

    /**
     * 查询正在使用中的路由配置
     * @return
     */
    List<RouteInfoDTO> queryRouteListOnline(String url);


    /**
     * 按照id查询路由配置
     * @param url
     * @return
     */
    RouteInfoDTO queryRouteInfoById(String url);


}
