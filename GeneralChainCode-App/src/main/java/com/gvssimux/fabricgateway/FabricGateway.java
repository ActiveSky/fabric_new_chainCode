package com.gvssimux.fabricgateway;


import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import org.hyperledger.fabric.client.*;
import org.hyperledger.fabric.client.identity.Identities;
import org.hyperledger.fabric.client.identity.Signers;
import org.hyperledger.fabric.client.identity.X509Identity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;


public class FabricGateway {
    String networkConnectionConfigPath = "/home/fabric-samples/test-network/organizations/peerOrganizations/org1.example.com/connection-org1.json";
    String networkConnectionConfigPathRemote = "src/main/resources/connection-org1.json";

    //String tlsCertPath = "/usr/software/fabric-samples/test-network/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt";
    //String privateKeyPath="/usr/software/fabric-samples/test-network/organizations/peerOrganizations/org1.example.com/users/User1@org1.example.com/msp/keystore/priv_sk";
    //String certificatePath="/usr/software/fabric-samples/test-network/organizations/peerOrganizations/org1.example.com/users/User1@org1.example.com/msp/signcerts/User1@org1.example.com-cert.pem";

//    String tlsCertPath = "src/main/resources/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt";
//    String certificatePath="src/main/resources/peerOrganizations/org1.example.com/users/User1@org1.example.com/msp/signcerts/User1@org1.example.com-cert.pem";
//    String privateKeyPath="src/main/resources/peerOrganizations/org1.example.com/users/User1@org1.example.com/msp/keystore/priv_sk";


//    String tlsCertPath = "D:\\JavaProject\\Fabric_TraceabilitySys\\src\\main\\resources\\peerOrganizations\\org1.example.com\\peers\\peer0.org1.example.com\\tls\\ca.crt";
//    String certificatePath="D:\\JavaProject\\Fabric_TraceabilitySys\\src\\main\\resources\\peerOrganizations\\org1.example.com\\users\\User1@org1.example.com\\msp\\signcerts\\User1@org1.example.com-cert.pem";
//    String privateKeyPath="D:\\JavaProject\\Fabric_TraceabilitySys\\src\\main\\resources\\peerOrganizations\\org1.example.com\\users\\User1@org1.example.com\\msp\\keystore\\priv_sk";

    String tlsCertPath = "src/main/resources/ca.crt";  //必须修改！！！
    String certificatePath="src/main/resources/User1@org1.example.com-cert.pem";//必须修改！！！
    String privateKeyPath="src/main/resources/priv_sk";//必须修改！！！





    String mspId="Org1MSP";  //可能需要修改
    String channel="mychannel"; //可能需要修改
    String chaincode="hyperledger-fabric-contract-java-demo"; //可能需要修改


    private static final String peerEndpoint = "http://47.113.151.248:7051";
    private static final String overrideAuth = "peer0.org1.example.com";

    private static final String mspID = "Org1MSP";

    public Gateway gateway() throws Exception {


        BufferedReader certificateReader = Files.newBufferedReader(Paths.get(certificatePath), StandardCharsets.UTF_8);

        X509Certificate certificate = Identities.readX509Certificate(certificateReader);

        BufferedReader privateKeyReader = Files.newBufferedReader(Paths.get(privateKeyPath), StandardCharsets.UTF_8);

        PrivateKey privateKey = Identities.readPrivateKey(privateKeyReader);



//         System.out.println("=========================================== connected fabric gateway {} " + gateway);

        return Gateway.newInstance()
                .identity(new X509Identity(mspId , certificate))
                .signer(Signers.newPrivateKeySigner(privateKey))
                .connection(newGrpcConnection())
                .evaluateOptions(CallOption.deadlineAfter(5, TimeUnit.SECONDS))
                .endorseOptions(CallOption.deadlineAfter(15, TimeUnit.SECONDS))
                .submitOptions(CallOption.deadlineAfter(5, TimeUnit.SECONDS))
                .commitStatusOptions(CallOption.deadlineAfter(1, TimeUnit.MINUTES))
                .connect();
    }

    private ManagedChannel newGrpcConnection() throws IOException, CertificateException {
        Reader tlsCertReader = Files.newBufferedReader(Paths.get(tlsCertPath));
        X509Certificate tlsCert = Identities.readX509Certificate(tlsCertReader);

        /*return NettyChannelBuilder.forTarget("peer0.org1.example.com:7051")
                .sslContext(GrpcSslContexts.forClient().trustManager(tlsCert).build())
                .overrideAuthority("peer0.org1.example.com")
                .build();*/
        return NettyChannelBuilder.forTarget(peerEndpoint) //可能需要修改
                .sslContext(GrpcSslContexts.forClient().trustManager(tlsCert).build())
                .overrideAuthority(overrideAuth) //可能需要修改
                .build();
    }

    public Network network(Gateway gateway) {
        return gateway.getNetwork(channel);
    }

   /* public Contract catContract(Network network) {
        return network.getContract("teaArea-java-demo");
    }*/

/*    public ChaincodeEventListener chaincodeEventListener(Network network) {
        return new ChaincodeEventListener(network);
    }*/


    public Contract getContract() throws Exception {
        Gateway gateway = gateway();
        Network network = network(gateway);
        return network.getContract(chaincode);
    }

}
