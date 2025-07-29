package org.sample.app;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.layered.TFramedTransport;
import org.sample.thrift.Calculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientApp {
    private static final Logger log = LoggerFactory.getLogger(ClientApp.class);

    private static final int serverPort = 9900;
    private static final String serverHost = "localhost";

    public static void main(String[] args) throws TException {
        TTransport transport = new TSocket(serverHost, serverPort);
        transport.open();

        TProtocol protocol = new TBinaryProtocol(transport);
        Calculator.Client client = new Calculator.Client(protocol);

        work(client);
    }

     static void work(Calculator.Client client) throws TException {
        client.ping();
        log.info("call ping()");

        int result = client.add(100, 200);
        log.info("call add({}, {}) = {}", 100, 200, result);

        result = client.add(500, 25);
         log.info("call add({}, {}) = {}", 500, 25, result);
    }
}
