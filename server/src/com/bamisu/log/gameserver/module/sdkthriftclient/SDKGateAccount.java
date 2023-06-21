package com.bamisu.log.gameserver.module.sdkthriftclient;

import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.log.gameserver.module.vip.VipType;
import com.bamisu.log.sdkthrift.entities.*;
import com.bamisu.log.sdkthrift.exception.SDKThriftError;
import com.bamisu.log.sdkthrift.exception.ThriftSVException;
import com.bamisu.log.sdkthrift.service.account.AccountService;
import com.bamisu.gamelib.socialnetwork.ESocialNetwork;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;

import java.util.HashMap;
import java.util.Map;

/**
 * Create by Popeye on 11:21 AM, 4/25/2020
 */
public class SDKGateAccount {
    public static final String serviceName = "account";

    @WithSpan
    public static TLoginResult loginGame(String token, int serverID, String clientIP, int os, String did) throws TException {
        TLoginResult loginResult = null;
        TProtocol protocol = null;
        try {
            protocol = SDKThriftClient.createProtocol();
            AccountService.Client client = (AccountService.Client) SDKThriftClient.getServiceClient(protocol, serviceName);
            loginResult = client.loginGame(token, serverID, clientIP, os, did);
            protocol.getTransport().close();

            if (protocol != null) {
                protocol.getTransport().close();
            }
        } catch (TException e) {
            e.printStackTrace();
            if (protocol != null) {
                protocol.getTransport().close();
            }
        }

        return loginResult;
    }

    @WithSpan
    public static TLinkAccountResult linkAccount(String accountID, long userID, int serverID, ESocialNetwork socialNetwork, String socialNetworkToken) throws TException {
        TLinkAccountResult linkAccountResult = null;
        TProtocol protocol = null;
        try {
            protocol = SDKThriftClient.createProtocol();
            AccountService.Client client = (AccountService.Client) SDKThriftClient.getServiceClient(protocol, serviceName);
            linkAccountResult = client.linkAccount(accountID, userID, serverID, socialNetwork.getIntValue(), socialNetworkToken);
            protocol.getTransport().close();

            if (protocol != null) {
                protocol.getTransport().close();
            }
        } catch (TException e) {
            e.printStackTrace();
            if (protocol != null) {
                protocol.getTransport().close();
            }
            throw e;
        }

        return linkAccountResult;
    }

    @WithSpan
    public static TSwitchAccountResult switchAccount(ESocialNetwork socialNetwork, String socialNetworkToken) {
        TSwitchAccountResult switchAccountResult = null;
        TProtocol protocol = null;
        try {
            protocol = SDKThriftClient.createProtocol();
            AccountService.Client client = (AccountService.Client) SDKThriftClient.getServiceClient(protocol, serviceName);
            switchAccountResult = client.switchAccount(socialNetwork.getIntValue(), socialNetworkToken);
            protocol.getTransport().close();

            if (protocol != null) {
                protocol.getTransport().close();
            }
        } catch (TException e) {
            e.printStackTrace();
            if (protocol != null) {
                protocol.getTransport().close();
            }
        }

        return switchAccountResult;
    }

    @WithSpan
    public static void updateLevel(String accountID, int serverID, int level)  throws ThriftSVException, TException {
        TProtocol protocol = null;
        try {
            protocol = SDKThriftClient.createProtocol();
            AccountService.Client client = (AccountService.Client) SDKThriftClient.getServiceClient(protocol, serviceName);
            client.updateLevel(accountID, serverID, level);
            protocol.getTransport().close();
            if (protocol != null) {
                protocol.getTransport().close();
            }
        } catch (TException e) {
            e.printStackTrace();
            if (protocol != null) {
                protocol.getTransport().close();
            }
        }
    }

    @WithSpan
    public static void updateAvatar(String accountID, int serverID, String avatar, int frame) throws ThriftSVException, TException {
        TProtocol protocol = null;
        try {
            protocol = SDKThriftClient.createProtocol();
            AccountService.Client client = (AccountService.Client) SDKThriftClient.getServiceClient(protocol, serviceName);
            client.updateAvatar(accountID, serverID, avatar, frame);
            protocol.getTransport().close();
            if (protocol != null) {
                protocol.getTransport().close();
            }
        } catch (TException e) {
            e.printStackTrace();
            if (protocol != null) {
                protocol.getTransport().close();
            }
        }
    }

