package com.lxz.thrift;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xiaolezheng on 16/4/20.
 */
public class CalculatorHandler implements Calculator.Iface{
    private static final Logger logger = LoggerFactory.getLogger(CalculatorHandler.class);

    @Override
    public void ping() throws TException {
        logger.info("ping...............");
    }

    @Override
    public int add(int num1, int num2) throws TException {
        return num1 + num2;
    }

    @Override
    public int calculate(int logid, Work w) throws TException {
        switch (w.op){
            case ADD:
                return w.num1 + w.num2;
            case SUBTRACT:
                return w.num1 - w.num2;
            case MULTIPLY:
                return w.num1 * w.num2;
            case DIVIDE:
                return w.num1 / w.num2;
        }

        return 0;
    }

    @Override
    public void zip() throws TException {
        logger.info("zip................");
    }

    @Override
    public SharedStruct getStruct(int key) throws TException {
        return new SharedStruct(1,"hello world!");
    }
}
