package com.vcbl.tabbanking.database;

import android.content.Context;

import com.vcbl.tabbanking.models.AgentLoginModel;
import com.vcbl.tabbanking.protobuff.AgentLoginRequest;
import com.vcbl.tabbanking.tools.RequestFormatter;


/**
 * Created by Balajee on 01-09-2017.
 */

public class DbHelper {


    private DbFunctions dbFunctions;
    static private  DbHelper dbHelper = null;
    RequestFormatter requestFormatter;


    private DbHelper(Context context)
    {
          dbFunctions = DbFunctions.getInstance(context);
        requestFormatter = new RequestFormatter();
    }
//==================================================================================================

    static public DbHelper getInstance(Context context)
    {
        if(null== dbHelper)
        {
            dbHelper = new DbHelper(context);
        }
        return dbHelper;
    }
//==================================================================================================

    public boolean  insertUpdateAgentLoginIntoDb(AgentLoginRequest.AgentLoginReq agentLoginReqProto)
    {

            AgentLoginModel agentLoginModel = requestFormatter.convertToModel(agentLoginReqProto);
           /* if( SyncResponse.Active.INACTIVE == nBinProto.getActive() )
            {
                if(false == dbFunctions.deleteNbin(nBinDbModel) )
                {
                    return false;
                }

            }

            if( null == dbFunctions.getNbinData(nBinDbModel.getNbin()) )
            {
                if (false == dbFunctions.insertNbin(nBinDbModel))
                {
                    return false;
                }

            }
            else
            {

                if(false == dbFunctions.updateNbin(nBinDbModel))
                {
                    return false;
                }

            }
*/

        return true;
    }
//==================================================================================================

    public String getStrError()
    {
           return dbFunctions.getStrError();

    }


}
