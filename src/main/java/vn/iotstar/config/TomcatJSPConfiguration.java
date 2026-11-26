package vn.iotstar.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
public class TomcatJSPConfiguration {

    @Bean
    public WebServerFactoryCustomizer<WebServerFactory> staticResourceCustomizer() {
        return factory -> {
            if (factory instanceof TomcatServletWebServerFactory tomcatFactory) {
                tomcatFactory.addContextCustomizers(
                        context -> context.addLifecycleListener(new JSPStaticResourceConfigurer(context)));
            }
        };
    }

    // Required on Tomcat 11 so JSP forward does not commit the response before SiteMesh 3 decorates it.
    @Bean
    public InternalResourceViewResolver jspViewResolver(
            @Value("${spring.mvc.view.prefix:/WEB-INF/views/}") String prefix,
            @Value("${spring.mvc.view.suffix:.jsp}") String suffix) {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver(prefix, suffix);
        resolver.setContentType("text/html; charset=UTF-8");
        resolver.setAlwaysInclude(true);
        return resolver;
    }
}
