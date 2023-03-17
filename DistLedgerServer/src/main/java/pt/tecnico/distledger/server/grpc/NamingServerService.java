package pt.tecnico.distledger.server.grpc;

import pt.ulisboa.tecnico.distledger.contract.namingserver.NamingServerDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.namingserver.NamingServerServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class NamingServerService {

	private final ManagedChannel channel;
	private NamingServerServiceGrpc.NamingServerServiceBlockingStub stub;

	public NamingServerService(String host, int port) {
		String target = host + ":" + port;
		channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
		stub = NamingServerServiceGrpc.newBlockingStub(channel);
	}

	public void register(String serviceName, String qualifier, String host, int port) {
		RegisterRequest request = RegisterRequest.newBuilder().setServiceName(serviceName).setQualifier(qualifier).setHost(host).setPort(port).build();
		RegisterResponse response = stub.register(request);
	}

	public void delete(String serviceName, String host, int port) {
		DeleteRequest request = DeleteRequest.newBuilder().setServiceName(serviceName).setHost(host).setPort(port).build();
		DeleteResponse response = stub.delete(request);
	}

	public void shutdown() {
		channel.shutdown();
	}
}
