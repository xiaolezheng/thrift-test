package com.lxz.thrift.main.test1;

import com.lxz.thrift.handler.test1.CalculatorHandler;
import com.lxz.thrift.test1.CalculatorService;
import com.thinkaurelius.thrift.Message;
import com.thinkaurelius.thrift.TDisruptorServer;
import com.thinkaurelius.thrift.util.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * Created by xiaolezheng on 16/4/21.
 */
public class MainServer {
    private static final Logger logger = LoggerFactory.getLogger(MainServer.class);
    private static final int PORT = 9090;
    private static final String HOST = "127.0.0.1";

    public static void main(String[] args){
        try {
            prepare(true, false, PORT);
        }catch (Exception e){
            logger.error("", e);
        }

    }

    private static void prepare(boolean onHeapBuffers, boolean shouldRellocateBuffers, int port) throws Exception
    {
        final TNonblockingServerTransport socket = new TNonblockingServerSocket(new InetSocketAddress(HOST, port));
        final TBinaryProtocol.Factory protocol = new TBinaryProtocol.Factory();

        TDisruptorServer.Args args = new TDisruptorServer.Args(socket)
                .inputTransportFactory(new TFramedTransport.Factory())
                .outputTransportFactory(new TFramedTransport.Factory())
                .inputProtocolFactory(protocol)
                .outputProtocolFactory(protocol)
                .processor(new CalculatorService.Processor<CalculatorService.Iface>(new CalculatorHandler()))
                .useHeapBasedAllocation(onHeapBuffers)
                .alwaysReallocateBuffers(shouldRellocateBuffers);

        final TServer TEST_SERVICE = new CustomTDisruptorServer(args);

        new Thread()
        {
            public void run()
            {
                logger.info("disruptor thrift server start ......");

                TEST_SERVICE.serve();
            }
        }.start();

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                TEST_SERVICE.stop();
            }
        }));
    }

    private static class CustomTDisruptorServer extends TDisruptorServer
    {
        public CustomTDisruptorServer(Args args)
        {
            super(args);
        }

        @Override
        protected void beforeInvoke(Message message)
        {}
    }
}
