package org.sample.app.client.service;

import org.sample.thrift.SongStruct;

public interface SongLibService {

    void saveSong(SongStruct songStruct);

    SongStruct find(int id);

}
