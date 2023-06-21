package com.bamisu.log.sdk.module.multiserver;

import com.bamisu.gamelib.entities.JoinedServerData;
import com.bamisu.log.sdk.module.account.model.AccountModel;
import com.bamisu.log.sdk.module.data.SDKDatacontroler;
import com.bamisu.log.sdk.module.gamethriftclient.IAP.ServerGateIAP;
import com.bamisu.log.sdk.module.gamethriftclient.IAP.entities.InfoIAPSale;
import com.bamisu.log.sdk.module.gamethriftclient.arena.ServerGateArena;
import com.bamisu.log.sdk.module.gamethriftclient.mail.ServerGateMail;
import com.bamisu.log.sdk.module.gamethriftclient.mail.entities.MailInfo;
import com.bamisu.log.sdk.module.gamethriftclient.event.ServerGateEvent;
import com.bamisu.log.sdk.module.gamethriftclient.event.entities.InfoEventNoti;
import com.bamisu.log.sdk.module.invitecode.InviteManager;
import com.bamisu.log.sdk.module.multiserver.entities.Cluster;
import com.bamisu.log.sdk.module.multiserver.entities.ClusterMap;
import com.bamisu.log.sdk.module.gamethriftclient.multiserver.ServerGateMultiserver;
import com.bamisu.gamelib.base.config.ConfigHandle;
import com.bamisu.gamelib.entities.ServerInfo;
import com.bamisu.gamelib.utils.Utils;
import org.apache.thrift.TException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Create by Popeye on 10:31 AM, 4/24/2020
 */
public class MultiServerManager {
    private static MultiServerManager ourInstance = new MultiServerManager();

    public static MultiServerManager getInstance() {
        return ourInstance;
    }

    private Map<Integer, ServerInfo> mapServer = new ConcurrentHashMap<>();

    private int newServer = -1;
    private ClusterMap clusterMap;
    private int gameThriftPort = ConfigHandle.instance().getInt("game_t_port");

    private MultiServerManager() {
        //init cluster map
        clusterMap = Utils.fromJson(Utils.loadConfig("sdk/server_map.json"), ClusterMap.class);

        //init server
        for(Cluster cluster : clusterMap.clusterList){
            for(int serverID : cluster.serverList){
                mapServer.put(serverID, new ServerInfo(serverID, "" + serverID, cluster.addr, cluster.socketPort, cluster.proxyPort, "" + serverID));
            }
        }
    }

    /**
     * lấy thông tin server qua token (server login mới nhất hoặc server mới nhất)
     * @param accountID
     * @return
     */
    public ServerInfo getServerForToken(String accountID) {
        AccountModel accountModel = AccountModel.copyFromDB(accountID, SDKDatacontroler.getInstance());
        if(accountModel.lastServerLogin != -1){
            return getServerInfo(accountModel.lastServerLogin);
        }else {
            return getNewServer();
        }

    }

    /**
     * lấy server mới nhất
     * @return
     */
    public ServerInfo getNewServer(){
        if(newServer == -1){
            for(int serverID : mapServer.keySet()){
                if(serverID > newServer){
                    newServer = serverID;
                }
            }
        }
        return getServerInfo(newServer);
    }

    /**
     * lấy thông tin server qua ID
     * @param serverID
     * @return
     */
    public ServerInfo getServerInfo(int serverID){
        ServerInfo serverInfo = mapServer.get(serverID);
//        serverInfo.zone = "s1";
//        serverInfo.serverID = serverID;
        return serverInfo;
    }

