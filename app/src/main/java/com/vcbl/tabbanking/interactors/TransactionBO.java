package com.vcbl.tabbanking.interactors;

import android.content.Context;

import com.vcbl.tabbanking.models.LoanHistoryModel;
import com.vcbl.tabbanking.models.TxnModel;
import com.vcbl.tabbanking.models.TxnResponse;
import com.vcbl.tabbanking.protobuff.Masters;

import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;
import com.vcbl.tabbanking.models.GlobalModel;
import com.vcbl.tabbanking.protobuff.Transaction;
import com.vcbl.tabbanking.protobuff.TxnMessage;
import com.vcbl.tabbanking.storage.Storage;
import com.vcbl.tabbanking.tools.CryptoHelper;
import com.vcbl.tabbanking.tools.UiHelper;
import com.vcbl.tabbanking.tools.Utility;
import com.vcbl.tabbanking.storage.Constants;

public class TransactionBO extends Protobuffbase implements Protobuffresponse {

    private static final String TAG = "TransactionBO-->";
    private UiHelper mUiHelper;
    public TxnModel txnModel;
    public TxnResponse txnResponseModel;
    public LoanHistoryModel loanHistoryModel;

    private OnTaskExecutionFinished onTaskExecutionFinished;
    public interface OnTaskExecutionFinished {
        void onTaskFinished();
    }

    public void setOnTaskFinishedEvent(OnTaskExecutionFinished onTaskFinishedEvent) {
        if (onTaskFinishedEvent != null) {
            this.onTaskExecutionFinished = onTaskFinishedEvent;
        }
    }

    public TransactionBO(Context context) {
        this.mContext = context;
        mUiHelper = new UiHelper(context);
    }

