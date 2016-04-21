package com.lxz.thrift.handler.test1;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxz.thrift.test1.ArgType;
import com.lxz.thrift.test1.CalculatorService;
import com.lxz.thrift.test1.Request;
import com.lxz.thrift.test1.Response;

/**
 * Created by xiaolezheng on 16/4/21.
 */
public class CalculatorHandler implements CalculatorService.Iface{
    private static final Logger logger = LoggerFactory.getLogger(CalculatorHandler.class);

    @Override
    public Response invoke(Request req) throws TException {
        if(req != null){
            int result = 0;
            switch (req.getOperationType()){
                case ADD:
                    result = req.getArg1() + req.getArg2();
                    break;
                case SUB:
                    result = req.getArg1() - req.getArg2();
                    break;
                case MUL:
                    result = req.getArg1() * req.getArg2();
                    break;
                case DIV:
                    result = req.getArg1() / req.getArg2();
                    break;
                default:
                    throw new TException("不支持的计算类型");
            }

            logger.info("req: {}", req);

            return new Response().setId(1).setResult(result).setResType(ArgType.INT);
        }

        throw new TException("req param is null");
    }

    @Override
    public void ping() throws TException {
        logger.info("ping ......");
    }
}