    /**
     * Server game gọi lên để đăng ký
     * @param serverID
     * @param serverName
     * @param addr
     * @param port
     * @param zone
     * @return
     */
    public synchronized boolean registerServer(int serverID, String serverName, String addr, int port, String zone) {
//        ServerInfo serverInfo = new ServerInfo(serverID, serverName, addr, port, zone);
//        System.out.println("\n====");
//        if(mapServer.containsKey(serverInfo.serverID)){
//            System.out.println("Override Resgister server success");
//        }else {
//            System.out.println("Resgister server success");
//        }
//        System.out.println(Utils.toJson(serverInfo));
//        System.out.println("=====\n");
//
//        mapServer.put(serverInfo.serverID, serverInfo);
//        newServer = -1;
        return true;
    }

    public boolean unRegisterServer(int serverID) {
        newServer = -1;
        return false;
    }

    public synchronized boolean maintenanceServer(boolean PRE_MAINTENANCE){
        mapServer.values().parallelStream().
                map(server -> server.addr).
                collect(Collectors.toSet()).parallelStream().forEach(adr -> {
                    try{
                        ServerGateMultiserver.maintenaceServer(adr, gameThriftPort, PRE_MAINTENANCE);
                    }catch (TException e){
                        e.printStackTrace();
                    }
        });

        System.out.println(
                "\n--------------------------------------------" +
                        "\n>> Execution process success ... " +
                        "\n--------------------------------------------");
        return true;
    }

    public Map<String,Integer> getCCUAllServer(){
        Map<String,Integer> result = new HashMap<>();

        mapServer.values().parallelStream().
                map(server -> server.addr).
                collect(Collectors.toSet()).parallelStream().forEach(adr -> {
            try{
                result.putAll(ServerGateMultiserver.getCCUServer(adr, gameThriftPort));
            }catch (TException e){
                e.printStackTrace();
            }
        });

        return result;
    }


    public boolean updateEventModuleServer(int serverID, String module, boolean active, int timeStamp){
        if (serverID <= 0) {
            mapServer.values().forEach(server -> {
                try {
                    ServerGateMultiserver.updateEventModuleServer(server.addr, gameThriftPort, server.serverName, module, active, timeStamp);
                } catch (TException e) {
                    e.printStackTrace();
                    System.out.println(
                            "\n--------------------------------------------" +
                                    "\n>> Process server-" + server.serverName + " address-" + server.addr + ": fail  " +
                                    "\n--------------------------------------------");
                }
            });
        } else {
            ServerInfo serverInfo = getServerInfo(serverID);
            if(serverInfo == null){
                System.out.println(
                        "\n--------------------------------------------" +
                                "\n>> Server isn't exsist  " +
                                "\n--------------------------------------------");
                return false;
            }
            try {
                if(ServerGateMultiserver.updateEventModuleServer(serverInfo.addr, gameThriftPort, serverInfo.serverName, module, active, timeStamp)){
                    System.out.println(
                            "\n--------------------------------------------" +
                                    "\n>> Execution process success ... " +
                                    "\n--------------------------------------------");
                    return true;
                }else {
                    System.out.println(
                            "\n--------------------------------------------" +
                                    "\n>> Process server-" + serverInfo.serverName + " address-" + serverInfo.addr + ": fail  " +
                                    "\n--------------------------------------------");
                    return false;
                }
            } catch (TException e) {
                e.printStackTrace();
                System.out.println(
                        "\n--------------------------------------------" +
                                "\n>> Process server-" + serverInfo.serverName + " address-" + serverInfo.addr + ": fail  " +
                                "\n--------------------------------------------");
                return false;
            }
        }
        System.out.println(
                "\n--------------------------------------------" +
                        "\n>> Execution process success ... " +
                        "\n--------------------------------------------");
        return true;
    }

