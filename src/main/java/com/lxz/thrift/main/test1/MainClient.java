package com.lxz.thrift.main.test1;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxz.thrift.test1.ArgType;
import com.lxz.thrift.test1.CalculatorService;
import com.lxz.thrift.test1.OperationType;
import com.lxz.thrift.test1.Request;
import com.lxz.thrift.test1.Response;

/**
 * Created by xiaolezheng on 16/4/21.
 */
public class MainClient {
    private static final Logger logger = LoggerFactory.getLogger(MainClient.class);
    private static final int PORT = 9090;
    private static final String HOST = "127.0.0.1";
    private static final AtomicInteger ID = new AtomicInteger(0);
    private static final Random RANDOM = new Random();
    private static final int CONNECTIONS = 20;

    public static void main(String[] args) {
        try {
            multipleConnections();
            producerConsumer();
        } catch (Exception e) {
            logger.error("", e);
        }

    }

    private static void producerConsumer() throws Exception
    {
        final CountDownLatch latch = new CountDownLatch(CONNECTIONS);
        final SynchronousQueue<Work> queue = new SynchronousQueue<Work>();
        final ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        new Thread()
        {
            public void run()
            {
                int count = 0;
                while (count < CONNECTIONS)
                {
                    Work newWork = queue.poll();

                    if (newWork == null)
                        continue;

                    service.submit(newWork);
                    count++;
                }
            }
        }.start();

        for (int i = 0; i < CONNECTIONS; i++)
            queue.put(new Work(latch, i, getRandomArgument(), getRandomArgument(), OperationType.ADD));

        latch.await();
        service.shutdown();
    }

    private static void multipleConnections() throws Exception {
        final ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 50; i++) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        final TTransport transport = getNewTransport();

                        final CalculatorService.Client client = getNewClient(transport);

                        client.ping();

                        invokeRequests(client, ID.incrementAndGet(), getRandomArgument(), getRandomArgument());

                        transport.close();
                    } catch (Exception e) {
                        logger.error("", e);
                    }
                }
            });
        }

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                executor.shutdown();
            }
        }));
    }

    private static void invokeRequests(CalculatorService.Client client, int startId, int arg1, int arg2)
            throws TException {

        Response responseAdd = client.invoke(new Request(startId + 0, arg1, arg2, ArgType.INT, OperationType.ADD));
        Response responseSub = client.invoke(new Request(startId + 1, arg1, arg2, ArgType.INT, OperationType.SUB));
        Response responseMul = client.invoke(new Request(startId + 2, arg1, arg2, ArgType.INT, OperationType.MUL));
        Response responseDiv = client.invoke(new Request(startId + 3, arg1, arg2, ArgType.INT, OperationType.DIV));

        logger.info("resultAdd: {}", responseAdd);
        logger.info("resultSub: {}", responseSub);
        logger.info("resultMul: {}", responseMul);
        logger.info("resultDiv: {}", responseDiv);

    }

    private static TTransport getNewTransport() throws TTransportException {
        return new TFramedTransport(new TSocket(HOST, PORT));
    }

    private static CalculatorService.Client getNewClient(TTransport transport) throws TTransportException {
        if (!transport.isOpen())
            transport.open();

        return new CalculatorService.Client(new org.apache.thrift.protocol.TBinaryProtocol(transport, true, true));
    }

    private static int getRandomArgument() {
        int n = RANDOM.nextInt(50000);
        return n == 0 ? 1 : n;
    }

    private static class Work implements Callable<Response> {
        private final CountDownLatch latch;
        private final int id, arg1, arg2;
        private final OperationType op;

        public Work(CountDownLatch latch, int id, int arg1, int arg2, OperationType op) {
            this.latch = latch;
            this.id = id;
            this.arg1 = arg1;
            this.arg2 = arg2;
            this.op = op;
        }

        @Override
        public Response call() throws Exception {
            TTransport transport = getNewTransport();
            Response res = null;
            try {
                CalculatorService.Client client = getNewClient(transport);

                res = client.invoke(new Request(id, arg1, arg2, ArgType.INT, op));

                logger.info("res: {}", res);
            } finally {
                transport.close();
            }

            latch.countDown();

            return res;
        }
    }
}
