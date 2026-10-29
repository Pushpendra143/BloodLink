package com.example.bloodlink.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

// SOAP lives under /ws, the REST controllers under /api
@EnableWs
@Configuration
public class WebServiceConfig {

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext context) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(context);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }

    // the bean name decides the wsdl url -> /ws/bloodlink.wsdl
    @Bean(name = "bloodlink")
    public DefaultWsdl11Definition bloodlinkWsdl(XsdSchema bloodlinkSchema) {
        DefaultWsdl11Definition wsdl = new DefaultWsdl11Definition();
        wsdl.setPortTypeName("BloodLinkPort");
        wsdl.setLocationUri("/ws");
        wsdl.setTargetNamespace("http://bloodlink.example.com/soap");
        wsdl.setSchema(bloodlinkSchema);
        return wsdl;
    }

    @Bean
    public XsdSchema bloodlinkSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/bloodlink.xsd"));
    }
}
