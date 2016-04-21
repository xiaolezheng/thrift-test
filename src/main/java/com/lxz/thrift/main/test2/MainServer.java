package com.lxz.thrift.main.test2;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxz.thrift.handler.test2.CalculatorHandler;
import com.lxz.thrift.test2.CalculatorService;

/**
 * Created by xiaolezheng on 16/4/21.
 */
public class MainServer {
    private static final Logger logger = LoggerFactory.getLogger(MainServer.class);
    private static final int PORT = 9090;

    public static void main(String[] args){
        try{
            final CalculatorService.Processor processor = new CalculatorService.Processor(new CalculatorHandler());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    start(processor);
                }
            }).start();

        }catch (Exception e){
            logger.error("", e);
        }
    }

    public static void start(CalculatorService.Processor processor) {
        try {
            TServerTransport serverTransport = new TServerSocket(PORT);
            final TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));
            //TServer server = new TSimpleServer(new THsHaServer.Args(serverTransport).processor(processor));

            // Use this for a multithreaded server
            // TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));

            logger.info("Starting the simple server...");

            server.serve();

            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    server.stop();
                }
            }));
        } catch (Exception e) {
            logger.error("", e);
        }
    }
}
