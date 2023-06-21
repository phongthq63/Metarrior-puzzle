namespace java com.lizardtek.svapi.service.IAP

include "exception.thrift"
include "constant.thrift"

service IAPClientService {
    bool addSaleIAP(1:required string zoneName, 2:required string jsonData) throws (1:exception.ThriftSVException e);
    bool removeSaleIAP(1:required string zoneName, 2:required list<string> listIdSale) throws (1:exception.ThriftSVException e);
    bool buyIAP(1:required string zoneName, 2:required constant.long uid, 3:required string idPackage) throws (1:exception.ThriftSVException e);
}