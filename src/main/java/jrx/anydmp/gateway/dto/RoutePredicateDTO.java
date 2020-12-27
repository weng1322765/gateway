package jrx.anydmp.gateway.dto;

import java.io.Serializable;

public class RoutePredicateDTO implements Serializable {
    private static final long serialVersionUID = -1300437459946881345L;

    public static final String PATH ="Path";
    public static final String QUERY = "Query";
    public static final String BEFORE = "Before";
    public static final String AFTER = "After";
    public static final String BETWEEN = "Between";

    private String type;
    private String param;
    private String regexp;
    private String pattern;
    private String startDatetime;
    private String endDatetime;

    public String getEndDatetime() {
        return endDatetime;
    }

    public void setEndDatetime(String endDatetime) {
        this.endDatetime = endDatetime;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getRegexp() {
        return regexp;
    }

    public void setRegexp(String regexp) {
        this.regexp = regexp;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getStartDatetime() {
        return startDatetime;
    }

    public void setStartDatetime(String startDatetime) {
        this.startDatetime = startDatetime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
