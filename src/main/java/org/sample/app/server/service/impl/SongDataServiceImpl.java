package org.sample.app.server.service.impl;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.sample.app.server.handler.SongServiceHandler;
import org.sample.app.server.service.SongDataService;
import org.sample.mongo.MongoContext;
import org.sample.thrift.SongService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SongDataServiceImpl implements SongDataService {

    private static final Logger log = LoggerFactory.getLogger(SongDataServiceImpl.class);

    private final SongService.Processor<SongServiceHandler> processor;

    public SongDataServiceImpl(MongoContext context) {
        processor = new SongService.Processor<>(
                new SongServiceHandler(new SongMongoPersistImpl(context)));
    }

    @Override
    public void process(int port) throws TTransportException {

        TServerTransport transport = new TServerSocket(port);
        TServer.Args serverArgs = new TServer.Args(transport)
                .processor(processor)
                .protocolFactory(TBinaryProtocol::new);

        final TServer server = new TSimpleServer(serverArgs);

        final Runnable serving = () -> {
            log.info("server serving...");
            server.serve();
        };
        new Thread(serving).start();
    }
}
