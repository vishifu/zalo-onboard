package org.sample.app;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.sample.app.handler.PlayerHandler;
import org.sample.thrift.Player;

public class ServerApp {
    private static final int port = 9900;

    public static void main(String[] args) throws TTransportException {
        Player.Processor<PlayerHandler> processor = new Player.Processor<>(new PlayerHandler());
        TServerTransport transport = new TServerSocket(port);
        TServer.Args serverArgs = new TServer.Args(transport)
                .processor(processor)
                .protocolFactory(TBinaryProtocol::new);

        TServer server = new TSimpleServer(serverArgs);

        Runnable serving = server::serve;
        new Thread(serving).start();
    }
}
