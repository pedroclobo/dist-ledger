package pt.tecnico.distledger.namingserver;

import pt.tecnico.distledger.namingserver.domain.NamingServerState;
import pt.tecnico.distledger.namingserver.domain.ServerEntry;

import pt.ulisboa.tecnico.distledger.contract.namingserver.NamingServerDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.namingserver.NamingServerServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.*;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.Server;

import io.grpc.stub.StreamObserver;
import static io.grpc.Status.INVALID_ARGUMENT;

public class NamingServerServiceImpl extends NamingServerServiceGrpc.NamingServerServiceImplBase {

	private NamingServerState state;

	public NamingServerServiceImpl(NamingServerState state) {
		this.state = state;
	}

	@Override
	public void register(RegisterRequest request, StreamObserver<RegisterResponse> responseObserver) {
		String serviceName = request.getServiceName();
		String qualifier = request.getQualifier();
		String host = request.getHost();
		int port = request.getPort();

		try {
			state.register(serviceName, qualifier, host, port);
			RegisterResponse response = RegisterResponse.newBuilder().build();
			responseObserver.onNext(response);
			responseObserver.onCompleted();
		} catch (RuntimeException e) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
		}
	}

	@Override
	public void lookup(LookupRequest request, StreamObserver<LookupResponse> responseObserver) {
		String serviceName = request.getServiceName();
		String qualifier = request.getQualifier();

		LookupResponse.Builder response = LookupResponse.newBuilder();

		try {
			for (ServerEntry serverEntry : state.lookup(serviceName, qualifier)) {
				Server.Builder server = Server.newBuilder();
				server.setQualifier(serverEntry.getQualifier()).setHost(serverEntry.getHost()).setPort(serverEntry.getPort());

				System.out.println("Server: " + serverEntry.getHost() + ":" + serverEntry.getPort() + " (" + serverEntry.getQualifier() + ")");

				response.addServer(server);
			}

			responseObserver.onNext(response.build());
			responseObserver.onCompleted();

		} catch (RuntimeException e) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
		}
	}

	@Override
	public void delete(DeleteRequest request, StreamObserver<DeleteResponse> responseObserver) {
		String serviceName = request.getServiceName();
		String host = request.getHost();
		int port = request.getPort();

		try {
			state.delete(serviceName, host, port);
			DeleteResponse response = DeleteResponse.newBuilder().build();
			responseObserver.onNext(response);
			responseObserver.onCompleted();
		} catch (RuntimeException e) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
		}
	}
}