    public String sendMailToPlayer(int serverID, List<Long> listUid, MailInfo mail) {
        String result = "";

        if (serverID <= 0) {
            Map<String,String> mapResult = new HashMap<>();

            mapServer.values().forEach(server -> {
                        try {
                            mapResult.put(server.serverName, ServerGateMail.sendMailAllPlayer(server.addr, gameThriftPort, server.serverName, listUid, mail));
                        } catch (TException e) {
                            e.printStackTrace();
                            System.out.println(
                                    "\n--------------------------------------------" +
                                            "\n>> Process server-" + server.serverName + " address-" + server.addr + ": fail  " +
                                            "\n--------------------------------------------");
                        }
                    });

            for(String key : mapResult.keySet()){
                if(mapResult.get(key).isEmpty()){
                    result += key + ": success\n";
                }else {
                    result += key + ": " + mapResult.get(key) + "\n";
                }
            }
        } else {
            ServerInfo serverInfo = getServerInfo(serverID);
            if(serverInfo == null){
                System.out.println(
                        "\n--------------------------------------------" +
                                "\n>> Server isn't exsist  " +
                                "\n--------------------------------------------");
                return "Server isn't exsist";
            }
            try {
                result = ServerGateMail.sendMailAllPlayer(serverInfo.addr, gameThriftPort, serverInfo.serverName, listUid, mail);

                if(result.isEmpty()){
                    System.out.println(
                            "\n--------------------------------------------" +
                                    "\n>> Execution process success ... " +
                                    "\n--------------------------------------------");
                    return "success";
                }else {
                    System.out.println(
                            "\n--------------------------------------------" +
                                    "\n>> Process server-" + serverInfo.serverName + " address-" + serverInfo.addr + ": fail  " +
                                    "\n--------------------------------------------");
                    return "fail: " + result;
                }
            } catch (TException e) {
                e.printStackTrace();
                System.out.println(
                        "\n--------------------------------------------" +
                                "\n>> Process server-" + serverInfo.serverName + " address-" + serverInfo.addr + ": fail  " +
                                "\n--------------------------------------------");
                return "exception";
            }
        }

        if(result.isEmpty()){
            System.out.println(
                    "\n--------------------------------------------" +
                            "\n>> Execution process success ... " +
                            "\n--------------------------------------------");
            result = "success";
        }
        return result;
    }

    public boolean addSaleIAP(int serverID, InfoIAPSale saleData) {
        if (serverID <= 0) {
            mapServer.values().forEach(server -> {
                try {
                    ServerGateIAP.addSaleIAP(server.addr, gameThriftPort, server.serverName, saleData);
                } catch (TException e) {
                    e.printStackTrace();
                    System.out.println(
                            "\n--------------------------------------------" +
                                    "\n>> Process server-" + server.serverName + " address-" + server.addr + ": fail  " +
                                    "\n--------------------------------------------");
                }
            });
        } else {
            ServerInfo serverInfo = getServerInfo(serverID);
            if(serverInfo == null){
                System.out.println(
                        "\n--------------------------------------------" +
                                "\n>> Server isn't exsist  " +
                                "\n--------------------------------------------");
                return false;
            }
            try {
                if(ServerGateIAP.addSaleIAP(serverInfo.addr, gameThriftPort, serverInfo.serverName, saleData)){
                    System.out.println(
                            "\n--------------------------------------------" +
                                    "\n>> Execution process success ... " +
                                    "\n--------------------------------------------");
                    return true;
                }else {
                    System.out.println(
                            "\n--------------------------------------------" +
                                    "\n>> Process server-" + serverInfo.serverName + " address-" + serverInfo.addr + ": fail  " +
                                    "\n--------------------------------------------");
                    return false;
                }
            } catch (TException e) {
                e.printStackTrace();
                System.out.println(
                        "\n--------------------------------------------" +
                                "\n>> Process server-" + serverInfo.serverName + " address-" + serverInfo.addr + ": fail  " +
                                "\n--------------------------------------------");
                return false;
            }
        }
        System.out.println(
                "\n--------------------------------------------" +
                        "\n>> Execution process success ... " +
                        "\n--------------------------------------------");
        return true;
    }

