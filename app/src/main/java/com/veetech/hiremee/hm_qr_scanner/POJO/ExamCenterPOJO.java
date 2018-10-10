package com.veetech.hiremee.hm_qr_scanner.POJO;

import java.io.Serializable;

/**
 * Created by SUDV2E08542 on 11/14/2017.
 */

public class ExamCenterPOJO implements Serializable{

    String strCenterName;
    int centerID;


    public ExamCenterPOJO(String strCenterName, int centerID) {
        this.strCenterName = strCenterName;
        this.centerID = centerID;
    }

    public String getStrCenterName() {
        return strCenterName;
    }

    public void setStrCenterName(String strCenterName) {
        this.strCenterName = strCenterName;
    }

    public int getCenterID() {
        return centerID;
    }

    public void setCenterID(int centerID) {
        this.centerID = centerID;
    }

    public ExamCenterPOJO() {
    }



}
