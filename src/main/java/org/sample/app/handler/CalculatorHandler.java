package org.sample.app.handler;

import org.apache.thrift.TException;
import org.sample.thrift.Calculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalculatorHandler implements Calculator.Iface {

    private static final Logger log = LoggerFactory.getLogger(CalculatorHandler.class);

    @Override
    public void ping() throws TException {
        log.info("receive call ping()");
    }

    @Override
    public int add(int a, int b) throws TException {
        log.info("receive call add({}, {})", a, b);
        return a + b;
    }
}
