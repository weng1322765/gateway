package jrx.anydmp.gateway.servie.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import jrx.anydmp.gateway.entity.RouteInfo;
import jrx.anydmp.gateway.mapper.RouteInfoMapper;
import jrx.anydmp.gateway.dto.RouteInfoDTO;
import jrx.anydmp.gateway.dto.RoutePredicateDTO;
import jrx.anydmp.gateway.servie.IRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.*;

/**
 * 路由服务实现类
 *
 * @author zwg
 * @date 2018-09-20 17:59
 **/
@Service
public class RouteServiceImpl implements IRouteService {

    @Value("${spring.application.name}")
    private String serverId;

    @Autowired
    private RouteDefinitionWriter routeDefinitionWriter;

    @Autowired
    private RouteInfoMapper routeInfoMapper;

    /**
     * 添加路由
     *
     * @return
     */
    @Override
    public String addRoute(RouteInfo routeInfo) {
        handleRouteInfo(routeInfo);
        return "success";
    }


    @Override
    public String delRoute(String routeId) {
        routeDefinitionWriter.delete(Mono.just(routeId)).subscribe();
        return "success";
    }

    @Override
    public void loadOnStartup() {
        List<RouteInfo> routeList = routeInfoMapper.selectByServerIdAndStatus(serverId, 1);
        if (routeList != null && routeList.size() > 0) {
            for (RouteInfo routeInfo : routeList) {
                handleRouteInfo(routeInfo);
            }
        }
    }


    private void handleRouteInfo (RouteInfo routeInfo) {
        RouteDefinition definition = new RouteDefinition();
        definition.setId(String.valueOf(routeInfo.getId()));
        URI uri = UriComponentsBuilder.fromUriString(routeInfo.getRouteUrl()).build().toUri();
        definition.setUri(uri);
        JSONArray routeRules = JSON.parseArray(routeInfo.getRules());

        List<PredicateDefinition> predicateDefinitionList = new ArrayList<>();
        for (int i = 0; i < routeRules.size(); i++) {
            RoutePredicateDTO routePredicate = JSON.parseObject(JSON.toJSONString(routeRules.get(i)), RoutePredicateDTO.class);
            if (RoutePredicateDTO.PATH.equals(routePredicate.getType())) {
                predicateDefinitionList.add(setPathPredicate(routePredicate));
            } else if (RoutePredicateDTO.QUERY.equals(routePredicate.getType())) {
                predicateDefinitionList.add(setQueryPredicate(routePredicate));
            } else if (RoutePredicateDTO.BEFORE.equals(routePredicate.getType())) {
                predicateDefinitionList.add(setBeforePredicate(routePredicate));
            } else if (RoutePredicateDTO.AFTER.equals(routePredicate.getType())) {
                predicateDefinitionList.add(setAfterPredicate(routePredicate));
            } else if (RoutePredicateDTO.BETWEEN.equals(routePredicate.getType())) {
                predicateDefinitionList.add(setBetweenPredicate(routePredicate));
            }
        }
        definition.setPredicates(predicateDefinitionList);
        definition.setFilters(Arrays.asList(setSentinelFilter(definition.getId())));
        routeDefinitionWriter.save(Mono.just(definition)).subscribe();
    }

    private PredicateDefinition setPathPredicate(RoutePredicateDTO routePredicate) {
        PredicateDefinition predicate = new PredicateDefinition();
        Map<String, String> predicateParams = new HashMap<>(8);
        predicate.setName(routePredicate.getType());
        predicateParams.put("pattern", routePredicate.getPattern());
        predicate.setArgs(predicateParams);
        return predicate;
    }

    private PredicateDefinition setQueryPredicate(RoutePredicateDTO routePredicate) {
        PredicateDefinition predicate = new PredicateDefinition();
        Map<String, String> predicateParams = new HashMap<>(8);
        predicate.setName(routePredicate.getType());
        predicateParams.put("param", routePredicate.getParam());
        predicateParams.put("regexp", routePredicate.getRegexp());
        predicate.setArgs(predicateParams);
        return predicate;
    }

    private PredicateDefinition setBeforePredicate(RoutePredicateDTO routePredicate) {
        PredicateDefinition predicate = new PredicateDefinition();
        Map<String, String> predicateParams = new HashMap<>(8);
        predicate.setName(routePredicate.getType());
        predicateParams.put("datetime", routePredicate.getEndDatetime());
        predicate.setArgs(predicateParams);
        return predicate;
    }

