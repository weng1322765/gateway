package jrx.anydmp.gateway.servie.impl;


import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import jrx.anydmp.gateway.entity.Degrade;
import jrx.anydmp.gateway.entity.FlowLimit;
import jrx.anydmp.gateway.mapper.DegradeMapper;
import jrx.anydmp.gateway.mapper.FlowLimitMapper;
import jrx.anydmp.gateway.sentinel.SentinelResource;
import jrx.anydmp.gateway.servie.ISentinelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SentinelServiceImpl implements ISentinelService {

    @Value("${spring.application.name}")
    private String serverId;

    @Autowired
    private FlowLimitMapper flowLimitMapper;

    @Autowired
    private DegradeMapper degradeLimitMapper;


    @Autowired
    private RouteDefinitionWriter routeDefinitionWriter;

    @Override
    public void setRule(SentinelResource sentinelResource) {
        if ("flow".equals(sentinelResource.getType())) {
            //限流规则
            FlowRuleManager.loadRules(setFlowRules(sentinelResource));
        } else if ("degrade".equals(sentinelResource.getType())) {
            //降级规则
            DegradeRuleManager.loadRules(setDegradeRules(sentinelResource));
        }
    }



    private List setFlowRules(SentinelResource sentinelResource) {
        FlowRule rule = new FlowRule();
        List<FlowRule> rules = FlowRuleManager.getRules();
        rule.setGrade(sentinelResource.getGrade() == null ? RuleConstant.FLOW_GRADE_QPS : sentinelResource.getGrade());
        rule.setResource(sentinelResource.getResourceId());
        rule.setCount(sentinelResource.getCount());
        rule.setStrategy(sentinelResource.getFlowMode() == null ? RuleConstant.STRATEGY_DIRECT : sentinelResource.getFlowMode());
        rule.setControlBehavior(sentinelResource.getFlowMethod() == null ? RuleConstant.CONTROL_BEHAVIOR_DEFAULT : sentinelResource.getFlowMethod());
        rule.setMaxQueueingTimeMs(sentinelResource.getMaxQueueTime() == null ? 5 * 1000 : sentinelResource.getMaxQueueTime());
        for (FlowRule oldRule : rules) {
            if (oldRule.getResource().equals(rule.getResource())) {
                rules.remove(oldRule);
                break;
            }
        }
        rules.add(rule);
        return rules;
    }

    private List setDegradeRules(SentinelResource sentinelResource) {
        DegradeRule rule = new DegradeRule();
        List<DegradeRule> rules = DegradeRuleManager.getRules();
        rule.setGrade(sentinelResource.getGrade() == null ? RuleConstant.DEGRADE_GRADE_RT : sentinelResource.getGrade());
        rule.setResource(sentinelResource.getResourceId());
        rule.setTimeWindow(sentinelResource.getTime() == null ? 10000 : sentinelResource.getTime());
        rule.setCount(sentinelResource.getCount());
        for (DegradeRule oldRule : rules) {
            if (oldRule.getResource().equals(rule.getResource())) {
                rules.remove(oldRule);
                break;
            }
        }
        rules.add(rule);
        return rules;
    }


    @Override
    public void removeRule(String resourceId, String type) {
        if ("flow".equals(type)) {
            List<FlowRule> flowRules = FlowRuleManager.getRules();
            for (FlowRule flowRule : flowRules) {
                if (resourceId.equals(flowRule.getResource())) {
                    flowRules.remove(flowRule);
                    break;
                }
            }
            FlowRuleManager.loadRules(flowRules);
        } else if ("degrade".equals(type)) {
            List<DegradeRule> degradeRules = DegradeRuleManager.getRules();
            for (DegradeRule degradeRule : degradeRules) {
                if (resourceId.equals(degradeRule.getResource())) {
                    degradeRules.remove(degradeRule);
                    break;
                }
            }
            DegradeRuleManager.loadRules(degradeRules);
        }
    }


    @Override
    public void loadOnStartup() {
        List<FlowLimit> routeLimitList = flowLimitMapper.selectByServerIdAndStatus(serverId, 1);
        List<Degrade> routeDegradeList = degradeLimitMapper.selectByServerIdAndStatus(serverId, 1);
        if (routeLimitList != null && routeLimitList.size() > 0) {
            setRouteLimit(routeLimitList);
        }
        if (routeDegradeList != null && routeDegradeList.size() > 0) {
            setRouteDegrade(routeDegradeList);
        }
    }

    private void setRouteLimit(List<FlowLimit> routeLimitList) {
        List<FlowRule> flowRuleList = FlowRuleManager.getRules();
        if (flowRuleList == null) {
            flowRuleList = new ArrayList<>();
        }
        for (FlowLimit flowLimit : routeLimitList) {
            FlowRule flowRule = new FlowRule();
            if (flowLimit.getRouteId() != null) {
                flowRule.setResource(String.valueOf(flowLimit.getRouteId()));
            } else {
                flowRule.setResource(flowLimit.getUrl());
            }
            flowRule.setGrade(flowLimit.getThresholdType() == null ? RuleConstant.FLOW_GRADE_QPS : flowLimit.getThresholdType());
            flowRule.setCount(flowLimit.getSingleThreshold() == null ? 0 : flowLimit.getSingleThreshold());
            flowRule.setControlBehavior(flowLimit.getFlowControlMethod() == null ? RuleConstant.CONTROL_BEHAVIOR_DEFAULT : flowLimit.getFlowControlMethod());
            flowRule.setStrategy(flowLimit.getFlowControlMode() == null ? RuleConstant.STRATEGY_DIRECT : flowLimit.getFlowControlMode());
            flowRule.setMaxQueueingTimeMs(flowLimit.getMaxQueueTime());
            flowRuleList.add(flowRule);
        }
        //去重操作
        flowRuleList = distinctFlowList(flowRuleList);
        FlowRuleManager.loadRules(flowRuleList);
    }

    private void setRouteDegrade(List<Degrade> routeDegradeList) {
        List<DegradeRule> degradeRuleList = DegradeRuleManager.getRules();
        if (degradeRuleList == null) {
            degradeRuleList = new ArrayList<>();
        }
        for (Degrade degrade : routeDegradeList) {
            DegradeRule degradeRule = new DegradeRule();
            if (degrade.getRouteId() != null) {
                degradeRule.setResource(String.valueOf(degrade.getRouteId()));
            } else {
                degradeRule.setResource(degrade.getUrl());
            }
            degradeRule.setGrade(degrade.getThresholdType() == null ? RuleConstant.DEGRADE_GRADE_RT : degrade.getThresholdType());
            degradeRule.setCount(degrade.getThresholdValue() == null ? 0 : degrade.getThresholdValue());
            degradeRule.setTimeWindow(degrade.getPeriod() == null ? 1000 : degrade.getPeriod());
            degradeRuleList.add(degradeRule);
        }
        //去重
        degradeRuleList = distinctDegradeList(degradeRuleList);
        DegradeRuleManager.loadRules(degradeRuleList);
    }


    private List<FlowRule> distinctFlowList(List<FlowRule> rules) {
        List<FlowRule> newList = new ArrayList<>();
        boolean flag = false;
        for (int i = rules.size() - 1; i >= 0; i--) {
            final FlowRule flowRule = rules.get(i);
            flag = newList.stream().anyMatch(r -> r.getResource().equals(flowRule.getResource()));
            if (!flag) {
                newList.add(flowRule);
            }
        }
        return newList;
    }

    private List<DegradeRule> distinctDegradeList(List<DegradeRule> rules) {
        List<DegradeRule> newList = new ArrayList<>();
        boolean flag = false;
        for (int i = rules.size() - 1; i >= 0; i--) {
            final DegradeRule degradeRule = rules.get(i);
            flag = newList.stream().anyMatch(r -> r.getResource().equals(degradeRule.getResource()));
            if (!flag) {
                newList.add(degradeRule);
            }
        }
        return newList;
    }

}
