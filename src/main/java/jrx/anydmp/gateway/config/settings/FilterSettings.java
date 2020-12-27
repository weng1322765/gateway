package jrx.anydmp.gateway.config.settings;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @author peidong.meng
 */
@Configuration
@ConfigurationProperties(prefix = "filter.setting")
public class FilterSettings {

    /**
     * 鉴权模式：
     *  0-无鉴权模式(默认用户可配置)
     *  1-仅简单登陆模式(用户可配置),未实现。
     *  其他-鉴权模式
     */
    private Integer authModel = -1;
    /**
     * 模式0/1时默认用户信息
     * 格式(账号-id-昵称)
     */
    private String user = "admin-0-默认用户";
    /**
     * 模式0/1时默认业务群信息
     * 格式(名称-id,名称-id)
     */
    private String project = "默认业务群-0";
    /**
     * 模式0/1时默认资源路径，格式参考server_resource.md文件
     */
    private String resourceFile = "/server_resource.md";

    /**
     * 综管url:port
     */
    private String authAddress;

    /**
     * 综管中心登陆地址
     */
    private String authLoginUrl;
    /**
     * 综管中心获取用户信息地址
     */
    private String authUserUrl;
    /**
     * 综管中心获取业务群信息地址
     */
    private String projectsUrl;
    /**
     * 综管中心获取资源目录信息地址
     */
    private String authResourceUrl;
    /**
     * 综管中心获取内容目录信息地址
     */
    private String authContentUrl;
    /**
     * 综管中心获取浏览范围信息地址
     */
    private String authViewScopeUrl;
    /**
     * 综管中心切换业务群地址
     */
    private String changeProjectUrl;
    /**
     * 综管中心鉴权地址
     */
    private String authUrl;
    /**
     * 登陆异常url
     */
    private String loginErrorUrl;
    /**
     * 无权url
     */
    private String noAuthUrl;
    /**
     * 鉴权异常url
     */
    private String authErrorUrl;
    /**
     * 服务回调地址(首页)
     */
    private String indexUrl;
    /**
     * 服务前缀(存储信息前缀使用，放置冲突)
     */
    private String servicePre;
    /**
     * 服务id(综管提供，用于解密)
     */
//    private String serviceId;
    /**
     * 服务端口号
     */
    private String localUrl;
    /**
     * 超时时长 默认半小时
     */
    private Integer timeout = 1800;

    /**
     * 超时时长 默认半小时
     */
    private Boolean hadAuth = false;

    /**
     * 不拦截url
     */
    private List<String> allows = new ArrayList<>();

    @PostConstruct
    public void init(){

        if(hadAuth){
            allows.add("/mc/**");
            allows.add("/api/**");
        }
        //忽略静态文件
        allows.add("/*.ico");
        allows.add("/**/*.js");
        allows.add("/**/*.css");
        allows.add("/**/js/**");
        allows.add("/**/font/**");
        allows.add("/**/css/**");
        allows.add("/**/img/**");
        allows.add("/**/*.html");
        //忽略拦截器中一些公共接口功能
        allows.add("/login");
        allows.add("/logout");
        allows.add("/change/project");
        allows.add("/auth/model");
        allows.add("/navbar");
        allows.add("/userInfo");
        allows.add("/project/all");
        allows.add("/change/project");

    }

    public Integer getAuthModel() {
        return authModel;
    }

