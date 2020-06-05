package com.programika.rro;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SamplesXml {
    public static String getXmlOpenShift(String fn, Date date, String hash, String offlineId) {
        String stringDate = new SimpleDateFormat("yyyyMMddHHmmss").format(date);
        String testData = "<?xml version=\"1.0\" encoding=\"windows-1251\"?>\n" +
                "<RQ V=\"1\">\n" +
                "<DAT DI=\"162292\" DT=\"0\" FN=\"" + fn + "\" TN=\"ПН   2080903659\" V=\"1\" ZN=\"402342434\">\n" +
                "    <C T=\"8\">\n" +
                "    </C>\n" +
                "    <TS>" + stringDate + "</TS>\n" +
                "</DAT>" +
                "<MAC ID=\"" + offlineId + "\">" + hash + "</MAC>\n" +
                "</RQ>\n";
        return testData;
    }

    public static String getXmlChk(String fn, Date date, String hash, String offlineId) {
        String stringDate = new SimpleDateFormat("yyyyMMddHHmmss").format(date);
        String testData = "<?xml version=\"1.0\" encoding=\"windows-1251\"?>\n" +
                "<RQ V=\"1\">\n" +
                "<DAT DI=\"162292\" DT=\"0\" FN=\"" + fn + "\" TN=\"ПН   2080903659\" V=\"1\" ZN=\"402342434\">\n" +
                "    <C T=\"0\">\n" +
                "        <P C=\"1\" N=\"1\" NM=\"ЛМ Лофт блу (12,00)\" SM=\"1050\" TX=\"4\"></P>\n" +
                "        <M N=\"2\" NM=\"ГРОШІ\" RM=\"50\" SM=\"1100\" T=\"0\"></M>\n" +
                "        <E CS=\"1\" DTPR=\"20.00\" DTSM=\"167\" N=\"3\" NO=\"168324\" SM=\"1050\" TX=\"4\" TXAL=\"1\" TXPR=\"5.00\" TXSM=\"50\"  TXTY=\"0\"></E>\n" +
                "    </C>\n" +
                "    <TS>" + stringDate + "</TS>\n" +
                "</DAT>" +
                "<MAC ID=\"" + offlineId + "\">" + hash + "</MAC>\n" +
                "</RQ>\n";
        return testData;
    }

    public static String getXmlZZvit(String fn, Date date, String hash, String offlineId) {
        String stringDate = new SimpleDateFormat("yyyyMMddHHmmss").format(date);
        String testData = "<?xml version=\"1.0\" encoding=\"windows-1251\"?>\n" +
                "<RQ V=\"1\">\n" +
                "<DAT DI=\"162292\" DT=\"0\" FN=\"" + fn + "\" TN=\"ПН   2080903659\" V=\"1\" ZN=\"402342434\">\n" +
                "    <Z T=\"0\">\n" +
                "    </Z>\n" +
                "    <TS>" + stringDate + "</TS>\n" +
                "</DAT>" +
                "<MAC ID=\"" + offlineId + "\">" + hash + "</MAC>\n" +
                "</RQ>\n";
        return testData;
    }

    public static String getXmlOfflineIds(String fn, Date date, String hash, String offlineId) {
        String stringDate = new SimpleDateFormat("yyyyMMddHHmmss").format(date);
        String testData = "<?xml version=\"1.0\" encoding=\"windows-1251\"?>\n" +
                "<RQ V=\"1\">\n" +
                "<DAT DI=\"162292\" DT=\"0\" FN=\"" + fn + "\" TN=\"ПН   2080903659\" V=\"1\" ZN=\"402342434\">\n" +
                "    <C T=\"12\">\n" +
                "<H SIZE=\"200\"></H>" +
                "    </C>\n" +
                "    <TS>" + stringDate + "</TS>\n" +
                "</DAT>" +
                "<MAC>" + hash + "</MAC>\n" +
                "</RQ>\n";
        return testData;
    }

    public static String getXmlToOffline(String fn, Date date, String hash, String offlineId) {
        String stringDate = new SimpleDateFormat("yyyyMMddHHmmss").format(date);
        String testData = "<?xml version=\"1.0\" encoding=\"windows-1251\"?>\n" +
                "<RQ V=\"1\">\n" +
                "<DAT DI=\"162292\" DT=\"0\" FN=\"" + fn + "\" TN=\"ПН   2080903659\" V=\"1\" ZN=\"402342434\">\n" +
                "    <C T=\"9\">\n" +
                "    </C>\n" +
                "    <TS>" + stringDate + "</TS>\n" +
                "</DAT>" +
                "<MAC ID=\"" + offlineId + "\">" + hash + "</MAC>\n" +
                "</RQ>\n";
        return testData;
    }

    public static String getXmlToOnline(String fn, Date date, String hash, String offlineId) {
        String stringDate = new SimpleDateFormat("yyyyMMddHHmmss").format(date);
        String testData = "<?xml version=\"1.0\" encoding=\"windows-1251\"?>\n" +
                "<RQ V=\"1\">\n" +
                "<DAT DI=\"162292\" DT=\"0\" FN=\"" + fn + "\" TN=\"ПН   2080903659\" V=\"1\" ZN=\"402342434\">\n" +
                "    <C T=\"10\">\n" +
                "    </C>\n" +
                "    <TS>" + stringDate + "</TS>\n" +
                "</DAT>" +
                "<MAC>" + hash + "</MAC>\n" +
                "</RQ>\n";
        return testData;
    }

}
