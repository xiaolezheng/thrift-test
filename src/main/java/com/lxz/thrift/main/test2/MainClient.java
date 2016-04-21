package com.lxz.thrift.main.test2;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxz.thrift.test2.CalculatorService;
import com.lxz.thrift.test2.Operation;
import com.lxz.thrift.test2.Work;

/**
 * Created by xiaolezheng on 16/4/21.
 */
public class MainClient {
    private static final Logger logger = LoggerFactory.getLogger(MainClient.class);
    private static final int PORT = 9090;

    public static void main(String[] args){
        try{
            TTransport transport = new TSocket("localhost", PORT);
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            CalculatorService.Client client = new CalculatorService.Client(protocol);

            perform(client);
        }catch (Exception e){
            logger.error("", e);
        }
    }

    private static void perform(CalculatorService.Client client) throws TException{
        client.ping();

        for(int i=0; i<100; i++) {
            int result = client.calculate(i, new Work().setNum1(10+i).setNum2(20).setOp(Operation.ADD).setComment("求和"));

            logger.info("add result: {}", result);

            result = client.calculate(i, new Work().setNum1(10+i).setNum2(20).setOp(Operation.MULTIPLY).setComment("求积"));

            logger.info("mul result: {}", result);

        }
    }
}
