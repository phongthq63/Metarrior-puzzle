namespace java com.lizardtek.svapi.service.IAP

include "exception.thrift"

service EventService {
    bool addEvent(1:required string zoneName, 2:required string jsonData) throws (1:exception.ThriftSVException e);
    bool removeEvent(1:required string zoneName, 2:required list<string> listId) throws (1:exception.ThriftSVException e);
    bool addSpecialEvent(1:required string zoneName, 2:required string jsonData) throws (1:exception.ThriftSVException e);
    bool removeSpecialEvent(1:required string zoneName, 2:required list<string> listId) throws (1:exception.ThriftSVException e);
}