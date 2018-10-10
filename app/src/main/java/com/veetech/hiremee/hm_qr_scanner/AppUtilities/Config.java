package com.veetech.hiremee.hm_qr_scanner.AppUtilities;

/**
 * Created by SUDV2E08542 on 01/10/2017.
 */

public class Config {

    public static final String url = "http://52.221.91.52:8090/api/HireMee/";

    public static final String HM_WebToken = "SECTOKEN99B2E5F1B1CF052B2E5F1B1D9A";

    //TODO only at live time

    // Live
    //public static final String image = " https://api.hiremee.co.in/api/v2/getcandidateprofilepicturescanning";
    // https://qrcode.hiremee.co.in/api/hmscanner/getexamcentrelist

    //    public static final String HM_Active_URL_Sakthi_local = "http://172.18.1.2/HMAssessmentAPI/api/";
//    public static final String HM_Active_URL_John_Local = "http://172.18.1.88:81/api/";

//    public static final String HM_Active_URL_Sakthi_UAT = "http://52.221.101.44:8082/api/";
//    public static final String HM_Active_URL_John_UAT = "https://devapi.hiremee.co.in/api/v3/";

    public static final String HM_Active_URL_Sakthi_live = "https://qrcode.hiremee.co.in/api/"; //change
//    public static final String HM_Active_URL_John_live = "https://api.hiremee.co.in/api/v3/";


    //https://54.169.81.240/
    /***
     * Sakthi
     */

//    public static final String examCenters = HM_Active_URL_Sakthi_UAT + "hmscanner/getexamcentrelist";
//    public static final String validateHallTicket = HM_Active_URL_Sakthi_UAT + "hmscanner/validatehiremeeid";
//    public static final String ExamMapping = HM_Active_URL_Sakthi_UAT + "hmscanner/mappingcandidatetoexamcentre";

    /***
     * John
     */

//    public static final String VerifyDeviceId = HM_Active_URL_John_UAT + "verifydeviceid";
//    public static final String image = HM_Active_URL_John_UAT + "getcandidateprofilepicturescanning";
//    public static final String UserAuthUsingID = HM_Active_URL_John_UAT + "checkCandidateIDPooledQRCode";


    //sakthi
//    public static final String HM_Base_URL_Sakthi = "http://172.18.1.2/HMAssessmentAPI/api/";//local
    public static final String HM_Base_URL_Sakthi_UAT = "http://52.221.101.44:8082/api/";//UAT

    public static final String Vijayurl = "http://52.76.103.21:8083/api/";

    public static final String HM_WebToken_vijay = "SECTOKEN99B2E5F1B1CF052B2E5F1B1D9A";

    public static final String SECRETE_KEY = "1f0df7dc-dcb3-4712-a585-c4edfd76a53e";


    //john
//  public static final String HM_Base_URL_Meena = "http://172.18.1.68:56/api/";//local
    /*public static final String HM_Base_URL_JOHN_UAT = "https://devapi.hiremee.co.in/api/";//UAT
    public static final String HM_Base_URL_JOHN_LIVE = "https://api.hiremee.co.in/api/";//Live

    public static final String examCenters = Vijayurl + "hmscanner/getexamcentrelist";
    public static final String validateHallTicket = Vijayurl + "hmscanner/validatehiremeeid";
    public static final String ExamMapping = Vijayurl + "hmscanner/mappingcandidatetoexamcentre";
    public static final String VerifyDeviceId = Vijayurl + "v8/verifydeviceid";
    public static final String image = Vijayurl + "v8/getcandidateprofilepicturescanning";
    public static final String UserAuthUsingID = Vijayurl + "v3/checkCandidateIDPooledQRCode";
    public static final String ScannerCount = Vijayurl + "v8/scancount";
*/

    public static final String DEV_API_New = "http://dev1api.hiremee.co.in/api/";//UAT
    public static final String HM_Base_URL_JOHN_LIVE = "https://api.hiremee.co.in/api/";//Live
    public static final String examCenters = HM_Base_URL_JOHN_LIVE + "v9/GetExamCenterList";
    public static final String validateHallTicket = HM_Base_URL_JOHN_LIVE + "v9/validatehiremeeid";
    public static final String ExamMapping = HM_Base_URL_JOHN_LIVE + "v9/mappingcandidatetoexamcentre";
    public static final String VerifyDeviceId = HM_Base_URL_JOHN_LIVE + "v9/verifydeviceid";
    public static final String image = HM_Base_URL_JOHN_LIVE + "v9/getcandidateprofilepicturescanning";
    public static final String UserAuthUsingID = HM_Base_URL_JOHN_LIVE + "v9/checkCandidateIDPooledQRCode";
    public static final String ScannerCount = HM_Base_URL_JOHN_LIVE + "v9/scancount";

}