    public boolean removeSaleIAP(int serverID, List<String> listIdSale) {
        if (serverID <= 0) {
            mapServer.values().forEach(server -> {
                try {
                    ServerGateIAP.removeSaleIAP(server.addr, gameThriftPort, server.serverName, listIdSale);
                } catch (TException e) {
                    e.printStackTrace();
                    System.out.println(
                            "\n--------------------------------------------" +
                                    "\n>> Process server-" + server.serverName + " address-" + server.addr + ": fail  " +
                                    "\n--------------------------------------------");
                }
            });
        } else {
            ServerInfo serverInfo = getServerInfo(serverID);
            if(serverInfo == null){
                System.out.println(
                        "\n--------------------------------------------" +
                                "\n>> Server isn't exsist  " +
                                "\n--------------------------------------------");
                return false;
            }
            try {
                if(ServerGateIAP.removeSaleIAP(serverInfo.addr, gameThriftPort, serverInfo.serverName, listIdSale)){
                    System.out.println(
                            "\n--------------------------------------------" +
                                    "\n>> Execution process success ... " +
                                    "\n--------------------------------------------");
                    return true;
                }else {
                    System.out.println(
                            "\n--------------------------------------------" +
                                    "\n>> Process server-" + serverInfo.serverName + " address-" + serverInfo.addr + ": fail  " +
                                    "\n--------------------------------------------");
                    return false;
                }
            } catch (TException e) {
                e.printStackTrace();
                System.out.println(
                        "\n--------------------------------------------" +
                                "\n>> Process server-" + serverInfo.serverName + " address-" + serverInfo.addr + ": fail  " +
                                "\n--------------------------------------------");
                return false;
            }
        }
        System.out.println(
                "\n--------------------------------------------" +
                        "\n>> Execution process success ... " +
                        "\n--------------------------------------------");
        return true;
    }

    public int getServerCount() {
        return mapServer.keySet().size();
    }

    public String getJoinedServer(String accountID) {
        JoinedServerData joinedServerData = new JoinedServerData(AccountModel.copyFromDB(accountID, SDKDatacontroler.getInstance()).joinedServerInfo);
        return Utils.toJson(joinedServerData);
    }

    public boolean updateCodeRefferalUser(String codeOld, String codeNew) {
        System.out.println(
                "\n--------------------------------------------" +
                        "\n>> Execution process success ... " +
                        "\n--------------------------------------------");
        return InviteManager.getInstance().updateInviteCodeUser(codeOld, codeNew);
    }

    public boolean addEventGeneral(int serverID, InfoEventNoti eventData){
        if (serverID <= 0) {
            mapServer.values().forEach(server -> {
                try {
                    ServerGateEvent.addEventGeneral(server.addr, gameThriftPort, server.serverName, eventData);
                } catch (TException e) {
                    e.printStackTrace();
                    System.out.println(
                            "\n--------------------------------------------" +
                                    "\n>> Process server-" + server.serverName + " address-" + server.addr + ": fail  " +
                                    "\n--------------------------------------------");
                }
            });
        } else {
            ServerInfo serverInfo = getServerInfo(serverID);
            if(serverInfo == null){
                System.out.println(
                        "\n--------------------------------------------" +
                                "\n>> Server isn't exsist  " +
                                "\n--------------------------------------------");
                return false;
            }
            try {
                if(ServerGateEvent.addEventGeneral(serverInfo.addr, gameThriftPort, serverInfo.serverName, eventData)){
                    System.out.println(
                            "\n--------------------------------------------" +
                                    "\n>> Execution process success ... " +
                                    "\n--------------------------------------------");
                    return true;
                }else {
                    System.out.println(
                            "\n--------------------------------------------" +
                                    "\n>> Process server-" + serverInfo.serverName + " address-" + serverInfo.addr + ": fail  " +
                                    "\n--------------------------------------------");
                    return false;
                }
            } catch (TException e) {
                e.printStackTrace();
                System.out.println(
                        "\n--------------------------------------------" +
                                "\n>> Process server-" + serverInfo.serverName + " address-" + serverInfo.addr + ": fail  " +
                                "\n--------------------------------------------");
                return false;
            }
        }
        System.out.println(
                "\n--------------------------------------------" +
                        "\n>> Execution process success ... " +
                        "\n--------------------------------------------");
        return true;
    }

