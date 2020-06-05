package com.programika.rro;

import com.iit.certificateAuthority.endUser.libraries.signJava.*;

import lombok.extern.slf4j.Slf4j;

import java.io.File;

import static com.iit.certificateAuthority.endUser.libraries.signJava.EndUser.EU_SIGN_TYPE_PARAMETER;

/**
 * Created by Igin7 on 28.10.2016.
 */
@Slf4j
public class CryptoService {
    public static final int EU_SIGN_TYPE_UNKNOWN = 0;
    public static final int EU_SIGN_TYPE_CADES_BES = 1;
    public static final int EU_SIGN_TYPE_CADES_T = 4;
    public static final int EU_SIGN_TYPE_CADES_C = 8;
    public static final int EU_SIGN_TYPE_CADES_X_LONG = 16;


    public static String CONST_SIGN = "UA1_SIGN\0";
    public static String CONST_TR = "TRANSPORTABLE\0";
    public static String CONST_CRYPT = "UA1_CRYPT\0";
    public static String CONST_CERTCRYPT = "CERTCRYPT\0";
    public static String sertS = "CERTCRYPT\0";


   private String paramCrt = "crt";

    private String ca = "acskidd.gov.ua";
    private String crt;
    private EndUser ee = null;

    public CryptoService(String ca , String paramCrt) {
        this.ca = ca;
        this.paramCrt=paramCrt;
    }


    public void initIt() throws Exception {
        log.debug("paramCrt " + paramCrt);
        File pp = new File(paramCrt);
        if (!pp.exists()) {
            pp.mkdirs();
        }

        crt = pp.getAbsolutePath() + File.separator;
        log.debug("crt =>" + crt);
        init();

    }

    public EndUser getEe() {
        return ee;
    }

    public boolean IsInit() {
        return ee.IsInitialized();

    }

    public boolean init() {
        // ee= myApp.getEe();
        if (ee != null && ee.IsInitialized())
            return true;

        // String
//        ca= "acsk.treasury.gov.ua";
        String ca = this.ca;
        ee = new EndUser();

        // NetUtils sm = new NetUtils(ctx);

        try {
            //
            ee.SetUIMode(false);

            ee.SetCharset("UTF-8");
            ee.Initialize();


            EndUserFileStoreSettings fileStoreSettings = ee.CreateFileStoreSettings();
            fileStoreSettings.SetExpireTime(3600);
            fileStoreSettings.SetAutoDownloadCRLs(true);
            fileStoreSettings.SetFullAndDeltaCRLs(true);

            fileStoreSettings.SetCheckCRLs(false); /////

            fileStoreSettings.SetAutoRefresh(true);
            fileStoreSettings.SetPath(crt);
            ee.SetFileStoreSettings(fileStoreSettings);
            EndUserOCSPSettings ocspSettings = ee.CreateOCSPSettings();
            ocspSettings.SetPort("80");
            ocspSettings.SetAddress(ca);
            ocspSettings.SetBeforeStore(true);
            ocspSettings.SetUseOCSP(true);
            ee.SetOCSPSettings(ocspSettings);
            EndUserTSPSettings tspSettings = ee.CreateTSPSettings();
            tspSettings.SetPort("80");
            tspSettings.SetAddress(ca);
            tspSettings.SetGetStamps(true);
            ee.SetTSPSettings(tspSettings);
            EndUserCMPSettings cmpSettings = ee.CreateCMPSettings();
            cmpSettings.SetCommonName(ca);
            cmpSettings.SetPort("80");
            cmpSettings.SetAddress(ca);
            cmpSettings.SetUseCMP(true);
            ee.SetCMPSettings(cmpSettings);
            EndUserLDAPSettings ldapSettings = ee.CreateLDAPSettings();
            ldapSettings.SetPassword("password");
            ldapSettings.SetUser("user");
            ldapSettings.SetPort("389");
            ldapSettings.SetAddress(ca);
            ldapSettings.SetAnonymous(true);
            ldapSettings.SetUseLDAP(false);
            ee.SetLDAPSettings(ldapSettings);
            EndUserProxySettings proxySettings = ee.CreateProxySettings();
            proxySettings.SetUseProxy(false);
            ee.SetProxySettings(proxySettings);
            EndUserModeSettings modeSettings = ee.CreateModeSettings();
            modeSettings.SetOfflineMode(false);
            ee.SetModeSettings(modeSettings);
            //  myApp.setEe(ee);
            ee.SetRuntimeParameter(EU_SIGN_TYPE_PARAMETER, EU_SIGN_TYPE_CADES_T);
            log.debug("init");
            return true;
        } catch (Exception e) {

            e.printStackTrace();
            //  Logger.getLogger(Crypt.class.getName()).error("Exception init", e);
            return false;
        }
    }


}
