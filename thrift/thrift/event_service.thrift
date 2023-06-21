namespace java com.bamisu.log.gamethrift.service.event

include "exception.thrift"
include "constant.thrift"
include "event.thrift"


service EventService {
    void addNewEvent(1:string title, 2:string content, 3:constant.int fr, 4:constant.int to, 5:string url, 6:string img);
    list<event.TEventInfo> getAllEvent();
    constant.long deleteEvent(1:constant.long to);
    constant.long editEvent(1:event.TEventInfo event);
}