    public boolean removeEventGeneral(int serverID, List<String> listIdEvent) {
        if (serverID <= 0) {
            mapServer.values().forEach(server -> {
                try {
                    ServerGateEvent.removeEventGeneral(server.addr, gameThriftPort, server.serverName, listIdEvent);
                } catch (TException e) {
                    e.printStackTrace();
                    System.out.println(
                            "\n--------------------------------------------" +
                                    "\n>> Process server-" + server.serverName + " address-" + server.addr + ": fail  " +
                                    "\n--------------------------------------------");
                }
            });
        } else {
            ServerInfo serverInfo = getServerInfo(serverID);
            if(serverInfo == null){
                System.out.println(
                        "\n--------------------------------------------" +
                                "\n>> Server isn't exsist  " +
                                "\n--------------------------------------------");
                return false;
            }
            try {
                if(ServerGateEvent.removeEventGeneral(serverInfo.addr, gameThriftPort, serverInfo.serverName, listIdEvent)){
                    System.out.println(
                            "\n--------------------------------------------" +
                                    "\n>> Execution process success ... " +
                                    "\n--------------------------------------------");
                    return true;
                }else {
                    System.out.println(
                            "\n--------------------------------------------" +
                                    "\n>> Process server-" + serverInfo.serverName + " address-" + serverInfo.addr + ": fail  " +
                                    "\n--------------------------------------------");
                    return false;
                }
            } catch (TException e) {
                e.printStackTrace();
                System.out.println(
                        "\n--------------------------------------------" +
                                "\n>> Process server-" + serverInfo.serverName + " address-" + serverInfo.addr + ": fail  " +
                                "\n--------------------------------------------");
                return false;
            }
        }
        System.out.println(
                "\n--------------------------------------------" +
                        "\n>> Execution process success ... " +
                        "\n--------------------------------------------");
        return true;
    }

    public boolean addEventSpecial(int serverID, InfoEventNoti eventData){
        if (serverID <= 0) {
            mapServer.values().forEach(server -> {
                try {
                    ServerGateEvent.addEventSpecial(server.addr, gameThriftPort, server.serverName, eventData);
                } catch (TException e) {
                    e.printStackTrace();
                    System.out.println(
                            "\n--------------------------------------------" +
                                    "\n>> Process server-" + server.serverName + " address-" + server.addr + ": fail  " +
                                    "\n--------------------------------------------");
                }
            });
        } else {
            ServerInfo serverInfo = getServerInfo(serverID);
            if(serverInfo == null){
                System.out.println(
                        "\n--------------------------------------------" +
                                "\n>> Server isn't exsist  " +
                                "\n--------------------------------------------");
                return false;
            }
            try {
                if(ServerGateEvent.addEventSpecial(serverInfo.addr, gameThriftPort, serverInfo.serverName, eventData)){
                    System.out.println(
                            "\n--------------------------------------------" +
                                    "\n>> Execution process success ... " +
                                    "\n--------------------------------------------");
                    return true;
                }else {
                    System.out.println(
                            "\n--------------------------------------------" +
                                    "\n>> Process server-" + serverInfo.serverName + " address-" + serverInfo.addr + ": fail  " +
                                    "\n--------------------------------------------");
                    return false;
                }
            } catch (TException e) {
                e.printStackTrace();
                System.out.println(
                        "\n--------------------------------------------" +
                                "\n>> Process server-" + serverInfo.serverName + " address-" + serverInfo.addr + ": fail  " +
                                "\n--------------------------------------------");
                return false;
            }
        }
        System.out.println(
                "\n--------------------------------------------" +
                        "\n>> Execution process success ... " +
                        "\n--------------------------------------------");
        return true;
    }

