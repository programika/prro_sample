package com.programika.rro;

import com.google.protobuf.ByteString;
import com.iit.certificateAuthority.endUser.libraries.signJava.EndUser;
import com.iit.certificateAuthority.endUser.libraries.signJava.EndUserSignInfo;
import com.programika.rro.ws.chk.CheckRequest;
import com.programika.rro.ws.chk.CheckResponse;
import com.programika.rro.ws.chk.ChkIncomeServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Sender {

    private byte[] key;
    private String password;
    private final String acsk;
    private final String patchToCa;
    private EndUser ee;
    private ChkIncomeServiceGrpc.ChkIncomeServiceBlockingStub chkIncomeServiceBlockingStub;
    private ManagedChannel managedChannel;

    public Sender(byte[] key, String password, String acsk, String url, String patchToCa, boolean ssl) throws Exception {
        this.key = key;
        this.password = password;
        this.acsk = acsk;
        this.patchToCa = patchToCa;
        String[] urls = url.split(":");
        managedChannel = ManagedChannelBuilder.forAddress(urls[0], Integer.parseInt(urls[1])).usePlaintext().build();
        if (ssl) {
            managedChannel = ManagedChannelBuilder.forAddress(urls[0], Integer.parseInt(urls[1])).build();
        }
        init();

    }

    //    @PostConstruct
    private void init() throws Exception {
        CryptoService cryptoService = new CryptoService(acsk, patchToCa);
        cryptoService.initIt();
        ee = cryptoService.getEe();
        ee.ReadPrivateKeyBinary(key, password);
    }

    void readKey(byte[] key, String password) throws Exception {
        this.key = key;
        this.password = password;
        ee.ReadPrivateKeyBinary(key, password);
    }

    EndUser getEe() {
        return ee;
    }

    byte[] signInternal(byte[] data) throws Exception {
        return DatatypeConverter.parseBase64Binary(ee.SignInternal(true, data));
    }

    public CheckResponse sendChk(CheckInternal check) throws Exception {
        //To do sign data

        byte[] dataSign = DatatypeConverter.parseBase64Binary(ee.SignInternal(true, check.getData()));
        long dateTime = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmss").format(check.getDate()));
        com.programika.rro.ws.chk.Check chk = com.programika.rro.ws.chk.Check.newBuilder()
                .setRroFn(check.getFn())
                .setLocalNumber(check.getNumber())
                .setCheckTypeValue(check.getType())
                .setDateTime(dateTime)
                .setCheckSign(ByteString.copyFrom(dataSign))
                .setIdCancel(check.getIdCancel() == null ? "" : check.getIdCancel())
                .setIdOffline(check.getIdOffline() == null ? "" : check.getIdOffline())
                .build();
        chkIncomeServiceBlockingStub = ChkIncomeServiceGrpc.newBlockingStub(managedChannel);
        return chkIncomeServiceBlockingStub.sendChk(chk);
    }

    public CheckResponse getLastChk(String fn) throws Exception {
        byte[] dataSign = DatatypeConverter.parseBase64Binary(ee.SignInternal(true, fn.getBytes()));
        ChkIncomeServiceGrpc.ChkIncomeServiceBlockingStub chkIncomeServiceBlockingStub;


        chkIncomeServiceBlockingStub = ChkIncomeServiceGrpc.newBlockingStub(managedChannel);
        com.programika.rro.ws.chk.CheckRequest l = CheckRequest.newBuilder()
                .setRroFnSign(ByteString.copyFrom(dataSign))
                .build();
        return chkIncomeServiceBlockingStub.lastChk(l);
    }

    public CheckResponse getLastChk2(String fn, byte[] dataSign) throws Exception {
        chkIncomeServiceBlockingStub = ChkIncomeServiceGrpc.newBlockingStub(managedChannel);
        com.programika.rro.ws.chk.CheckRequest l = CheckRequest.newBuilder()
                .setRroFnSign(ByteString.copyFrom(dataSign))
                .build();
        return chkIncomeServiceBlockingStub.lastChk(l);
    }

    public CheckResponse ping(CheckInternal check) throws Exception {
        byte[] dataSign = DatatypeConverter.parseBase64Binary(ee.SignInternal(true, check.getData()));
        long dateTime = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmss").format(check.getDate()));
        com.programika.rro.ws.chk.Check chk = com.programika.rro.ws.chk.Check.newBuilder()
                .setRroFn(check.getFn())
                .setLocalNumber(check.getNumber())
                .setCheckTypeValue(check.getType())
                .setDateTime(dateTime)
                .setCheckSign(ByteString.copyFrom(dataSign))
                .build();
        chkIncomeServiceBlockingStub = ChkIncomeServiceGrpc.newBlockingStub(managedChannel);
        return chkIncomeServiceBlockingStub.ping(chk);
    }

    public String getHashPrev(String fn) throws Exception {
        CheckResponse lastChk = getLastChk(fn);
        if (lastChk.getStatusValue() == 1) {
            log.debug("lastChk {}", lastChk);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            if (lastChk.getDataSign() == null || lastChk.getDataSign().isEmpty()) {
                return "";
            }
            byte[] signData = lastChk.getDataSign().toByteArray();
            EndUserSignInfo sd = ee.VerifyInternal(DatatypeConverter.printBase64Binary(signData));
//        log.debug("SignInfo {} , {}", sd.GetOwnerInfo().GetSerial(), sd.GetOwnerInfo().GetSubjCN());
            byte[] data = sd.GetData();
            byte[] hash = digest.digest(data);
            return bytesToHex(hash);
        } else {
            throw new RuntimeException(lastChk.getStatus().toString());
        }
    }

    public static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public byte[] extractDataSign(byte[] toByteArray) throws Exception {
        EndUserSignInfo sd = ee.VerifyInternal(DatatypeConverter.printBase64Binary(toByteArray));
//        log.debug("SignInfo {} , {}", sd.GetOwnerInfo().GetSerial(), sd.GetOwnerInfo().GetSubjCN());
        return sd.GetData();
    }

    public OfflineId readOfflineIds(String patch) throws Exception {
        OfflineId offlineId = new OfflineId();
        List<String> ids = new ArrayList<>();
        byte[] xmlByte = Files.readAllBytes(Paths.get(patch));
        byte[] xml = new String(xmlByte, "windows-1251").getBytes();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new ByteArrayInputStream(xml)));
        Node nd = doc.getElementsByTagName("C").item(0);
        NodeList childNodes = nd.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item.getNodeType() == Node.ELEMENT_NODE) {
                ids.add(item.getTextContent());
            }

        }
        offlineId.setId(ids);
        return offlineId;
    }

    public void saveOfflineIds(String patch, OfflineId ids) throws Exception {

        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"windows-1251\"?>\n" +
                "<RS V=\"1\">\n<C T=\"12\">\n");
        int i = 0;
        for (String item : ids.getId()) {
            i++;
            sb.append("<ID>").append(item).append("</ID>");
            if (i % 100 == 0) {
                sb.append("\n");
            }
        }
        sb.append("\n</C></RS>");
        Files.write(Paths.get(patch), sb.toString().getBytes("windows-1251"));
    }
}
