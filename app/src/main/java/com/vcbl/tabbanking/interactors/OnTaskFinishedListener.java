package com.vcbl.tabbanking.interactors;

import com.vcbl.tabbanking.models.AgentLoginModel;
import com.vcbl.tabbanking.models.BCEnrollmentModel;
import com.vcbl.tabbanking.models.TxnModel;
import com.vcbl.tabbanking.protobuff.AgentLoginResponse;
import com.vcbl.tabbanking.protobuff.BCEnrolment;
import com.vcbl.tabbanking.protobuff.TxnMessage;

/**
 * Created by ecosoft2 on 27-Feb-18.
 */

public interface OnTaskFinishedListener {
    void onTaskFinished(AgentLoginModel agentLoginRes);
}