    public boolean removeEventSpecial(int serverID, List<String> listIdEvent) {
        if (serverID <= 0) {
            mapServer.values().forEach(server -> {
                try {
                    ServerGateEvent.removeEventSpecial(server.addr, gameThriftPort, server.serverName, listIdEvent);
                } catch (TException e) {
                    e.printStackTrace();
                    System.out.println(
                            "\n--------------------------------------------" +
                                    "\n>> Process server-" + server.serverName + " address-" + server.addr + ": fail  " +
                                    "\n--------------------------------------------");
                }
            });
        } else {
            ServerInfo serverInfo = getServerInfo(serverID);
            if(serverInfo == null){
                System.out.println(
                        "\n--------------------------------------------" +
                                "\n>> Server isn't exsist  " +
                                "\n--------------------------------------------");
                return false;
            }
            try {
                if(ServerGateEvent.removeEventSpecial(serverInfo.addr, gameThriftPort, serverInfo.serverName, listIdEvent)){
                    System.out.println(
                            "\n--------------------------------------------" +
                                    "\n>> Execution process success ... " +
                                    "\n--------------------------------------------");
                    return true;
                }else {
                    System.out.println(
                            "\n--------------------------------------------" +
                                    "\n>> Process server-" + serverInfo.serverName + " address-" + serverInfo.addr + ": fail  " +
                                    "\n--------------------------------------------");
                    return false;
                }
            } catch (TException e) {
                e.printStackTrace();
                System.out.println(
                        "\n--------------------------------------------" +
                                "\n>> Process server-" + serverInfo.serverName + " address-" + serverInfo.addr + ": fail  " +
                                "\n--------------------------------------------");
                return false;
            }
        }
        System.out.println(
                "\n--------------------------------------------" +
                        "\n>> Execution process success ... " +
                        "\n--------------------------------------------");
        return true;
    }


    public boolean sendGiftArenaDaily(int serverID) {
        if (serverID <= 0) {
            mapServer.values().forEach(server -> {
                try {
                    ServerGateArena.sendGiftArenaDaily(server.addr, gameThriftPort, server.serverName);
                } catch (TException e) {
                    e.printStackTrace();
                    System.out.println(
                            "\n--------------------------------------------" +
                                    "\n>> Process server-" + server.serverName + " address-" + server.addr + ": fail  " +
                                    "\n--------------------------------------------");
                }
            });
        } else {
            ServerInfo serverInfo = getServerInfo(serverID);
            if(serverInfo == null){
                System.out.println(
                        "\n--------------------------------------------" +
                                "\n>> Server isn't exsist  " +
                                "\n--------------------------------------------");
                return false;
            }
            try {
                if(ServerGateArena.sendGiftArenaDaily(serverInfo.addr, gameThriftPort, serverInfo.serverName)){
                    System.out.println(
                            "\n--------------------------------------------" +
                                    "\n>> Execution process success ... " +
                                    "\n--------------------------------------------");
                    return true;
                }else {
                    System.out.println(
                            "\n--------------------------------------------" +
                                    "\n>> Process server-" + serverInfo.serverName + " address-" + serverInfo.addr + ": fail  " +
                                    "\n--------------------------------------------");
                    return false;
                }
            } catch (TException e) {
                e.printStackTrace();
                System.out.println(
                        "\n--------------------------------------------" +
                                "\n>> Process server-" + serverInfo.serverName + " address-" + serverInfo.addr + ": fail  " +
                                "\n--------------------------------------------");
                return false;
            }
        }
        System.out.println(
                "\n--------------------------------------------" +
                        "\n>> Execution process success ... " +
                        "\n--------------------------------------------");
        return true;
    }


