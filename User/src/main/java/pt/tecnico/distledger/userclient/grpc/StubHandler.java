package pt.tecnico.distledger.userclient.grpc;

import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.LedgerState;
import pt.ulisboa.tecnico.distledger.contract.user.UserDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.user.UserServiceGrpc;

import java.util.Map;

public class StubHandler {
	private NamingServerService namingServerService;
	private Map<String, UserServiceStubHandler> stubHandlers;

	public StubHandler(NamingServerService namingServerService) {
		this.namingServerService = namingServerService;
		this.stubHandlers = namingServerService.getHandlers();
	}

	public UserServiceGrpc.UserServiceBlockingStub getStub(String qualifier) {
		try {
			if (!stubHandlers.containsKey(qualifier)) {
				stubHandlers.put(qualifier,
				    namingServerService.getHandler(qualifier));
			}

			return stubHandlers.get(qualifier)
			                   .getStub();
		} catch (RuntimeException e) {
			throw e;
		}
	}

	public void shutdown() {
		stubHandlers.values()
		            .forEach(UserServiceStubHandler::shutdown);
	}
}
