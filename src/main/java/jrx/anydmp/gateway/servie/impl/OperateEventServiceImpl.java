package jrx.anydmp.gateway.servie.impl;

import jrx.anydmp.gateway.entity.Degrade;
import jrx.anydmp.gateway.entity.FlowLimit;
import jrx.anydmp.gateway.entity.OperateEvent;
import jrx.anydmp.gateway.entity.RouteInfo;
import jrx.anydmp.gateway.mapper.DegradeMapper;
import jrx.anydmp.gateway.mapper.FlowLimitMapper;
import jrx.anydmp.gateway.mapper.OperateEventMapper;
import jrx.anydmp.gateway.mapper.RouteInfoMapper;
import jrx.anydmp.gateway.common.constant.GatewayConstant;
import jrx.anydmp.gateway.common.enums.Status;
import jrx.anydmp.gateway.sentinel.SentinelResource;
import jrx.anydmp.gateway.servie.IOperateEventService;
import jrx.anydmp.gateway.servie.IRouteService;
import jrx.anydmp.gateway.servie.ISentinelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class OperateEventServiceImpl implements IOperateEventService {

    private final Logger logger = LoggerFactory.getLogger(OperateEventServiceImpl.class);

    @Autowired
    private OperateEventMapper operateEventMapper;

    @Autowired
    private RouteInfoMapper routeInfoMapper;

    @Autowired
    private FlowLimitMapper flowLimitMapper;

    @Autowired
    private DegradeMapper degradeMapper;

    @Autowired
    private ISentinelService sentinelService;

    @Autowired
    private IRouteService routeService;

    @Autowired
    private RouteDefinitionLocator routeDefinitionLocator;

    @Autowired
    private RouteDefinitionWriter routeDefinitionWriter;



    @Override
    public String syncOperateEvent(Integer id) {

        OperateEvent operateEvent = operateEventMapper.selectById(id);
        if (Status.DISABLE.getCode() != operateEvent.getStatus()) {
            return null;
        }
        if (GatewayConstant.ROUTE.equals(operateEvent.getType())) {
            RouteInfo routeInfo = routeInfoMapper.selectById(operateEvent.getObjectId());
            return operateRouteInfo(routeInfo, operateEvent.getOperateType());
        }
        if (GatewayConstant.FLOW.equals(operateEvent.getType())) {
            FlowLimit flowLimit = flowLimitMapper.selectById(operateEvent.getObjectId());
            return operateFlowLimit(flowLimit, operateEvent.getOperateType());
        }
        if (GatewayConstant.DEGRADE.equals(operateEvent.getType())) {
            Degrade degrade = degradeMapper.selectById(operateEvent.getObjectId());
            return operateDegrade(degrade, operateEvent.getOperateType());
        }
        return null;
    }

    private String operateRouteInfo(RouteInfo routeInfo, String operateType) {
        if (GatewayConstant.NEW.equals(operateType) || GatewayConstant.UPDATE.equals(operateType)) {
            routeService.addRoute(routeInfo);
        } else if (GatewayConstant.DELETE.equals(operateType)) {
            this.routeDefinitionWriter.delete(Mono.just(String.valueOf(routeInfo.getId())))
                    .then(Mono.defer(() -> Mono.just("success")))
                    .onErrorResume((t) -> t instanceof NotFoundException, (t) -> {
                        logger.warn("该路由id{}不存在或已被删除！", routeInfo.getId());
                        return Mono.just("failed");
                    }).subscribe();
        }
        return null;
    }

    private String operateFlowLimit(FlowLimit flowLimit, String operateType) {
        SentinelResource sentinelResource = new SentinelResource();
        sentinelResource.setResourceId(flowLimit.getRouteId() == null ? flowLimit.getUrl() : String.valueOf(flowLimit.getRouteId()));
        if (GatewayConstant.NEW.equals(operateType) || GatewayConstant.UPDATE.equals(operateType)) {
            sentinelResource.setCount((double) flowLimit.getSingleThreshold());
            sentinelResource.setGrade(flowLimit.getThresholdType());
            sentinelResource.setFlowMethod(flowLimit.getFlowControlMethod());
            sentinelResource.setFlowMode(flowLimit.getFlowControlMode());
            sentinelResource.setType(GatewayConstant.FLOW);
            sentinelService.setRule(sentinelResource);
        } else if (GatewayConstant.DELETE.equals(operateType)) {
            sentinelService.removeRule(sentinelResource.getResourceId(), GatewayConstant.FLOW);
        }
        return null;
    }

    private String operateDegrade(Degrade degrade, String operateType) {
        SentinelResource sentinelResource = new SentinelResource();
        sentinelResource.setResourceId(degrade.getRouteId() == null ? degrade.getUrl() : String.valueOf(degrade.getRouteId()));
        if (GatewayConstant.NEW.equals(operateType) || GatewayConstant.UPDATE.equals(operateType)) {
            sentinelResource.setResourceId(degrade.getRouteId() == null ? degrade.getUrl() : String.valueOf(degrade.getRouteId()));
            sentinelResource.setGrade(degrade.getThresholdType());
            sentinelResource.setCount(degrade.getThresholdValue());
            sentinelResource.setTime(degrade.getPeriod());
            sentinelResource.setType(GatewayConstant.DEGRADE);
            sentinelService.setRule(sentinelResource);
        } else if (GatewayConstant.DELETE.equals(operateType)) {
            sentinelService.removeRule(sentinelResource.getResourceId(), GatewayConstant.DEGRADE);
        }
        return null;
    }


    @Override
    public void updateStatusById(Integer id, Integer status) {
        operateEventMapper.updateStatusById(id, Status.ENABLE.getCode());
    }
}
