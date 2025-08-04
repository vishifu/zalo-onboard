// define namespace
include "shared.thrift"

namespace java org.sample.thrift

// define service
service SongService {
    shared.SongStruct get(1:i32 id),
    void save(2:shared.SongStruct song)
}