// define namespace
include "shared.thrift"

namespace java org.sample.thrift

// define service
service Calculator {
    void ping(),
    i32 add(1:i32 a, 2:i32 b),
}

service Player {
    shared.SongStruct get(1:i32 id),
    void save(2:shared.SongStruct song)
}