package com.lxz.thrift.main;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxz.thrift.Calculator;
import com.lxz.thrift.InvalidOperation;
import com.lxz.thrift.Operation;
import com.lxz.thrift.SharedStruct;
import com.lxz.thrift.Work;

public class MainClient {
    private static final Logger logger = LoggerFactory.getLogger(MainClient.class);

    public static void main(String[] args) {
        try {
            TTransport transport = new TSocket("localhost", 9090);
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            Calculator.Client client = new Calculator.Client(protocol);

            perform(client);

            transport.close();
        } catch (TException x) {
            x.printStackTrace();
        }
    }

    private static void perform(Calculator.Client client) throws TException {
        client.ping();
        logger.info("ping()");

        int sum = client.add(1, 1);
        logger.info("1+1=" + sum);

        Work work = new Work();

        work.op = Operation.DIVIDE;
        work.num1 = 1;
        work.num2 = 2;
        try {
            int quotient = client.calculate(1, work);
            logger.info("result: {}", quotient);
        } catch (InvalidOperation io) {
            logger.error("Invalid operation: {}", io.why, io);
        }

        work.op = Operation.SUBTRACT;
        work.num1 = 15;
        work.num2 = 10;
        try {
            int diff = client.calculate(1, work);
            logger.info("15-10=" + diff);
        } catch (InvalidOperation io) {
            logger.error("Invalid operation: {}", io.why);
        }

        SharedStruct log = client.getStruct(1);
        logger.info("Check log: {}", log.value);
    }
}