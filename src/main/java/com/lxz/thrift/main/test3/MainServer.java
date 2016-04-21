package com.lxz.thrift.main.test3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.thrift.TProcessor;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.facebook.nifty.core.NettyServerConfig;
import com.facebook.nifty.core.NettyServerConfigBuilder;
import com.facebook.nifty.core.NettyServerTransport;
import com.facebook.nifty.core.ThriftServerDef;
import com.facebook.nifty.core.ThriftServerDefBuilder;
import com.lxz.thrift.handler.test2.CalculatorHandler;
import com.lxz.thrift.test2.CalculatorService;

/**
 *
 *
 * Nifty is an implementation of Thrift clients and servers on Netty. https://github.com/facebook/nifty
 *
 * Created by xiaolezheng on 16/4/21.
 */
public class MainServer {
    private static final Logger logger = LoggerFactory.getLogger(MainServer.class);
    private static final int PORT = 9090;

    public static void main(String[] args) {
        try {
            TProcessor processor = new CalculatorService.Processor(new CalculatorHandler());

            ThriftServerDef serverDef = new ThriftServerDefBuilder().withProcessor(processor).listen(PORT).build();

            final NettyServerTransport server = new NettyServerTransport(serverDef, NettyConfigProvider.build(),
                    new DefaultChannelGroup());

            ExecutorService bossExecutor = Executors.newCachedThreadPool();
            ExecutorService workerExecutor = Executors.newCachedThreadPool();

            server.start(new NioServerSocketChannelFactory(bossExecutor, workerExecutor));

            logger.info("start netty-thrift server .......");

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    try {
                        server.stop();
                    } catch (InterruptedException e) {
                        logger.error("", e);
                    }
                }
            });
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    private static class NettyConfigProvider {

        public static NettyServerConfig build() {
            NettyServerConfigBuilder nettyConfigBuilder = new NettyServerConfigBuilder();
            nettyConfigBuilder.getSocketChannelConfig().setTcpNoDelay(true);
            nettyConfigBuilder.getSocketChannelConfig().setConnectTimeoutMillis(5000);
            nettyConfigBuilder.getSocketChannelConfig().setTcpNoDelay(true);

            return nettyConfigBuilder.build();
        }
    }
}
