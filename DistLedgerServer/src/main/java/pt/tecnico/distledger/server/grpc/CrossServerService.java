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
		try (DistLedgerCrossServerServiceStubHandler stubHandler = namingServerService.getHandler("B")) {
			DistLedgerCrossServerServiceGrpc.DistLedgerCrossServerServiceBlockingStub stub = stubHandler.getStub();

			LedgerState.Builder ledgerState = LedgerState.newBuilder();
			DistLedgerCommonDefinitions.Operation.Builder op = DistLedgerCommonDefinitions.Operation.newBuilder();

			switch (operation.getType()) {
			case "CreateOp":
				op.setType(DistLedgerCommonDefinitions.OperationType.OP_CREATE_ACCOUNT).setUserId(operation.getAccount());
				break;
			case "DeleteOp":
				op.setType(DistLedgerCommonDefinitions.OperationType.OP_DELETE_ACCOUNT).setUserId(operation.getAccount());
				break;
			case "TransferOp":
				op.setType(DistLedgerCommonDefinitions.OperationType.OP_TRANSFER_TO).setUserId(operation.getAccount()).setDestUserId(((TransferOp) operation).getDestAccount())
				    .setAmount(((TransferOp) operation).getAmount());
				break;
			default:
				break;
			}

			ledgerState.addLedger(op);
			PropagateStateRequest request = PropagateStateRequest.newBuilder().setState(ledgerState).build();
			stub.propagateState(request);

		} catch (Exception e) {
			throw new RuntimeException("Error while connecting to server", e);
		}
	}
}
