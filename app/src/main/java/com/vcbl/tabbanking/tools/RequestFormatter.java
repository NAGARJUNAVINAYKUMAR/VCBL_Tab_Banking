package com.vcbl.tabbanking.tools;

import com.vcbl.tabbanking.models.AgentLoginModel;
import com.vcbl.tabbanking.protobuff.AgentLoginRequest;

/**
 * Created by Balajee on 01-09-2017.
 */

public class RequestFormatter {
    
    private AgentLoginRequest agentLoginRequest;

    public AgentLoginModel convertToModel(AgentLoginRequest.AgentLoginReq protoAgentLogin)
    {
        AgentLoginModel agentLoginModel = new AgentLoginModel();
        agentLoginModel.setTimeStamp(protoAgentLogin.getTimeStamp());
        agentLoginModel.setBiometric(protoAgentLogin.getBiometric());
        agentLoginModel.setPin(protoAgentLogin.getPin());
        agentLoginModel.setLoginId(protoAgentLogin.getLoginId());
        agentLoginModel.setTerminalId(protoAgentLogin.getTerminalId());
        return agentLoginModel;

    }

}
