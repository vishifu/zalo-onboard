package org.sample.app.client.service.impl;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.sample.app.client.service.SongLibService;
import org.sample.thrift.SongService;
import org.sample.thrift.SongStruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SongLibServiceImpl implements SongLibService {

    private static final Logger log = LoggerFactory.getLogger(SongLibServiceImpl.class);

    private final SongService.Client client;

    public SongLibServiceImpl(String host, int port) {
        try {
            TTransport transport = new TSocket(host, port);
            TProtocol protocol = new TBinaryProtocol(transport);
            transport.open();;
            client = new SongService.Client(protocol);
        } catch (TTransportException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveSong(SongStruct songStruct) {
        try {
            client.save(songStruct);
        } catch (TException e) {
            throw new RuntimeException(e);
        }
        log.info("saving {}", songStruct.toString());
    }

    public SongStruct find(int id) {
        SongStruct song = null;
        try {
            song = client.get(id);
        } catch (TException e) {
            throw new RuntimeException(e);
        }
        log.info("find song id: {}; result={}", id, song);
        return song;
    }
}
