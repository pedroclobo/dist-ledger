package pt.tecnico.distledger.server.grpc;

import pt.tecnico.distledger.server.domain.operation.*;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.LedgerState;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.DistLedgerCrossServerServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.CrossServerDistLedger.*;

public class CrossServerService {
	private final NamingServerService namingServerService;

	public CrossServerService(NamingServerService namingServerService) {
		this.namingServerService = namingServerService;
	}

	public void propagateState(Operation operation) {
		// TODO: refactor hardcoded server names
		// Can we assume there is only one secondary and hardcode it to B?
		try (DistLedgerCrossServerServiceStubHandler stubHandler = namingServerService.getHandler("B")) {
			DistLedgerCrossServerServiceGrpc.DistLedgerCrossServerServiceBlockingStub stub = stubHandler.getStub();

			LedgerState.Builder ledgerState = LedgerState.newBuilder();
			ledgerState.addLedger(operation.toProtobuf());
			PropagateStateRequest request = PropagateStateRequest.newBuilder().setState(ledgerState).build();
			stub.propagateState(request);

		} catch (Exception e) {
			throw new RuntimeException("Error while connecting to server", e);
		}
	}
}
