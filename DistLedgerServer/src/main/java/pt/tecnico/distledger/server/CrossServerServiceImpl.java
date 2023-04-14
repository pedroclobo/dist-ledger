package pt.tecnico.distledger.server;

import pt.ulisboa.tecnico.distledger.contract.distledgerserver.CrossServerDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.DistLedgerCrossServerServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;

import pt.tecnico.distledger.server.domain.ServerState;
import pt.tecnico.distledger.server.domain.operation.*;
import pt.tecnico.distledger.server.exceptions.ServerUnavailableException;
import pt.tecnico.distledger.sharedutils.VectorClock;

import io.grpc.stub.StreamObserver;
import static io.grpc.Status.INVALID_ARGUMENT;

public class CrossServerServiceImpl extends DistLedgerCrossServerServiceGrpc.DistLedgerCrossServerServiceImplBase {

	/**
	 * Set flag to true to print debug messages. The flag can be set using the
	 * -Ddebug=true command line option.
	 */
	private static final boolean DEBUG_FLAG = (System.getProperty("debug") != null);

	private ServerState state;
	private ServerMode mode;
	private ServerTimestamp timestamp;

	public CrossServerServiceImpl(ServerState state, ServerMode mode, ServerTimestamp timestamp) {
		this.state = state;
		this.mode = mode;
		this.timestamp = timestamp;
	}

	private void checkIfInactive() {
		if (mode.isInactive()) {
			throw new ServerUnavailableException();
		}
	}

	@Override
	public void propagateState(PropagateStateRequest request, StreamObserver<PropagateStateResponse> responseObserver) {
		try {
			checkIfInactive();
			debug("--- Processing 'propagateState' request");

			VectorClock otherReplicaTS = VectorClock.fromProtobuf(request.getReplicaTS());

			for (DistLedgerCommonDefinitions.Operation op : request.getState()
			                                                       .getLedgerList()) {
				Operation operation = Operation.fromProtobuf(op);

				// Check if op isn't already in ledger
				if (!timestamp.getReplicaTS()
				              .GE(operation.getTS())
				    && !state.OperationInLedger(operation)) {
					state.addOperationToLedger(operation);
				}
			}

			timestamp.mergeReplicaTS(otherReplicaTS);
			debug("\nTimeStamp after merge:\n" + timestamp.toStringPretty() + "\n");

			state.recomputeStability();

			PropagateStateResponse response = PropagateStateResponse.newBuilder()
			                                                        .build();
			responseObserver.onNext(response);
			responseObserver.onCompleted();
			debug("------------------------------------------\n");
		} catch (RuntimeException e) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage())
			                                         .asRuntimeException());
		}
	}

	/** Helper method to print debug messages. */
	private static void debug(String debugMessage) {
		if (DEBUG_FLAG)
			System.err.println(debugMessage);
	}
}
