package st.wit.calc;

import java.math.BigDecimal;
import java.math.MathContext;

public enum Calculator {
    SUM((a, b) -> a.add(b)),
    SUB((a, b) -> a.subtract(b)),
    MULTI((a, b) -> a.multiply(b)),
    DIV((a, b) -> a.divide(b, new MathContext(16)));
    
    private final Calc c;
    
    static interface Calc {
        BigDecimal calcMe(BigDecimal a, BigDecimal b);
    }
    
    Calculator(Calc c) {
        this.c = c;
    }
    
    public BigDecimal calc(BigDecimal a, BigDecimal b) {
        return c.calcMe(a, b);
    }
}