package com.example.shiro.config;

import com.example.shiro.filter.MyFormAuthenticationFilter;
import com.example.shiro.test.FirstRealm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(DefaultWebSecurityManager defaultWebSecurityManager){
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(defaultWebSecurityManager);
        Map<String,String> map = new HashMap<>();
        map.put("/user/login","anon");//表示该资源无需认证授权,无需授权的应该写在上面
//        map.put("/user/logout","anon");//表示该资源无需认证授权
        map.put("/login.jsp","anon");//表示该资源无需认证授权
//        map.put("/test/test1","anon");//表示该资源无需认证授权

        map.put("/**","authc");//表示所有资源都需要经过认证授权
        shiroFilterFactoryBean.setFilterChainDefinitionMap(map);

        //设置授权失败返回的页面
//        shiroFilterFactoryBean.setLoginUrl("login.jsp");//这也是默认值

        LinkedHashMap<String, Filter> map2 = new LinkedHashMap<>();
        // 2、将自定义过滤器放入map中，如果实现了自定义授权过滤器，那就必须在这里注册，否则Shiro不会使用自定义的授权过滤器
        map2.put("authc", new MyFormAuthenticationFilter());
        // 3、将过滤器Ma绑定到shiroFilterFactoryBean上
        shiroFilterFactoryBean.setFilters(map2);
        return shiroFilterFactoryBean;
    }

    @Bean
    public DefaultWebSecurityManager getDefaultWebSecurityManager(FirstRealm firstReaml){
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        defaultWebSecurityManager.setRealm(firstReaml);
        return defaultWebSecurityManager;
    }

    @Bean
    public FirstRealm getRealm(){
        FirstRealm firstRealm = new FirstRealm();
        // 不设置的话就直接比对原始的密码
//        Md5CredentialsMatcher md5CredentialsMatcher = new Md5CredentialsMatcher();
//        md5CredentialsMatcher.setHashIterations(1024);
//        firstRealm.setCredentialsMatcher(md5CredentialsMatcher);
        return firstRealm;
    }

}

