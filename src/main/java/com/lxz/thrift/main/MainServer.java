package com.lxz.thrift.main;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxz.thrift.Calculator;
import com.lxz.thrift.CalculatorHandler;

public class MainServer {
    private static final Logger logger = LoggerFactory.getLogger(MainServer.class);

    public static void main(String[] args) {
        try {

            final Calculator.Processor processor = new Calculator.Processor(new CalculatorHandler());

            Runnable simple = new Runnable() {
                public void run() {
                    simple(processor);
                }
            };

            new Thread(simple).start();
        } catch (Exception x) {
            logger.error("", x);
        }
    }

    public static void simple(Calculator.Processor processor) {
        try {
            TServerTransport serverTransport = new TServerSocket(9090);
            // TServer server = new TSimpleServer(new Args(serverTransport).processor(processor));

            TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));

            logger.info("Starting the simple server...");
            server.serve();
        } catch (Exception e) {
            logger.error("", e);
        }
    }

}