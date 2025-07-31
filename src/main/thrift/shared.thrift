namespace java org.sample.thrift

struct SongStruct {
    1: i32 id
    2: string name
    3: double rating
    4: set<i32> authorID
    5: binary content
}