namespace java org.sample.thrift

typedef i32 int

struct SharedStruct {
    1: i32 key
    2: string value
}

service SharedService {
    SharedStruct getStruct()
}