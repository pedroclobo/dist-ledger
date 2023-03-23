package pt.tecnico.distledger.adminclient.grpc;

import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.LedgerState;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminServiceGrpc;

import java.util.Map;

public class StubHandler {
	private NamingServerService namingServerService;
	private Map<String, AdminServiceStubHandler> stubHandlers;

	public StubHandler(NamingServerService namingServerService) {
		this.namingServerService = namingServerService;
		this.stubHandlers = namingServerService.getHandlers();
	}

	public AdminServiceGrpc.AdminServiceBlockingStub getStub(String qualifier) {
		try {
			if (!stubHandlers.containsKey(qualifier)) {
				stubHandlers.put(qualifier, namingServerService.getHandler(qualifier));
			}

			return stubHandlers.get(qualifier).getStub();
		} catch (RuntimeException e) {
			throw e;
		}
	}

	public void shutdown() {
		stubHandlers.values().forEach(AdminServiceStubHandler::shutdown);
	}
}