    public void transactionRequest(TxnModel txnModel) {
        this.txnModel = txnModel;
        Transaction.TransactionProto.Builder txnRequest = Transaction.TransactionProto.newBuilder();
        TxnMessage.TxnRequestProto.Builder txnRequestProtoBuilder = TxnMessage.TxnRequestProto.newBuilder();
        String processingCode = txnModel.getProcessingcode();
        String strIdentifier = null;
        txnRequest.setProcessingcode(processingCode);
        switch (processingCode) {
            case Constants.BalanceEnqryProcCode:
                txnRequest.setAmount("");
                strIdentifier = Constants.PROTO_IDENTIFIER_TXN_BAL_ENQ;
                break;
            case Constants.DepositProcCode:
                txnRequest.setCustname(txnModel.getCustName());
                txnRequest.setAmount(txnModel.getAmount());
                txnRequest.setAvlbalance(txnModel.getAvlbalance());
                txnRequest.setLedgbalance(txnModel.getLedgbalance());
                storage = new Storage(mContext);
                if ("BIOMETRIC".equals(storage.getValue(Constants.LOGIN_TYPE))) {
                    txnRequest.setResponsecode("");
                } else {
                    try {
                        txnRequest.setResponsecode(CryptoHelper.encrypt(txnModel.getPinNo()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                strIdentifier = Constants.PROTO_IDENTIFIER_TXN_BAL_SUBMIT;
                break;
            case Constants.LoanHistoryProcCode:
                txnRequest.setAmount("");
                strIdentifier = Constants.PROTO_IDENTIFIER_LOAN_HISTORY;
                break;
            case Constants.AadhaarSeedingProcCode:
                txnRequest.setAmount("");
                txnRequest.setCustno(txnModel.getCustno());
                txnRequest.setCustRefNo(txnModel.getCustRefNo());
                strIdentifier = Constants.PROTO_IDENTIFIER_AADHAAR_FEEDING;
                break;
            case Constants.MobileSeedingProcCode:
                txnRequest.setAmount("");
                txnRequest.setCustno(txnModel.getCustno());
                txnRequest.setCustRefNo(txnModel.getCustRefNo());
                strIdentifier = Constants.PROTO_IDENTIFIER_AADHAAR_FEEDING;
                break;
            case Constants.panUpdateProcCode:
                txnRequest.setAmount("");
                txnRequest.setCustno(txnModel.getCustno());
                txnRequest.setCustRefNo(txnModel.getCustRefNo());
                strIdentifier = Constants.PROTO_IDENTIFIER_AADHAAR_FEEDING;
                break;
            default:
                break;
        }
        txnRequest.setRrn(txnModel.getRrn());
        txnRequest.setTxndate(txnModel.getTxndate());
        txnRequest.setMicroatmid(Integer.parseInt(txnModel.getMicroatmid()));
        txnRequest.setAccountno(txnModel.getAccNo());
        txnRequest.setBcid(txnModel.getBcid());
        txnRequest.setBranchid(txnModel.getBranch_id());
        txnRequest.setTxnserviceid(txnModel.getTxnserviceid());
        txnRequest.setTxnsubserviceid(txnModel.getTxnsubserviceid());
        txnRequest.setProductcode(txnModel.getProductcode());
        txnRequest.setTxntype(txnModel.getTxnType());
        txnRequestProtoBuilder.setAppVersion(Constants.APP_VERSION);
        txnRequestProtoBuilder.setMicroatmid((GlobalModel.microatmid));
        txnRequestProtoBuilder.setTimestamp(Utility.getCurrentTimeStamp());
        txnRequestProtoBuilder.setTransactionProto(txnRequest);
        TxnMessage.TxnRequestProto txnReqProtobuild = txnRequestProtoBuilder.build();

        protobuffresponseDelegate = this;

        Storage storage = new Storage(mContext);
        if (storage.getValue(Constants.TXT_URL).length() > 0) {
            url = storage.getValue(Constants.TXT_URL);
            Log.i(TAG, "TXT_URL---> " + url);
        }

        generateProtoRequest(txnReqProtobuild.toByteArray(), strIdentifier, Constants.PROTO_TYPE_REQUEST);

        mUiHelper.showProgress("Please wait...");
    }

    @Override
    public void onTxnProtobufferFinished(boolean bResult, String strMessage, byte[] byProtoOutput) {
        mUiHelper.dismissProgress();
        if (!bResult) {
            mUiHelper.errorDialog(strMessage);
        } else {
            try {
                TxnMessage.TxnResponseProto txnResponse = TxnMessage.TxnResponseProto.parseFrom(byProtoOutput);
                txnModel = new TxnModel();
                txnResponseModel = new TxnResponse();
                txnResponseModel.setStatus(txnResponse.getStatus());
                txnResponseModel.setStatusDescription(txnResponse.getStatusDescription());
                if (txnResponse.getStatus() == Masters.Status.SUCCESS) {
                    if (txnResponse.getTransactionProto() != null) {
                        if (txnResponse.getTransactionProto().getCustname() != null
                                && !"".equals(txnResponse.getTransactionProto().getCustname())
                                && txnResponse.getTransactionProto().getCustname().length() > 0) {
                            txnModel.setCustName(txnResponse.getTransactionProto().getCustname());
                        } else {
                            txnModel.setCustName("");
                        }
                        if (txnResponse.getTransactionProto().getAvlbalance() != null
                                && !"".equals(txnResponse.getTransactionProto().getAvlbalance())
                                && txnResponse.getTransactionProto().getAvlbalance().length() > 0) {
                            txnModel.setAvlbalance(txnResponse.getTransactionProto().getAvlbalance());
                        } else {
                            txnModel.setAvlbalance("");
                        }
                        if (txnResponse.getTransactionProto().getLedgbalance() != null
                                && !"".equals(txnResponse.getTransactionProto().getLedgbalance())
                                && txnResponse.getTransactionProto().getLedgbalance().length() > 0) {
                            txnModel.setLedgbalance(txnResponse.getTransactionProto().getLedgbalance());
                        } else {
                            txnModel.setLedgbalance("");
                        }
                        if (txnResponse.getTransactionProto().getRrn() != null
                                && !"".equals(txnResponse.getTransactionProto().getRrn())
                                && txnResponse.getTransactionProto().getRrn().length() > 0) {
                            txnModel.setRrn(txnResponse.getTransactionProto().getRrn());
                        } else {
                            txnModel.setRrn("");
                        }
                        if (txnResponse.getTransactionProto().getTxnStatus() > 0) {
                            txnModel.setTxnStatus(String.valueOf(txnResponse.getTransactionProto().getTxnStatus()));
                        } else {
                            txnModel.setTxnStatus("");
                        }
                        if (txnResponse.getTransactionProto().getResponsecode() != null
                                && !"".equals(txnResponse.getTransactionProto().getResponsecode())
                                && txnResponse.getTransactionProto().getResponsecode().length() > 0) {
                            txnModel.setResponsecode(txnResponse.getTransactionProto().getResponsecode());
                        } else {
                            txnModel.setResponsecode("");
                        }
                        if (txnResponse.getTransactionProto().getAccountStatus() > 0) {
                            txnModel.setAccountStatus(txnResponse.getTransactionProto().getAccountStatus());
                        } else {
                            txnModel.setAccountStatus(0);
                        }
                        if (txnResponse.getTransactionProto().getAccountType() > 0) {
                            txnModel.setAccountType(txnResponse.getTransactionProto().getAccountType());
                        } else {
                            txnModel.setAccountType(0);
                        }
                        if (txnResponse.getTransactionProto().getAccountStatusDesc() != null
                                && !"".equals(txnResponse.getTransactionProto().getAccountStatusDesc())
                                && txnResponse.getTransactionProto().getAccountStatusDesc().length() > 0) {
                            txnModel.setAccountStatusDesc(txnResponse.getTransactionProto().getAccountStatusDesc());
                        } else {
                            txnModel.setAccountStatusDesc("");
                        }
                        if (txnResponse.getTransactionProto().getAccountTypeDesc() != null
                                && !"".equals(txnResponse.getTransactionProto().getAccountTypeDesc())
                                && txnResponse.getTransactionProto().getAccountTypeDesc().length() > 0) {
                            txnModel.setAccountTypeDesc(txnResponse.getTransactionProto().getAccountTypeDesc());
                        } else {
                            txnModel.setAccountTypeDesc("");
                        }
                        txnResponseModel.setTxnModel(txnModel);
                    }
                    if (txnResponse.getLoanDetailsProto() != null) {
                        loanHistoryModel = new LoanHistoryModel();
                        if (txnResponse.getLoanDetailsProto().getBranchcode() != null
                                && !"".equals(txnResponse.getLoanDetailsProto().getBranchcode())
                                && txnResponse.getLoanDetailsProto().getBranchcode().length() > 0) {
                            loanHistoryModel.setBranchcode(txnResponse.getLoanDetailsProto().getBranchcode());
                        } else {
                            loanHistoryModel.setBranchcode("");
                        }
                        if (txnResponse.getLoanDetailsProto().getAccountNo() != null
                                && !"".equals(txnResponse.getLoanDetailsProto().getAccountNo())
                                && txnResponse.getLoanDetailsProto().getAccountNo().length() > 0) {
                            loanHistoryModel.setAccountNom(txnResponse.getLoanDetailsProto().getAccountNo());
                        } else {
                            loanHistoryModel.setAccountNom("");
                        }
                        if (txnResponse.getLoanDetailsProto().getCustName() != null
                                && !"".equals(txnResponse.getLoanDetailsProto().getCustName())
                                && txnResponse.getLoanDetailsProto().getCustName().length() > 0) {
                            loanHistoryModel.setCustName(txnResponse.getLoanDetailsProto().getCustName());
                        } else {
                            loanHistoryModel.setCustName("");
                        }
                        if (txnResponse.getLoanDetailsProto().getScanctiondt() != null
                                && !"".equals(txnResponse.getLoanDetailsProto().getScanctiondt())
                                && txnResponse.getLoanDetailsProto().getScanctiondt().length() > 0) {
                            loanHistoryModel.setScanctiondt(txnResponse.getLoanDetailsProto().getScanctiondt());
                        } else {
                            loanHistoryModel.setScanctiondt("");
                        }
                        if (txnResponse.getLoanDetailsProto().getScanctionAmount() != null
                                && !"".equals(txnResponse.getLoanDetailsProto().getScanctionAmount())
                                && txnResponse.getLoanDetailsProto().getScanctionAmount().length() > 0) {
                            loanHistoryModel.setScanctionAmount(txnResponse.getLoanDetailsProto().getScanctionAmount());
                        } else {
                            loanHistoryModel.setScanctionAmount("");
                        }
                        if (txnResponse.getLoanDetailsProto().getInstallmentAmt() != null
                                && !"".equals(txnResponse.getLoanDetailsProto().getInstallmentAmt())
                                && txnResponse.getLoanDetailsProto().getInstallmentAmt().length() > 0) {
                            loanHistoryModel.setInstallmentAmt(txnResponse.getLoanDetailsProto().getInstallmentAmt());
                        } else {
                            loanHistoryModel.setInstallmentAmt("");
                        }
                        if (txnResponse.getLoanDetailsProto().getExpirydate() != null
                                && !"".equals(txnResponse.getLoanDetailsProto().getExpirydate())
                                && txnResponse.getLoanDetailsProto().getExpirydate().length() > 0) {
                            loanHistoryModel.setExpirydate(txnResponse.getLoanDetailsProto().getExpirydate());
                        } else {
                            loanHistoryModel.setExpirydate("");
                        }
                        if (txnResponse.getLoanDetailsProto().getNoofinstallments() > 0) {
                            loanHistoryModel.setNoofinstallments(txnResponse.getLoanDetailsProto().getNoofinstallments());
                        } else {
                            loanHistoryModel.setNoofinstallments(0);
                        }
                        if (txnResponse.getLoanDetailsProto().getOutstandingBal() != null
                                && !"".equals(txnResponse.getLoanDetailsProto().getOutstandingBal())
                                && txnResponse.getLoanDetailsProto().getOutstandingBal().length() > 0) {
                            loanHistoryModel.setOutstandingBal(txnResponse.getLoanDetailsProto().getOutstandingBal());
                        } else {
                            loanHistoryModel.setOutstandingBal("");
                        }
                        if (txnResponse.getLoanDetailsProto().getOverdue() != null
                                && !"".equals(txnResponse.getLoanDetailsProto().getOverdue())
                                && txnResponse.getLoanDetailsProto().getOverdue().length() > 0) {
                            loanHistoryModel.setOverdue(txnResponse.getLoanDetailsProto().getOverdue());
                        } else {
                            loanHistoryModel.setOverdue("");
                        }
                        if (txnResponse.getLoanDetailsProto().getReserve1() != null
                                && !"".equals(txnResponse.getLoanDetailsProto().getReserve1())
                                && txnResponse.getLoanDetailsProto().getReserve1().length() > 0) {
                            loanHistoryModel.setReserve1(txnResponse.getLoanDetailsProto().getReserve1());
                        } else {
                            loanHistoryModel.setReserve1("");
                        }
                        if (txnResponse.getLoanDetailsProto().getReserve2() != null
                                && !"".equals(txnResponse.getLoanDetailsProto().getReserve2())) {
                            loanHistoryModel.setReserve2(txnResponse.getLoanDetailsProto().getReserve2());
                        } else {
                            loanHistoryModel.setReserve2("");
                        }
                        if (txnResponse.getLoanDetailsProto().getReserve3() != null
                                && !"".equals(txnResponse.getLoanDetailsProto().getReserve3())
                                && txnResponse.getLoanDetailsProto().getReserve3().length() > 0) {
                            loanHistoryModel.setReserve3(txnResponse.getLoanDetailsProto().getReserve3());
                        } else {
                            loanHistoryModel.setReserve3("");
                        }
                        if (txnResponse.getLoanDetailsProto().getTxndate() != null
                                && !"".equals(txnResponse.getLoanDetailsProto().getTxndate())
                                && txnResponse.getLoanDetailsProto().getTxndate().length() > 0) {
                            loanHistoryModel.setTxndate(txnResponse.getLoanDetailsProto().getTxndate());
                        } else {
                            loanHistoryModel.setTxndate("");
                        }
                        if (txnResponse.getLoanDetailsProto().getPendingInstlmnts() > 0) {
                            loanHistoryModel.setPendingInstlmnts(txnResponse.getLoanDetailsProto().getPendingInstlmnts());
                        } else {
                            loanHistoryModel.setPendingInstlmnts(0);
                        }
                        if (txnResponse.getLoanDetailsProto().getAccountStatus() > 0) {
                            loanHistoryModel.setAccountStatus(txnResponse.getLoanDetailsProto().getAccountStatus());
                        } else {
                            loanHistoryModel.setAccountStatus(0);
                        }
                        if (txnResponse.getLoanDetailsProto().getAccountType() > 0) {
                            loanHistoryModel.setAccountType(txnResponse.getLoanDetailsProto().getAccountType());
                        } else {
                            loanHistoryModel.setAccountType(0);
                        }
                        if (txnResponse.getLoanDetailsProto().getAccountStatusDesc() != null
                                && !"".equals(txnResponse.getLoanDetailsProto().getAccountStatusDesc())
                                && txnResponse.getLoanDetailsProto().getAccountStatusDesc().length() > 0) {
                            loanHistoryModel.setAccountStatusDesc(txnResponse.getLoanDetailsProto().getAccountStatusDesc());
                        } else {
                            loanHistoryModel.setAccountStatusDesc("");
                        }
                        if (txnResponse.getLoanDetailsProto().getAccountTypeDesc() != null
                                && !"".equals(txnResponse.getLoanDetailsProto().getAccountTypeDesc())
                                && txnResponse.getLoanDetailsProto().getAccountTypeDesc().length() > 0) {
                            loanHistoryModel.setAccountTypeDesc(txnResponse.getLoanDetailsProto().getAccountTypeDesc());
                        } else {
                            loanHistoryModel.setAccountTypeDesc("");
                        }
                        txnResponseModel.setLoanHistoryModel(loanHistoryModel);
                    }
                } else {
                    mUiHelper.errorDialog("" + txnResponse.getStatusDescription());
                }
                if (this.onTaskExecutionFinished != null) {
                    this.onTaskExecutionFinished.onTaskFinished();
                }
            } catch (InvalidProtocolBufferException e) {
                Log.e(TAG, e.toString());
            }
        }
    }
}