    private PredicateDefinition setAfterPredicate(RoutePredicateDTO routePredicate) {
        PredicateDefinition predicate = new PredicateDefinition();
        Map<String, String> predicateParams = new HashMap<>(8);
        predicate.setName(routePredicate.getType());
        predicateParams.put("datetime", routePredicate.getStartDatetime());
        predicate.setArgs(predicateParams);
        return predicate;
    }

    private PredicateDefinition setBetweenPredicate(RoutePredicateDTO routePredicate) {
        PredicateDefinition predicate = new PredicateDefinition();
        Map<String, String> predicateParams = new HashMap<>(8);
        predicate.setName(routePredicate.getType());
        predicateParams.put("datetime1", routePredicate.getStartDatetime());
        predicateParams.put("datetime2", routePredicate.getEndDatetime());
        predicate.setArgs(predicateParams);
        return predicate;
    }

    private FilterDefinition setSentinelFilter(String routeId) {
        FilterDefinition filterDefinition = new FilterDefinition();
        Map<String, String> filterParams = new HashMap<>(8);
        filterDefinition.setName("SentinelFilter");
        filterParams.put("resource", routeId);
        filterDefinition.setArgs(filterParams);
        return filterDefinition;
    }


    @Override
    public List<RouteInfoDTO> queryRouteListOnline(String url) {
        List<RouteInfoDTO> routeInfoList = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(url, String.class);
        JSONArray jsonArray = JSON.parseArray(result);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            RouteInfoDTO routeInfo = new RouteInfoDTO();
            routeInfo.setServerId(serverId);
            routeInfo.setId(jsonObject.getString("route_id"));
            routeInfo.setRouteUrl(jsonObject.getJSONObject("route_definition").getString("uri"));
            JSONArray predicates = jsonObject.getJSONObject("route_definition").getJSONArray("predicates");
            routeInfo.setRules(formatRules(predicates));
            routeInfoList.add(routeInfo);
        }
        return routeInfoList;
    }

    private String formatRules(JSONArray predicates) {
        List<RoutePredicateDTO> predicateList = new ArrayList<>();
        for (int j = 0; j < predicates.size(); j++) {
            JSONObject predicate = predicates.getJSONObject(j);
            RoutePredicateDTO predicateDTO = new RoutePredicateDTO();
            predicateDTO.setType(predicate.getString("name"));
            if (RoutePredicateDTO.PATH.equals(predicateDTO.getType())) {
                predicateDTO.setPattern(predicate.getJSONObject("args").getString("pattern"));
            } else if (RoutePredicateDTO.QUERY.equals(predicateDTO.getType())) {
                predicateDTO.setParam(predicate.getJSONObject("args").getString("param"));
                predicateDTO.setRegexp(predicate.getJSONObject("args").getString("regexp"));
            } else if (RoutePredicateDTO.BEFORE.equals(predicateDTO.getType())) {
                predicateDTO.setEndDatetime(predicate.getJSONObject("args").getString("datetime"));
            } else if (RoutePredicateDTO.AFTER.equals(predicateDTO.getType())) {
                predicateDTO.setStartDatetime(predicate.getJSONObject("args").getString("datetime"));
            } else if (RoutePredicateDTO.BETWEEN.equals(predicateDTO.getType())) {
                predicateDTO.setStartDatetime(predicate.getJSONObject("args").getString("datetime1"));
                predicateDTO.setEndDatetime(predicate.getJSONObject("args").getString("datetime2"));
            }
            predicateList.add(predicateDTO);
        }
        return JSON.toJSONString(predicateList);
    }


    @Override
    public RouteInfoDTO queryRouteInfoById(String url) {
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(url, String.class);
        JSONObject jsonObject = JSON.parseObject(result);

        RouteInfoDTO routeInfo = new RouteInfoDTO();
        routeInfo.setServerId(serverId);
        routeInfo.setId(jsonObject.getString("route_id"));
        routeInfo.setRouteUrl(jsonObject.getString("uri"));
        JSONArray predicates = jsonObject.getJSONArray("predicates");
        routeInfo.setRules(formatRules(predicates));
        return routeInfo;

    }

}
