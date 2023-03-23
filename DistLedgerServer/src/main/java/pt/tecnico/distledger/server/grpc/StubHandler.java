package pt.tecnico.distledger.server.grpc;

import pt.tecnico.distledger.server.domain.operation.*;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.LedgerState;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.DistLedgerCrossServerServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.CrossServerDistLedger.*;

import java.util.Map;

public class StubHandler {
	private NamingServerService namingServerService;
	private Map<String, DistLedgerCrossServerServiceStubHandler> stubHandlers;

	public StubHandler(NamingServerService namingServerService) {
		this.namingServerService = namingServerService;
		this.stubHandlers = namingServerService.getHandlers();
	}

	public DistLedgerCrossServerServiceGrpc.DistLedgerCrossServerServiceBlockingStub getStub(
	    String qualifier) {
		if (!stubHandlers.containsKey(qualifier)) {
			stubHandlers.put(qualifier,
			    namingServerService.getHandler(qualifier));
		}

		return stubHandlers.get(qualifier)
		                   .getStub();
	}

	public void shutdown() {
		stubHandlers.values()
		            .forEach(DistLedgerCrossServerServiceStubHandler::shutdown);
	}
}
