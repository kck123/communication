package com.cisco.oss.foundation.http.server;

import com.cisco.oss.foundation.configuration.ConfigurationFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.SimpleServletHandlerAdapter;
import org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter;

/**
 * Created by Yair Ogen (yaogen) on 08/06/2016.
 */

@Configuration
public class ServletConfiguration {

//    @Value("${spring.application.name}")
//    private String serviceName = null;
//
//    @ConditionalOnExpression("#{'${${spring.application.name}.http.pingFilter.isEnabled}' != null && '${${spring.application.name}.http.pingFilter.isEnabled}'.toLowerCase().equals('false')}")
//    @Bean
//    public SimpleServletHandlerAdapter getSimpleServletHandlerAdapter(){
//        return new SimpleServletHandlerAdapter();
//    }

    @ConditionalOnExpression("#{'${${spring.application.name}.http.pingFilter.isEnabled}' != null && '${${spring.application.name}.http.pingFilter.isEnabled}'.toLowerCase().equals('false')}")
    @Bean (name="probe")
    public PingServlet probe(){
        return new PingServlet();
    }

    @ConditionalOnExpression("#{'${${spring.application.name}.http.pingFilter.isEnabled}' != null && '${${spring.application.name}.http.pingFilter.isEnabled}'.toLowerCase().equals('false')}")
    @Bean
    public ServletRegistrationBean dispatcherRegistration(PingServlet pingServlet) {
        ServletRegistrationBean registration = new ServletRegistrationBean(
                pingServlet);
        registration.addUrlMappings("/probe/*");
        return registration;
    }
}
