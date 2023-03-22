package pt.tecnico.distledger.server.grpc;

import pt.tecnico.distledger.server.domain.operation.*;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.LedgerState;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.DistLedgerCrossServerServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.CrossServerDistLedger.*;

import java.util.Map;

public class CrossServerService {
	private final NamingServerService namingServerService;
	private StubHandler stubHandler;

	public CrossServerService(NamingServerService namingServerService) {
		this.namingServerService = namingServerService;
		this.stubHandler = new StubHandler(namingServerService);
	}

	public void propagateState(Operation operation) {
		// FIXME: IOException
		DistLedgerCrossServerServiceGrpc.DistLedgerCrossServerServiceBlockingStub stub = stubHandler.getStub("B");

		LedgerState.Builder ledgerState = LedgerState.newBuilder();
		ledgerState.addLedger(operation.toProtobuf());
		PropagateStateRequest request = PropagateStateRequest.newBuilder().setState(ledgerState).build();
		stub.propagateState(request);
	}

	public void shutdown() {
		stubHandler.shutdown();
	}
}
