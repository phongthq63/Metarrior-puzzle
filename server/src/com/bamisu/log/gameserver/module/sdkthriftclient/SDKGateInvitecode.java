package com.bamisu.log.gameserver.module.sdkthriftclient;

import com.bamisu.log.gameserver.module.invite.entities.UpdateRewardDetail;
import com.bamisu.log.gameserver.module.invite.entities.UserInvite;
import com.bamisu.log.sdkthrift.service.invitecode.InviteService;
import com.bamisu.gamelib.utils.Utils;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;

import java.util.List;

public class SDKGateInvitecode {
    public static final String serviceName = "invite";

    @WithSpan
    public static boolean haveInputInviteCode(String accountID) throws TException {
        boolean result = true;
        TProtocol protocol = null;
        try {
            protocol = SDKThriftClient.createProtocol();
            InviteService.Client client = (InviteService.Client) SDKThriftClient.getServiceClient(protocol, serviceName);
            result =  client.haveInputInviteCode(accountID);
            protocol.getTransport().close();

            if (protocol != null) {
                protocol.getTransport().close();
            }
        }catch (TException e){
            e.printStackTrace();
            if (protocol != null) {
                protocol.getTransport().close();
            }
            throw e;
        }

        return result;
    }

    @WithSpan
    public static boolean haveExsistInviteCode(String inviteCode) throws TException {
        boolean result = false;
        TProtocol protocol = null;
        try {
            protocol = SDKThriftClient.createProtocol();
            InviteService.Client client = (InviteService.Client) SDKThriftClient.getServiceClient(protocol, serviceName);
            result =  client.haveExsistInviteCode(inviteCode);
            protocol.getTransport().close();

            if (protocol != null) {
                protocol.getTransport().close();
            }
        }catch (TException e){
            e.printStackTrace();
            if (protocol != null) {
                protocol.getTransport().close();
            }
            throw e;
        }

        return result;
    }

    @WithSpan
    public static UserInvite inputCodeUserInviteModel(String accountID, String inviteCode) throws TException {
        String result;
        TProtocol protocol = null;
        try {
            protocol = SDKThriftClient.createProtocol();
            InviteService.Client client = (InviteService.Client) SDKThriftClient.getServiceClient(protocol, serviceName);
            result =  client.inputInviteCode(accountID, inviteCode);
            protocol.getTransport().close();

            if (protocol != null) {
                protocol.getTransport().close();
            }
        }catch (TException e){
            e.printStackTrace();
            if (protocol != null) {
                protocol.getTransport().close();
            }
            throw e;
        }

        return (result == null || result.isEmpty()) ? null : Utils.fromJson(result, UserInvite.class);
    }

    @WithSpan
    public static UserInvite getUserInviteModel(String accountID) throws TException {
        String result;
        TProtocol protocol = null;
        try {
            protocol = SDKThriftClient.createProtocol();
            InviteService.Client client = (InviteService.Client) SDKThriftClient.getServiceClient(protocol, serviceName);
            result =  client.getUserInviteModel(accountID);
            protocol.getTransport().close();

            if (protocol != null) {
                protocol.getTransport().close();
            }
        }catch (TException e){
            e.printStackTrace();
            if (protocol != null) {
                protocol.getTransport().close();
            }
            throw e;
        }

        return (result == null || result.isEmpty()) ? null : Utils.fromJson(result, UserInvite.class);
    }

    @WithSpan
    public static boolean canRewardInviteBonus(String accountID, String idBonus, int point) throws TException {
        boolean result = false;
        TProtocol protocol = null;
        try {
            protocol = SDKThriftClient.createProtocol();
            InviteService.Client client = (InviteService.Client) SDKThriftClient.getServiceClient(protocol, serviceName);
            result =  client.canRewardInviteBonus(accountID, idBonus, point);
            protocol.getTransport().close();

            if (protocol != null) {
                protocol.getTransport().close();
            }
        }catch (TException e){
            e.printStackTrace();
            if (protocol != null) {
                protocol.getTransport().close();
            }
            throw e;
        }

        return result;
    }

    @WithSpan
    public static boolean rewardInviteBonus(String accountID, String idBonus, int point) throws TException {
        boolean result = false;
        TProtocol protocol = null;
        try {
            protocol = SDKThriftClient.createProtocol();
            InviteService.Client client = (InviteService.Client) SDKThriftClient.getServiceClient(protocol, serviceName);
            result =  client.rewardInviteBonus(accountID, idBonus, point);
            protocol.getTransport().close();

            if (protocol != null) {
                protocol.getTransport().close();
            }
        }catch (TException e){
            e.printStackTrace();
            if (protocol != null) {
                protocol.getTransport().close();
            }
            throw e;
        }

        return result;
    }

    @WithSpan
    public static boolean updateRewardInviteDetail(String accountID, List<UpdateRewardDetail> update) throws TException {
        boolean result = true;
        TProtocol protocol = null;
        try {
            protocol = SDKThriftClient.createProtocol();
            InviteService.Client client = (InviteService.Client) SDKThriftClient.getServiceClient(protocol, serviceName);
            result =  client.updateRewardInviteDetail(accountID, Utils.toJson(update));
            protocol.getTransport().close();

            if (protocol != null) {
                protocol.getTransport().close();
            }
        }catch (TException e){
            e.printStackTrace();
            if (protocol != null) {
                protocol.getTransport().close();
            }
            throw e;
        }

        return result;
    }
}
