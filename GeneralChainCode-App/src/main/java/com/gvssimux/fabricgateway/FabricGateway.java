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


	private static final String tlsCertPath = "src/main/resources/ca.crt";  //必须修改！！！
	private static final String certificatePath = "src/main/resources/User1@org1.example.com-cert.pem";//必须修改！！！
	private static final String privateKeyPath = "src/main/resources/priv_sk";//必须修改！！！


	private static final String channel = "mychannel"; //可能需要修改
	private static final String chaincode = "hyperledger-fabric-contract-java-demo"; //可能需要修改


	private static final String peerEndpoint = "47.113.151.248:7051";
	private static final String overrideAuth = "peer0.org1.example.com";

	private static final String mspID = "Org1MSP";


	public ManagedChannel newGrpcConnection() throws IOException, CertificateException {

		Reader tlsCertReader = Files.newBufferedReader(Paths.get(tlsCertPath));
		X509Certificate tlsCert = Identities.readX509Certificate(tlsCertReader);

		return NettyChannelBuilder.forTarget(peerEndpoint) //可能需要修改
				.sslContext(GrpcSslContexts.forClient().trustManager(tlsCert).build())
				.overrideAuthority(overrideAuth) //可能需要修改
				.build();
	}

	public Gateway gateway() throws Exception {


		BufferedReader certificateReader = Files.newBufferedReader(Paths.get(certificatePath), StandardCharsets.UTF_8);

		X509Certificate certificate = Identities.readX509Certificate(certificateReader);

		BufferedReader privateKeyReader = Files.newBufferedReader(Paths.get(privateKeyPath), StandardCharsets.UTF_8);

		PrivateKey privateKey = Identities.readPrivateKey(privateKeyReader);


		System.out.println("=========================== connected fabric gateway================");

		return Gateway.newInstance()
				.identity(new X509Identity(mspID, certificate))
				.signer(Signers.newPrivateKeySigner(privateKey))
				.connection(this.newGrpcConnection())
				.evaluateOptions(CallOption.deadlineAfter(5, TimeUnit.SECONDS))
				.endorseOptions(CallOption.deadlineAfter(15, TimeUnit.SECONDS))
				.submitOptions(CallOption.deadlineAfter(5, TimeUnit.SECONDS))
				.commitStatusOptions(CallOption.deadlineAfter(1, TimeUnit.MINUTES))
				.connect();
	}


	public Network network(Gateway gateway) {
		return gateway.getNetwork(channel);
	}


	public Contract getContract() throws Exception {
		Gateway gateway = this.gateway();
		Network network = this.network(gateway);
		return network.getContract(chaincode);
	}

}
