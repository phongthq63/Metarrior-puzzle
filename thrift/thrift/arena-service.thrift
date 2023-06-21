namespace java com.lizardtek.svapi.service.arena

include "exception.thrift"

service ArenaService {
    bool sendGiftArenaDaily(1:required string zoneName) throws (1:exception.ThriftSVException e);
    bool closeSeasonArena(1:required string zoneName) throws (1:exception.ThriftSVException e);
    bool openSeasonArena(1:required string zoneName) throws (1:exception.ThriftSVException e);
}