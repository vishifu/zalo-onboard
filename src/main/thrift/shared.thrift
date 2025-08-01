namespace java org.sample.thrift

struct DateStruct {
    1: i32 date
    2: i32 month
    3: i32 year
}

struct SongStruct {
    1: i32 id
    2: string name
    3: double rating
    4: set<i32> authorID
    5: binary content
    6: DateStruct publishDate
    7: list<SongStruct> innerSongs
}