package st.wit.calc.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);
    private final RPCClient client;
    
    public Controller(RPCClient client) {
        this.client = client;
    }

    private Result request(BigDecimal a, BigDecimal b, String operation,
            HttpServletResponse response) throws IOException, TimeoutException, InterruptedException {
        RPCClient cli = this.client;
        final String corrId = UUID.randomUUID().toString();
        response.setHeader("Correlation-Id", corrId);
        logger.info("Correlation-Id: "+corrId + ": " +a.toPlainString() + ' ' + operation + " "+b.toPlainString());
        String call = cli.call(a.toPlainString()+'\t'+b.toPlainString(), operation, corrId);
        return new Result(new BigDecimal(call));
    }
    
	@GetMapping("/sum")
    public Result sum(@RequestParam(value = "a") BigDecimal a,
                      @RequestParam(value = "b") BigDecimal b,
                      HttpServletResponse response) throws Exception {
        return request(a, b, "SUM", response);
    }
    
	@GetMapping("/sub")
    public Result sub(@RequestParam(value = "a") BigDecimal a,
                      @RequestParam(value = "b") BigDecimal b,
                      HttpServletResponse response) throws Exception {
        return request(a, b, "SUB", response);
    }
    
	@GetMapping("/multi")
    public Result multi(@RequestParam(value = "a") BigDecimal a,
                      @RequestParam(value = "b") BigDecimal b,
                      HttpServletResponse response) throws Exception {
        return request(a, b, "MULTI", response);
    }
    
	@GetMapping("/div")
    public Result div(@RequestParam(value = "a") BigDecimal a,
                      @RequestParam(value = "b") BigDecimal b,
                      HttpServletResponse response) throws Exception {
        return request(a, b, "DIV", response);
    }
}
