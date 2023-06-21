namespace java com.bamisu.log.gamethrift.service.dynamicconfig

include "exception.thrift"
include "constant.thrift"

service DynamicConfigService {
    string getConfig();
    bool updateConfig(1:string jsonData);
    string getMomoConfig();
    string getSellChipConfig();
    bool updateMomoConfig(1:string jsonData);
    bool updateSellChipConfig(1:string jsonData);
}