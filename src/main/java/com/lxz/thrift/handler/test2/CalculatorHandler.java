package com.lxz.thrift.handler.test2;

import com.lxz.thrift.test2.CalculatorService;
import com.lxz.thrift.test2.InvalidOperation;
import com.lxz.thrift.test2.PingResponse;
import com.lxz.thrift.test2.Work;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xiaolezheng on 16/4/21.
 */
public class CalculatorHandler implements CalculatorService.Iface{
    private static final Logger logger = LoggerFactory.getLogger(CalculatorHandler.class);

    @Override
    public int calculate(int logid, Work w) throws InvalidOperation, TException {
        logger.info("logid: {}, work: {}", logid, w);
        if(w != null){
            switch (w.getOp()){
                case ADD:
                    return w.getNum1() + w.getNum2();
                case SUBTRACT:
                    return w.getNum1() - w.getNum2();
                case MULTIPLY:
                    return w.getNum1() * w.getNum2();
                case DIVIDE:
                    return w.getNum1() / w.getNum2();
                default:
                    throw new InvalidOperation().setWhatOp(-1).setWhy("不支持的op");
            }
        }

        throw new TException("req param w is null");
    }

    @Override
    public PingResponse ping() throws TException {
        logger.info("ping ......");

        return new PingResponse().setKey("status").setValue("success");
    }
}
