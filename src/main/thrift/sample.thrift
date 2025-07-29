// define namespace
include "shared.thrift"

namespace java org.sample.thrift

// define service
service Calculator {
    void ping(),
    i32 add(1:i32 a, 2:i32 b),
}