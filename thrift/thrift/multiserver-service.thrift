namespace java com.bamisu.log.gamethrift.service.multiserver

include "exception.thrift"

service MultiserverService {
    bool maintenaceServer(1:required bool PRE_MAINTENANCE) throws (1:exception.ThriftSVException e);
    string getCCUServer() throws (1:exception.ThriftSVException e);
    bool activeEventModuleServer(1:required string moduleName, 2:required bool active, 3:required i32 timeStamp, 4:required string zoneName) throws (1:exception.ThriftSVException e);
}