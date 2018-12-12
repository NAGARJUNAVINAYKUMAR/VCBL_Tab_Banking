package com.vcbl.tabbanking.interactors;

import android.content.Context;
import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;
import com.vcbl.tabbanking.models.EnrollmentData;
import com.vcbl.tabbanking.models.GlobalModel;
import com.vcbl.tabbanking.protobuff.EnrollmentUploadRequest;
import com.vcbl.tabbanking.protobuff.Enrollmentuploadresponse;
import com.vcbl.tabbanking.storage.Constants;
import com.vcbl.tabbanking.storage.Storage;
import com.vcbl.tabbanking.tools.UiHelper;
import com.vcbl.tabbanking.tools.Utility;

public class CustomerBO extends Protobuffbase implements Protobuffresponse {

    private static final String TAG = "CustomerBO-->";

    private UiHelper mUiHelper;

    private OnTaskExecutionFinished onTaskExecutionFinished;

    public interface OnTaskExecutionFinished {
        void onTaskFinished();
    }

    public void setOnTaskFinishedEvent(OnTaskExecutionFinished onTaskFinishedEvent) {
        if (onTaskFinishedEvent != null) {
            this.onTaskExecutionFinished = onTaskFinishedEvent;
        }
    }

    public CustomerBO(Context context) {
        this.mContext = context;
        mUiHelper = new UiHelper(context);
    }

