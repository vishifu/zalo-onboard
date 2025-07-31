package org.sample.app.server;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.sample.app.server.handler.SongServiceHandler;
import org.sample.app.server.service.SongMongoPersistImpl;
import org.sample.thrift.SongService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerApplication {

    private static final int port = 9900;

    public static void main(String[] args) throws TTransportException {

        SongService.Processor<SongServiceHandler> processor = new SongService.Processor<>(
                new SongServiceHandler(new SongMongoPersistImpl()));

        TServerTransport transport = new TServerSocket(port);
        TServer.Args serverArgs = new TServer.Args(transport)
                .processor(processor)
                .protocolFactory(TBinaryProtocol::new);

        TServer server = new TSimpleServer(serverArgs);
        Runnable serving = server::serve;

        ExecutorService executor = Executors.newFixedThreadPool(4);
        executor.execute(serving);
    }

}
