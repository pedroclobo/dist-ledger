package pt.tecnico.distledger.namingserver;

import pt.ulisboa.tecnico.distledger.contract.namingserver.NamingServerDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.namingserver.NamingServerServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.Server;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.Map;
import java.util.HashMap;

public class ServerNamingServerService extends NamingServerService {

	public ServerNamingServerService(String host, int port) {
		super(host, port);
	}

	public void register(String serviceName, String qualifier, String host,
	    int port) {
		RegisterRequest request = RegisterRequest.newBuilder()
		                                         .setServiceName(serviceName)
		                                         .setQualifier(qualifier)
		                                         .setHost(host)
		                                         .setPort(port)
		                                         .build();
		RegisterResponse response = stub.register(request);
	}

	public void delete(String serviceName, String host, int port) {
		DeleteRequest request = DeleteRequest.newBuilder()
		                                     .setServiceName(serviceName)
		                                     .setHost(host)
		                                     .setPort(port)
		                                     .build();
		DeleteResponse response = stub.delete(request);
	}
}
