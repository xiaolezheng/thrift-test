package com.lxz.thrift.main.test4;

import com.google.common.collect.Lists;
import org.apache.hadoop.hbase.thrift2.generated.TColumnValue;
import org.apache.hadoop.hbase.thrift2.generated.TGet;
import org.apache.hadoop.hbase.thrift2.generated.THBaseService;
import org.apache.hadoop.hbase.thrift2.generated.TPut;
import org.apache.hadoop.hbase.thrift2.generated.TResult;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * HBase-thrift client 测试
 * <p>
 * 1. 启动 HBase-thrift server {./bin/hbase thrift2 start}
 * <p>
 * Created by xiaolezheng on 16/4/25.
 */
public class HBaseThriftClient {
    private static final Logger logger = LoggerFactory.getLogger(HBaseThriftClient.class);

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 9090;

    public static void main(String[] args) throws Exception {
        TTransport transport = new TSocket(HOST, PORT);
        TProtocol protocol = new TBinaryProtocol(transport);
        THBaseService.Iface client = new THBaseService.Client(protocol);

        transport.open();

        ByteBuffer table = ByteBuffer.wrap("test".getBytes());

        for (int i = 1; i < 1000; i++) {
            TPut put = new TPut();
            put.setRow(String.valueOf(i).getBytes());

            TColumnValue columnValue = new TColumnValue();
            columnValue.setFamily("article".getBytes());
            columnValue.setQualifier("title,".getBytes());
            columnValue.setValue(String.valueOf("thrift" + i).getBytes());

            TColumnValue columnValue1 = new TColumnValue();
            columnValue1.setFamily("article".getBytes());
            columnValue1.setQualifier("sn".getBytes());
            columnValue1.setValue(String.valueOf(System.currentTimeMillis()).getBytes());

            List<TColumnValue> columnValues = Lists.newArrayList(columnValue, columnValue1);

            put.setColumnValues(columnValues);

            client.put(table, put);
        }


        for (int i = 1; i < 1000; i++) {
            TGet get = new TGet();
            get.setRow(String.valueOf(i).getBytes());

            TResult result = client.get(table, get);

            StringBuilder builder = new StringBuilder();

            for (TColumnValue tColumnValue : result.getColumnValues()) {
                builder.append(new String(tColumnValue.getFamily())).append(":");
                builder.append(new String(tColumnValue.getQualifier())).append(":");
                builder.append(new String(tColumnValue.getValue())).append(":");
                builder.append(tColumnValue.getTimestamp()).append("|");
            }

            logger.info("row:{} ", builder.toString());
        }

        transport.close();
    }
}
