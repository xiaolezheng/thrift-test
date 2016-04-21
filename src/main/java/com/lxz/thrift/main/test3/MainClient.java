package com.lxz.thrift.main.test3;

import com.facebook.nifty.client.FramedClientConnector;
import com.facebook.nifty.client.NettyClientConfigBuilder;
import com.facebook.nifty.client.NiftyClient;
import io.airlift.units.Duration;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxz.thrift.test2.CalculatorService;
import com.lxz.thrift.test2.Operation;
import com.lxz.thrift.test2.Work;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by xiaolezheng on 16/4/21.
 */
public class MainClient {
    private static final Logger logger = LoggerFactory.getLogger(MainClient.class);
    private static final String HOST = "localhost";
    private static final int PORT = 9090;

    public static void main(String[] args) {
        try {
            doRequestMultiThread();

            doRequestNiftyClient();
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    private static void doRequestMultiThread() {
        ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (int i = 0; i < 100000; i++) {
            service.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        TTransport transport = new TSocket(HOST, PORT);
                        transport.open();

                        TProtocol protocol = new TBinaryProtocol(transport);
                        CalculatorService.Client client = new CalculatorService.Client(protocol);

                        perform(client);
                    } catch (Exception e) {
                        logger.error("", e);
                    }
                }
            });
        }

        service.shutdown();
    }

    private static void perform(CalculatorService.Client client) throws TException {
        client.ping();

        for (int i = 0; i < 10; i++) {
            int result = client.calculate(i,
                    new Work().setNum1(10 + i).setNum2(20).setOp(Operation.ADD).setComment("求和"));

            logger.info("add result: {}", result);

            result = client.calculate(i,
                    new Work().setNum1(10 + i).setNum2(20).setOp(Operation.MULTIPLY).setComment("求积"));

            logger.info("mul result: {}", result);

        }
    }

    /**
     *
     * NiftyClient test
     *
     * @throws Exception
     */
    private static void doRequestNiftyClient() throws Exception {
        final Duration TEST_CONNECT_TIMEOUT = new Duration(500, TimeUnit.MILLISECONDS);
        final Duration TEST_RECEIVE_TIMEOUT = new Duration(500, TimeUnit.MILLISECONDS);
        final Duration TEST_READ_TIMEOUT = new Duration(500, TimeUnit.MILLISECONDS);
        final Duration TEST_SEND_TIMEOUT = new Duration(500, TimeUnit.MILLISECONDS);
        final int TEST_MAX_FRAME_SIZE = 16777216;

        NettyClientConfigBuilder builder = new NettyClientConfigBuilder().setBossThreadCount(2).setWorkerThreadCount(2);
        NiftyClient niftyClient = new NiftyClient(builder.build());

        TTransport transport = niftyClient.connectSync(CalculatorService.Client.class, new FramedClientConnector(new InetSocketAddress(HOST, PORT)),
                TEST_CONNECT_TIMEOUT, TEST_RECEIVE_TIMEOUT, TEST_READ_TIMEOUT, TEST_SEND_TIMEOUT, TEST_MAX_FRAME_SIZE);
        TBinaryProtocol tBinaryProtocol = new TBinaryProtocol(transport);
        CalculatorService.Client client = new CalculatorService.Client(tBinaryProtocol);


        perform(client);

        niftyClient.close();
    }
}