    public void setAuthModel(Integer authModel) {
        this.authModel = authModel;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getResourceFile() {
        return resourceFile;
    }

    public void setResourceFile(String resourceFile) {
        this.resourceFile = resourceFile;
    }

    public List<String> getAllows() {
        return allows;
    }

    public void setAllows(List<String> allows) {
        this.allows = allows;
    }

    public String getAuthLoginUrl() {
        if(StringUtils.isEmpty(authLoginUrl)){
            return getAuthAddress() + "/mc/page/login.html";
        }
        return authLoginUrl;
    }

    public void setAuthLoginUrl(String authLoginUrl) {
        this.authLoginUrl = authLoginUrl;
    }

    public String getIndexUrl() {
        return indexUrl;
    }

    public void setIndexUrl(String indexUrl) {
        this.indexUrl = indexUrl;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public String getLocalUrl() {
        return localUrl;
    }

    public void setLocalUrl(String localUrl) {
        this.localUrl = localUrl;
    }

    public String getServicePre() {
        if(StringUtils.isEmpty(servicePre)){
            return "";
        }
        return servicePre;
    }

    public void setServicePre(String servicePre) {
        this.servicePre = servicePre;
    }

//    public String getServiceId() {
//        return serviceId;
//    }
//
//    public void setServiceId(String serviceId) {
//        this.serviceId = serviceId;
//    }

    public String getLoginErrorUrl() {
        return loginErrorUrl;
    }

    public void setLoginErrorUrl(String loginErrorUrl) {
        this.loginErrorUrl = loginErrorUrl;
    }

    public String getNoAuthUrl() {
        return noAuthUrl;
    }

    public void setNoAuthUrl(String noAuthUrl) {
        this.noAuthUrl = noAuthUrl;
    }

    public String getAuthErrorUrl() {
        return authErrorUrl;
    }

    public void setAuthErrorUrl(String authErrorUrl) {
        this.authErrorUrl = authErrorUrl;
    }

    public String getAuthUserUrl() {
        if(StringUtils.isEmpty(authUserUrl)){
            return getAuthAddress() + "/api/user/info";
        }
        return authUserUrl;
    }

    public void setAuthUserUrl(String authUserUrl) {
        this.authUserUrl = authUserUrl;
    }

    public String getAuthResourceUrl() {
        if(StringUtils.isEmpty(authResourceUrl)){
            return getAuthAddress() + "/api/user/resource";
        }
        return authResourceUrl;
    }

    public void setAuthResourceUrl(String authResourceUrl) {
        this.authResourceUrl = authResourceUrl;
    }

    public String getAuthContentUrl() {
        if(StringUtils.isEmpty(authContentUrl)){
            return getAuthAddress() + "/api/user/content2";
        }
        return authContentUrl;
    }

    public void setAuthContentUrl(String authContentUrl) {
        this.authContentUrl = authContentUrl;
    }

    public String getAuthUrl() {
        if(StringUtils.isEmpty(authUrl)){
            return getAuthAddress() + "/api/auth/valid";
        }
        return authUrl;
    }

    public void setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
    }

    public String getAuthViewScopeUrl() {
        if(StringUtils.isEmpty(authViewScopeUrl)){
            return getAuthAddress() + "/api/user/viewScope";
        }
        return authViewScopeUrl;
    }

    public void setAuthViewScopeUrl(String authViewScopeUrl) {
        this.authViewScopeUrl = authViewScopeUrl;
    }

    public String getProjectsUrl() {
        if(StringUtils.isEmpty(projectsUrl)){
            return getAuthAddress() + "/api/user/projects";
        }
        return projectsUrl;
    }

    public void setProjectsUrl(String projectsUrl) {
        this.projectsUrl = projectsUrl;
    }

    public String getChangeProjectUrl() {
        if(StringUtils.isEmpty(changeProjectUrl)){
            return getAuthAddress() + "/api/user/refresh/project";
        }
        return changeProjectUrl;
    }

    public void setChangeProjectUrl(String changeProjectUrl) {
        this.changeProjectUrl = changeProjectUrl;
    }

    public String getAuthAddress() {
        return authAddress;
    }

    public void setAuthAddress(String authAddress) {
        this.authAddress = authAddress;
    }

    public Boolean getHadAuth() {
        return hadAuth;
    }

    public void setHadAuth(Boolean hadAuth) {
        this.hadAuth = hadAuth;
    }
}
