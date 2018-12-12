package com.vcbl.tabbanking.storage;

public class Constants {
	// sharedPreferences
	public static final String APP_LAUNCH = "APP_LAUNCH";
	public static final String BT_NAME = "BT_NAME";
	public static final String BT_ADDRESS = "BT_ADDRESS";
	public static final String BT_NAME_OTHER = "BT_NAME_OTHER";
	public static final String BT_ADDRESS_OTHER = "BT_ADDRESS_OTHER";
	public static final String LEOPARD_ACTIVATE = "LEOPARD_ACTIVATE";
	public static final String SYNC_URL = "SYNC_URL";
	public static final String TXT_URL = "TXT_URL";
	public static final String LOGIN_TYPE = "LOGIN_TYPE";
	public static final String FRAGMENT_NAVIGATION = "FRAGMENT_NAVIGATION";
	public static final String SYNC_PROCESS = "SYNC_PROCESS";
	public static final String CUSTOMER_AGE = "CUSTOMER_AGE";
	public static final String BT_SERVICE = "BT_SERVICE";

	// copied from tabbanking_old
	public static final int AGENT_ID = 9;
	public static final String MICRO_ATM_TAG = "Echosoft Micro Atm : ";
	public static final String APP_VERSION = "0.001";

	public final static String PROTO_VERSION = "000001.0";
	public final static String DB_NAME = "vcbltab.db";
	public static final String PROTO_HEADER = "PTB";
	public static final String PROTO_IDENTIFIER_SYNC = "113";
	public static final String PROTO_IDENTIFIER_BOD = "111";
	public static final String PROTO_IDENTIFIER_EOD = "112";
	public static final String PROTO_IDENTIFIER_VERSION = "114";
	public static final String PROTO_TYPE_REQUEST = "01";
	public static final String PROTO_TYPE_RESPONSE = "02";
	public static final String PROTO_IDENTIFIER_EKYC = "102";
	public static final String PROTO_IDENTIFIER_LOGIN = "116";
	public static final String PROTO_IDENTIFIER_BC_ENROLL = "118";
	public static final String PROTO_IDENTIFIER_TXN_BAL_ENQ = "119";
	public static final String PROTO_IDENTIFIER_TXN_BAL_SUBMIT = "120";
	public static final String PROTO_IDENTIFIER_AADHAAR_FEEDING = "121";
	public static final String PROTO_IDENTIFIER_LOAN_HISTORY = "122";
	public static final String PROTO_IDENTIFIER_TXN_REPORTS = "123";
	public static final String PROTO_IDENTIFIER_TXN_CANCELLATION = "128";
	public static final String PROTO_IDENTIFIER_TXN_SUMMARY = "129";
	public static final String PROTO_IDENTIFIER_BC_ENROLL_STATUS = "124";
	public static final String PROTO_IDENTIFIER_NEW_CUSTOMER = "126";
    public static final String PRINT_STATUS = "PRINT_STATUS" ;
	public static final String PRINT_FROM = "PRINT_FROM" ;
    public static final String APP_FIRST_TIME_LAUNCH = "APP_FIRST_TIME_LAUNCH";
	public static final String DEVICE_TYPE = "DEVICE_TYPE";

    public static String syncUrl = "http://192.168.43.35:80/VCBLApp/api/mastersync/postsyncdata";
	public static String txnUrl = "http://192.168.43.35:80/VCBLApp/api/Transaction/PostTranData";

	public static String sync = "/api/mastersync/postsyncdata";
	public static String txn = "/api/Transaction/PostTranData";

	public static final String DepositProcCode = "492010";
	public static final String BalanceEnqryProcCode = "311000";
	public static final String AadhaarSeedingProcCode = "151000";
	public static final String MobileSeedingProcCode = "141000";
	public static final String panUpdateProcCode = "161000";
	public static final String LoanHistoryProcCode = "121000";
	public static final String withdrawalProcCode = "480000";
	public static final int txnserviceid = 0;
	public static final int txnsubserviceid = 1;
	public static final int aadhaarTxnsTxnServiceID = 1;
	public static final int rupayCardTxnServiceID = 2;

	// bundle values
	public static final String accountNo = "accountNo";
	public static final String productCode = "productCode";

}
