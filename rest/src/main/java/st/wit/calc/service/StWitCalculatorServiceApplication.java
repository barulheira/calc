package st.wit.calc.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@SpringBootApplication
public class StWitCalculatorServiceApplication {
    
    @Value("${st.wit.rabbitmq.host}")
    private String hostName;

    public static void main(String[] args) {
        SpringApplication.run(StWitCalculatorServiceApplication.class, args);
    }

    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
        loggingFilter.setIncludeClientInfo(true);
        loggingFilter.setIncludeQueryString(true);
        loggingFilter.setIncludePayload(false);
        loggingFilter.setIncludeHeaders(false);
        return loggingFilter;
    }
    
    @Bean
    public RPCClient client() throws Exception {
        return new RPCClient(hostName);
    }
}
