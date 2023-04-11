package pt.tecnico.distledger.server;

import pt.ulisboa.tecnico.distledger.contract.admin.AdminDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.*;

import pt.tecnico.distledger.server.domain.ServerState;
import pt.tecnico.distledger.server.grpc.CrossServerService;

import io.grpc.stub.StreamObserver;
import static io.grpc.Status.INVALID_ARGUMENT;
import io.grpc.StatusRuntimeException;

public class AdminServiceImpl extends AdminServiceGrpc.AdminServiceImplBase {

	private ServerState state;
	private ServerMode mode;
	private ServerTimestamp timestamp;
	private CrossServerService crossServerService;

	public AdminServiceImpl(ServerState state, ServerMode mode, ServerTimestamp timestamp,
	    CrossServerService crossServerService) {
		this.state = state;
		this.mode = mode;
		this.timestamp = timestamp;
		this.crossServerService = crossServerService;
	}

	@Override
	public void activate(ActivateRequest request, StreamObserver<ActivateResponse> responseObserver) {
		mode.activate();

		ActivateResponse response = ActivateResponse.newBuilder()
		                                            .build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	@Override
	public void deactivate(DeactivateRequest request, StreamObserver<DeactivateResponse> responseObserver) {
		mode.deactivate();

		DeactivateResponse response = DeactivateResponse.newBuilder()
		                                                .build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	@Override
	public void getLedgerState(getLedgerStateRequest request, StreamObserver<getLedgerStateResponse> responseObserver) {
		getLedgerStateResponse.Builder responseBuilder = getLedgerStateResponse.newBuilder();
		LedgerState.Builder ledgerBuilder = LedgerState.newBuilder();

		for (pt.tecnico.distledger.server.domain.operation.Operation op : state.getLedger()) {
			ledgerBuilder.addLedger(op.toProtobuf());
		}

		getLedgerStateResponse response = responseBuilder.setLedgerState(ledgerBuilder)
		                                                 .build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	@Override
	public void gossip(GossipRequest request, StreamObserver<GossipResponse> responseObserver) {
		try {
			String qualifier = request.getQualifier();

			crossServerService.propagateState(timestamp.getReplicaTS(), state.getLedger(), qualifier);

			GossipResponse response = GossipResponse.newBuilder()
			                                        .build();
			responseObserver.onNext(response);
			responseObserver.onCompleted();
		} catch (StatusRuntimeException e) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage())
			                                         .asRuntimeException());
		}
	}
}