    @WithSpan
    public static void updateDisplayName(String accountID, int serverID, String displayName) throws ThriftSVException, TException {
        TProtocol protocol = null;
        try {
            protocol = SDKThriftClient.createProtocol();
            AccountService.Client client = (AccountService.Client) SDKThriftClient.getServiceClient(protocol, serviceName);
            client.updateDisplayName(accountID, serverID, displayName);
            protocol.getTransport().close();
            if (protocol != null) {
                protocol.getTransport().close();
            }
        } catch (TException e) {
            e.printStackTrace();
            if (protocol != null) {
                protocol.getTransport().close();
            }
        }
    }

    @WithSpan
    public static void joinServer(UserModel userModel, int serverID) {
        TProtocol protocol = null;
        try {
            protocol = SDKThriftClient.createProtocol();
            AccountService.Client client = (AccountService.Client) SDKThriftClient.getServiceClient(protocol, serviceName);
            client.joinServer(userModel.accountID, serverID, userModel.userID, userModel.displayName, userModel.avatar, userModel.avatarFrame, 1);
            protocol.getTransport().close();
            if (protocol != null) {
                protocol.getTransport().close();
            }
        } catch (TException e) {
            e.printStackTrace();
            if (protocol != null) {
                protocol.getTransport().close();
            }
        }
    }

    @WithSpan
    public static void logIAP(String accountID, int serverId, long userID, int gate, String idAPI, String purchaseToken, String transactionId) {
        TProtocol protocol = null;
        try {
            protocol = SDKThriftClient.createProtocol();
            AccountService.Client client = (AccountService.Client) SDKThriftClient.getServiceClient(protocol, serviceName);
            client.logIAP(accountID, serverId, userID, gate, idAPI, purchaseToken, transactionId);
            protocol.getTransport().close();
            if (protocol != null) {
                protocol.getTransport().close();
            }
        } catch (TException e) {
            e.printStackTrace();
            if (protocol != null) {
                protocol.getTransport().close();
            }
        }
    }

    @WithSpan
    public static String getUniqueIdSocialNetwork(String accountId, int socialNetwork) {
        String result = null;
        TProtocol protocol = null;
        try {
            protocol = SDKThriftClient.createProtocol();
            AccountService.Client client = (AccountService.Client) SDKThriftClient.getServiceClient(protocol, serviceName);
            result = client.getUniqueIdSocialNetwork(accountId, socialNetwork);
            protocol.getTransport().close();
            if (protocol != null) {
                protocol.getTransport().close();
            }
        } catch (TException e) {
            e.printStackTrace();
            if (protocol != null) {
                protocol.getTransport().close();
            }
        }
        return result;
    }

    @WithSpan
    public static long getUserId(String socialNetworkKey, int socialNetworkID, String serverId) {
        long result = -1;
        TProtocol protocol = null;
        try {
            protocol = SDKThriftClient.createProtocol();
            AccountService.Client client = (AccountService.Client) SDKThriftClient.getServiceClient(protocol, serviceName);
            result = client.getUserId(socialNetworkKey, socialNetworkID, serverId);
            protocol.getTransport().close();
            if (protocol != null) {
                protocol.getTransport().close();
            }
        } catch (TException e) {
            e.printStackTrace();
            if (protocol != null) {
                protocol.getTransport().close();
            }
        }
        return result;
    }

    @WithSpan
    public static TLoginResult usernameLoginGame(String username, String password, int serverID, String clientIP, int os, String did) throws TException {
        TLoginResult loginResult = null;
        TProtocol protocol = null;
        try {
            protocol = SDKThriftClient.createProtocol();
            AccountService.Client client = (AccountService.Client) SDKThriftClient.getServiceClient(protocol, serviceName);
            loginResult = client.usernameLoginGame(username, password, serverID, clientIP, os, did);
        }
        catch (TException e) {
            e.printStackTrace();
        } finally {
            if (protocol != null) {
                protocol.getTransport().close();
            }
        }

        return loginResult;
    }