    public void customerEnrollmentRequest() {
        EnrollmentUploadRequest.Request.Builder builder = EnrollmentUploadRequest.Request.newBuilder();
        builder.setAppsVersion(Constants.APP_VERSION);
        builder.setTimeStamp(Utility.getCurrentTimeStamp());
        builder.setMicroatmId(String.valueOf(GlobalModel.microatmid));
        builder.setBcId(String.valueOf(GlobalModel.bcid));
        builder.setEntity(EnrollmentUploadRequest.Entity.CUSTOMER);
        builder.setEnrollmentNo("");

        // enrollment builder
        EnrollmentUploadRequest.EnrolmentData.Builder dataBuilder = EnrollmentUploadRequest.EnrolmentData.newBuilder();

        // identity builder
        EnrollmentUploadRequest.Identity.Builder identityBuilder = EnrollmentUploadRequest.Identity.newBuilder();
        identityBuilder.setUid(EnrollmentData.getIdentityDetails().getUid());
        identityBuilder.setPan(EnrollmentData.getIdentityDetails().getPan());
        identityBuilder.setNregaNo(EnrollmentData.getIdentityDetails().getNregaNo());
        identityBuilder.setSspNo(EnrollmentData.getIdentityDetails().getSspNo());

        // personal builder
        EnrollmentUploadRequest.PersonalInfo.Builder persnlInfoBuilder = EnrollmentUploadRequest.PersonalInfo.newBuilder();

        // customer name builder
        EnrollmentUploadRequest.Name.Builder nameBuilder = EnrollmentUploadRequest.Name.newBuilder();
        nameBuilder.setTitle(EnrollmentData.getPersonalInfo().getCustomerName().getTitle());
        nameBuilder.setFirstName(EnrollmentData.getPersonalInfo().getCustomerName().getFirstName());
        nameBuilder.setLastName(EnrollmentData.getPersonalInfo().getCustomerName().getLastName());
        persnlInfoBuilder.setCustomerName(nameBuilder);

        // father name builder
        EnrollmentUploadRequest.Name.Builder fNameBuilder = EnrollmentUploadRequest.Name.newBuilder();
        fNameBuilder.setFirstName(EnrollmentData.getPersonalInfo().getFatherName());
        persnlInfoBuilder.setFatherName(fNameBuilder);

        // mother name builder
        EnrollmentUploadRequest.Name.Builder mNameBuilder = EnrollmentUploadRequest.Name.newBuilder();
        mNameBuilder.setFirstName(EnrollmentData.getPersonalInfo().getMotherName());
        persnlInfoBuilder.setMotherName(mNameBuilder);

        persnlInfoBuilder.setGender(EnrollmentData.getPersonalInfo().getGender());
        persnlInfoBuilder.setMaritialStatus(EnrollmentData.getPersonalInfo().getMaritalStatus());
        persnlInfoBuilder.setLanguage(EnrollmentData.getPersonalInfo().getLanguage());
        persnlInfoBuilder.setHeadOfFamily(EnrollmentData.getPersonalInfo().getHeadOfFamily());

        // age builder
        EnrollmentUploadRequest.Age.Builder ageBuilder = EnrollmentUploadRequest.Age.newBuilder();
        ageBuilder.setAge(EnrollmentData.getPersonalInfo().getAge().getAge());
        ageBuilder.setDob(EnrollmentData.getPersonalInfo().getAge().getDob());
        persnlInfoBuilder.setAge(ageBuilder);

        dataBuilder.setPersonalInfo(persnlInfoBuilder);
        dataBuilder.setFpsData(EnrollmentData.getPersonalInfo().getBiometricData());
        // address info builder
        EnrollmentUploadRequest.Address.Builder addressBuilder = EnrollmentUploadRequest.Address.newBuilder();
        addressBuilder.setAddress1(EnrollmentData.getAddressInfo().getAddress1());
        addressBuilder.setAddress2(EnrollmentData.getAddressInfo().getAddress2());
        addressBuilder.setState(EnrollmentData.getAddressInfo().getState());
        addressBuilder.setCity(EnrollmentData.getAddressInfo().getCity());
        //addressBuilder.setCountry
        addressBuilder.setPincode(EnrollmentData.getAddressInfo().getPinCode());

        dataBuilder.setAddress(addressBuilder);

        // contact info
        EnrollmentUploadRequest.ContactInfo.Builder contactInfoBuilder = EnrollmentUploadRequest.ContactInfo.newBuilder();
        contactInfoBuilder.setMobile(EnrollmentData.getContactInfo().getMobile());

        dataBuilder.setContactInfo(contactInfoBuilder);

        // id details builder
        EnrollmentUploadRequest.IdDetails.Builder idDetailsBuilder = EnrollmentUploadRequest.IdDetails.newBuilder();
        idDetailsBuilder.setIdType(EnrollmentData.getIdDetails().getIdType());
        idDetailsBuilder.setIdNo(EnrollmentData.getIdDetails().getIdNo());
        idDetailsBuilder.setIssueAuthority(EnrollmentData.getIdDetails().getIssueAuthority());
        idDetailsBuilder.setIssuePlace(EnrollmentData.getIdDetails().getIssuePlace());
        idDetailsBuilder.setIssueDate(EnrollmentData.getIdDetails().getIssueDate());

        identityBuilder.addIdDetails(idDetailsBuilder);

        dataBuilder.setIdentity(identityBuilder);

        // financial info builder
        EnrollmentUploadRequest.FinacialInfo.Builder financialInfoBuilder = EnrollmentUploadRequest.FinacialInfo.newBuilder();
        financialInfoBuilder.setHouseType(EnrollmentData.getFinancialInfo().getHouseType());
        //financialInfoBuilder.setBpl
        financialInfoBuilder.setLandHolding(EnrollmentData.getFinancialInfo().getLandHolding());
        financialInfoBuilder.setNoOfDependents(EnrollmentData.getFinancialInfo().getNoOfDependents());

        dataBuilder.setFinacialInfo(financialInfoBuilder);

        // other info builder
        EnrollmentUploadRequest.OtherInfo.Builder otherInfoBuilder = EnrollmentUploadRequest.OtherInfo.newBuilder();
        otherInfoBuilder.setEducation(EnrollmentData.getOtherInfo().getEducation());
        otherInfoBuilder.setOccupation(EnrollmentData.getOtherInfo().getOccupation());
        otherInfoBuilder.setCommunity(EnrollmentData.getOtherInfo().getCommunity());
        otherInfoBuilder.setSpecialCategory(EnrollmentData.getOtherInfo().getSpecialCategory());
        otherInfoBuilder.setReligion(EnrollmentData.getOtherInfo().getReligion());
        otherInfoBuilder.setMinority(EnrollmentData.getOtherInfo().getMinority());
        otherInfoBuilder.setAuthorisedPerson(EnrollmentData.getOtherInfo().getAuthorisedPerson());

        dataBuilder.setOtherInfo(otherInfoBuilder);

        // guardian builder data
        EnrollmentUploadRequest.Guardian.Builder guardBuilder = EnrollmentUploadRequest.Guardian.newBuilder();

        // guardian name builder data
        EnrollmentUploadRequest.Name.Builder guardNameBuilder = EnrollmentUploadRequest.Name.newBuilder();
        storage = new Storage(mContext);
        if ((Integer.parseInt(storage.getValue(Constants.CUSTOMER_AGE))) < 18) {
            guardNameBuilder.setFirstName(EnrollmentData.getGuardianDetails().getName());
            guardBuilder.setName(guardNameBuilder);
            guardBuilder.setRelation(EnrollmentData.getGuardianDetails().getRelation());
        } else {
            guardNameBuilder.setFirstName("");
            guardBuilder.setName(guardNameBuilder);
            guardBuilder.setRelation("");
        }

        // guardian age builder data
        EnrollmentUploadRequest.Age.Builder guardAgeBuilder = EnrollmentUploadRequest.Age.newBuilder();
        if ((Integer.parseInt(storage.getValue(Constants.CUSTOMER_AGE))) < 18) {
            guardAgeBuilder.setAge(EnrollmentData.getGuardianDetails().getAge().getAge());
            guardAgeBuilder.setDob(EnrollmentData.getGuardianDetails().getAge().getDob());
        } else {
            guardAgeBuilder.setAge(0);
            guardAgeBuilder.setDob("");
        }
        guardBuilder.setAge(guardAgeBuilder);

        // guardian address builder data
        EnrollmentUploadRequest.Address.Builder guardAddressBuilder = EnrollmentUploadRequest.Address.newBuilder();
        if ((Integer.parseInt(storage.getValue(Constants.CUSTOMER_AGE))) < 18) {
            guardAddressBuilder.setAddress1(EnrollmentData.getNomineeDetails().getAddress().getAddress1());
            guardAddressBuilder.setAddress2(EnrollmentData.getNomineeDetails().getAddress().getAddress2());
            guardAddressBuilder.setState(EnrollmentData.getNomineeDetails().getAddress().getState());
            guardAddressBuilder.setCity(EnrollmentData.getNomineeDetails().getAddress().getCity());
            guardAddressBuilder.setPincode(EnrollmentData.getNomineeDetails().getAddress().getPinCode());
            //guardAddressBuilder.setMobile();
            //guardAddressBuilder.setAadhaar()'
        } else {
            guardAddressBuilder.setAddress1("");
            guardAddressBuilder.setAddress2("");
            guardAddressBuilder.setState("");
            guardAddressBuilder.setCity("");
            guardAddressBuilder.setPincode("");
        }
        guardBuilder.setAddress(guardAddressBuilder);

        dataBuilder.setGuardian(guardBuilder);

        // nominee builder data
        EnrollmentUploadRequest.Nominee.Builder nomBuilder = EnrollmentUploadRequest.Nominee.newBuilder();

        // nominee name builder data
        EnrollmentUploadRequest.Name.Builder nomNameBuilder = EnrollmentUploadRequest.Name.newBuilder();
        nomNameBuilder.setFirstName(EnrollmentData.getNomineeDetails().getName());
        nomBuilder.setName(nomNameBuilder);
        nomBuilder.setRelation(EnrollmentData.getNomineeDetails().getRelation());

        // nominee age builder data
        EnrollmentUploadRequest.Age.Builder nomAgeBuilder = EnrollmentUploadRequest.Age.newBuilder();
        nomAgeBuilder.setAge(EnrollmentData.getNomineeDetails().getAge().getAge());
        nomAgeBuilder.setDob(EnrollmentData.getNomineeDetails().getAge().getDob());
        nomBuilder.setAge(nomAgeBuilder);

        // nominee address builder data
        EnrollmentUploadRequest.Address.Builder nomAddressBuilder = EnrollmentUploadRequest.Address.newBuilder();
        nomAddressBuilder.setAddress1(EnrollmentData.getNomineeDetails().getAddress().getAddress1());
        nomAddressBuilder.setAddress2(EnrollmentData.getNomineeDetails().getAddress().getAddress2());
        nomAddressBuilder.setState(EnrollmentData.getNomineeDetails().getAddress().getState());
        nomAddressBuilder.setCity(EnrollmentData.getNomineeDetails().getAddress().getCity());
        nomAddressBuilder.setPincode(EnrollmentData.getNomineeDetails().getAddress().getPinCode());
        //nomAddressBuilder.setMobile();
        //nomAddressBuilder.setAadhaar()'
        nomBuilder.setAddress(nomAddressBuilder);

        dataBuilder.setNominee(nomBuilder);

        EnrollmentUploadRequest.Picture.Builder pictureBuilder = EnrollmentUploadRequest.Picture.newBuilder();
        pictureBuilder.setPicture(EnrollmentData.getPersonalInfo().getImageData());
        dataBuilder.setPicture(pictureBuilder);

        EnrollmentUploadRequest.Signature.Builder signBuilder = EnrollmentUploadRequest.Signature.newBuilder();
        signBuilder.setSignature(EnrollmentData.getPersonalInfo().getSignData());
        dataBuilder.setSignature(signBuilder);

        // introducer builder data
        /*EnrollmentUploadRequest.Introducer.Builder introBuilder = EnrollmentUploadRequest.Introducer.newBuilder();

        EnrollmentUploadRequest.Name.Builder introNameBuilder = EnrollmentUploadRequest.Name.newBuilder();
        introNameBuilder.setFirstName(GlobalModel.getIntroducerDetails().getName().getFirstName());
        introBuilder.setName(introNameBuilder);

        // introducer account details builder
        EnrollmentUploadRequest.AccountDetails.Builder introAccBuilder = EnrollmentUploadRequest.AccountDetails.newBuilder();
        introAccBuilder.setAccountNo(GlobalModel.getIntroducerDetails().getAccountDetails().getAccountNo());
        introBuilder.setAccountDetails(introAccBuilder);

        // contact no
        EnrollmentUploadRequest.ContactInfo.Builder introContactBuilder = EnrollmentUploadRequest.ContactInfo.newBuilder();
        introContactBuilder.setPhone(GlobalModel.getIntroducerDetails().getAddress().getMobileNo());*/
        //introBuilder.setAddress(introContactBuilder);

        builder.setEnrolmentData(dataBuilder);

        if (storage.getValue(Constants.SYNC_URL).length() > 0) {
            url = storage.getValue(Constants.SYNC_URL);
            Log.i(TAG, "SYNC_URL--> " + url);
        }

        EnrollmentUploadRequest.Request buildMessage = builder.build();
        protobuffresponseDelegate = this;
        generateProtoRequest(buildMessage.toByteArray(), Constants.PROTO_IDENTIFIER_NEW_CUSTOMER, Constants.PROTO_TYPE_REQUEST);
        mUiHelper.showProgress("Enrolling, Please wait....");
    }

    @Override
    public void onTxnProtobufferFinished(boolean bResult, String strMessage, byte[] byProtoOutput) {
        mUiHelper.dismissProgress();
        if (!bResult) {
            mUiHelper.errorDialog("" + strMessage);
        } else {
            try {
                Enrollmentuploadresponse.EnrolmentUploadResponse uploadResponse = Enrollmentuploadresponse.EnrolmentUploadResponse.parseFrom(byProtoOutput);
                if (uploadResponse.getStatus().equals(Enrollmentuploadresponse.Status.SUCCESS)) {
                    EnrollmentData.getStatusResp().setStatus(uploadResponse.getStatus());
                    if (this.onTaskExecutionFinished != null) {
                        this.onTaskExecutionFinished.onTaskFinished();
                    }
                } else {
                    mUiHelper.errorDialog("" + uploadResponse.getStatusDescription());
                }
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    }
}