    public boolean closeSeasonArena(int serverID) {
        if (serverID <= 0) {
            mapServer.values().forEach(server -> {
                try {
                    ServerGateArena.closeSeasonArena(server.addr, gameThriftPort, server.serverName);
                } catch (TException e) {
                    e.printStackTrace();
                    System.out.println(
                            "\n--------------------------------------------" +
                                    "\n>> Process server-" + server.serverName + " address-" + server.addr + ": fail  " +
                                    "\n--------------------------------------------");
                }
            });
        } else {
            ServerInfo serverInfo = getServerInfo(serverID);
            if(serverInfo == null){
                System.out.println(
                        "\n--------------------------------------------" +
                                "\n>> Server isn't exsist  " +
                                "\n--------------------------------------------");
                return false;
            }

            try {
                if(ServerGateArena.closeSeasonArena(serverInfo.addr, gameThriftPort, serverInfo.serverName)){
                    System.out.println(
                            "\n--------------------------------------------" +
                                    "\n>> Execution process success ... " +
                                    "\n--------------------------------------------");
                    return true;
                }else {
                    System.out.println(
                            "\n--------------------------------------------" +
                                    "\n>> Process server-" + serverInfo.serverName + " address-" + serverInfo.addr + ": fail  " +
                                    "\n--------------------------------------------");
                    return false;
                }
            } catch (TException e) {
                e.printStackTrace();
                System.out.println(
                        "\n--------------------------------------------" +
                                "\n>> Process server-" + serverInfo.serverName + " address-" + serverInfo.addr + ": fail  " +
                                "\n--------------------------------------------");
                return false;
            }
        }
        System.out.println(
                "\n--------------------------------------------" +
                        "\n>> Execution process success ... " +
                        "\n--------------------------------------------");
        return true;
    }


    public boolean openSeasonArena(int serverID) {
        if (serverID <= 0) {
            mapServer.values().forEach(server -> {
                try {
                    ServerGateArena.openSeasonArena(server.addr, gameThriftPort, server.serverName);
                } catch (TException e) {
                    e.printStackTrace();
                    System.out.println(
                            "\n--------------------------------------------" +
                                    "\n>> Process server-" + server.serverName + " address-" + server.addr + ": fail  " +
                                    "\n--------------------------------------------");
                }
            });
        } else {
            ServerInfo serverInfo = getServerInfo(serverID);
            if(serverInfo == null){
                System.out.println(
                        "\n--------------------------------------------" +
                                "\n>> Server isn't exsist  " +
                                "\n--------------------------------------------");
                return false;
            }
            try {
                if(ServerGateArena.openSeasonArena(serverInfo.addr, gameThriftPort, serverInfo.serverName)){
                    System.out.println(
                            "\n--------------------------------------------" +
                                    "\n>> Execution process success ... " +
                                    "\n--------------------------------------------");
                    return true;
                }else {
                    System.out.println(
                            "\n--------------------------------------------" +
                                    "\n>> Process server-" + serverInfo.serverName + " address-" + serverInfo.addr + ": fail  " +
                                    "\n--------------------------------------------");
                    return false;
                }
            } catch (TException e) {
                e.printStackTrace();
                System.out.println(
                        "\n--------------------------------------------" +
                                "\n>> Process server-" + serverInfo.serverName + " address-" + serverInfo.addr + ": fail  " +
                                "\n--------------------------------------------");
                return false;
            }
        }
        System.out.println(
                "\n--------------------------------------------" +
                        "\n>> Execution process success ... " +
                        "\n--------------------------------------------");
        return true;
    }

    public boolean buyPackageIAP(int serverID, long uid, String specialPackageID){
        ServerInfo serverInfo = getServerInfo(serverID);
        if(serverInfo == null) return false;

        try {
            return ServerGateIAP.buyIAP(serverInfo.addr, gameThriftPort, serverInfo.serverName, uid, specialPackageID);
        } catch (TException e) {
            e.printStackTrace();
            return false;
        }
    }
}