    @WithSpan
    public static boolean updateUsernameAndPassword(String accountId, String username, String password, String email, String code) throws TException {
        TProtocol protocol = null;
        try {
            protocol = SDKThriftClient.createProtocol();
            AccountService.Client client = (AccountService.Client) SDKThriftClient.getServiceClient(protocol, serviceName);
            return client.updateUsernameAndPassword(accountId, username, password, email, code);
        }
        catch (TException e) {
            if (protocol != null) {
                protocol.getTransport().close();
            }

            throw e;
        } finally {
            if (protocol != null) {
                protocol.getTransport().close();
            }
        }
    }

    @WithSpan
    public static boolean changePassword(String accountId, String password) throws TException {
        TProtocol protocol = null;
        try {
            protocol = SDKThriftClient.createProtocol();
            AccountService.Client client = (AccountService.Client) SDKThriftClient.getServiceClient(protocol, serviceName);
            return client.changePassword(accountId, password);
        }
        catch (TException e) {
            if (protocol != null) {
                protocol.getTransport().close();
            }

            throw e;
        } finally {
            if (protocol != null) {
                protocol.getTransport().close();
            }
        }
    }

    @WithSpan
    public static String getUsername(String accountId) throws TException {
        TProtocol protocol = null;
        try {
            protocol = SDKThriftClient.createProtocol();
            AccountService.Client client = (AccountService.Client) SDKThriftClient.getServiceClient(protocol, serviceName);
            return client.getUsername(accountId);
        }
        catch (TException e) {
            if (protocol != null) {
                protocol.getTransport().close();
            }
        } finally {
            if (protocol != null) {
                protocol.getTransport().close();
            }
        }

        return "";
    }

    @WithSpan
    public static String getWalletByAccountId(String accountId) throws TException {
        TProtocol protocol = null;
        try {
            protocol = SDKThriftClient.createProtocol();
            AccountService.Client client = (AccountService.Client) SDKThriftClient.getServiceClient(protocol, serviceName);
            return client.getWalletByAccountId(accountId);
        }
        catch (TException e) {
            if (protocol != null) {
                protocol.getTransport().close();
            }

            throw e;
        } finally {
            if (protocol != null) {
                protocol.getTransport().close();
            }
        }
    }

    @WithSpan
    public static long linkWallet(String accountId, String address, String username, String password, int zoneId) throws TException {
        TProtocol protocol = null;
        try {
            protocol = SDKThriftClient.createProtocol();
            AccountService.Client client = (AccountService.Client) SDKThriftClient.getServiceClient(protocol, serviceName);
            return client.linkWallet(accountId, address, username, password, zoneId);
        }
        catch (TException e) {
            if (protocol != null) {
                protocol.getTransport().close();
            }

            throw e;
        } finally {
            if (protocol != null) {
                protocol.getTransport().close();
            }
        }
    }

    @WithSpan
    public static Map<String, String> getLinkedAccount(String accountId) throws TException {
        TProtocol protocol = null;
        Map<String, String> res = new HashMap<>();
        try {
            protocol = SDKThriftClient.createProtocol();
            AccountService.Client client = (AccountService.Client) SDKThriftClient.getServiceClient(protocol, serviceName);
            TLinkedAccount linkedAccount = client.getLinkedAccount(accountId);
            res.put(Params.USER_NAME, linkedAccount.username);
            res.put(Params.USER_EMAIL, linkedAccount.email);
            res.put(Params.WALLET, linkedAccount.walletAddress);
        }
        catch (TException e) {
            if (protocol != null) {
                protocol.getTransport().close();
            }

            throw e;
        } finally {
            if (protocol != null) {
                protocol.getTransport().close();
            }
        }

        return res;
    }

    @WithSpan
    public static TUserInfo getUserInfo(String address) throws TException {
        TProtocol protocol = null;
        TUserInfo userInfo = null;
        try {
            protocol = SDKThriftClient.createProtocol();
            AccountService.Client client = (AccountService.Client) SDKThriftClient.getServiceClient(protocol, serviceName);
            userInfo = client.getUserInfo(address);
        }
        catch (TException e) {
            if (protocol != null) {
                protocol.getTransport().close();
            }

            throw e;
        } finally {
            if (protocol != null) {
                protocol.getTransport().close();
            }
        }

        return userInfo;
    }
}
