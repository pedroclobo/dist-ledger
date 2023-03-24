package pt.tecnico.distledger.server;

import pt.tecnico.distledger.server.domain.ServerState;
import pt.tecnico.distledger.server.domain.operation.CreateOp;
import pt.tecnico.distledger.server.domain.operation.DeleteOp;
import pt.tecnico.distledger.server.domain.operation.TransferOp;
import pt.tecnico.distledger.server.domain.exceptions.AccountHasBalanceException;
import pt.tecnico.distledger.server.domain.exceptions.AccountNotFoundException;
import pt.tecnico.distledger.server.domain.exceptions.DeleteBrokerException;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class DeleteAccountTest {
	private static ServerState state;

	@BeforeEach
	public void setUp() {
		state = new ServerState();
	}

	@AfterEach
	public void tearDown() {
		state = null;
	}

	// Delete an account
	@Test
	public void deleteAccount() {
		CreateOp op1 = new CreateOp("Alice");
		DeleteOp op2 = new DeleteOp("Alice");

		state.addCreateOperation(op1);
		state.addDeleteOperation(op2);

		assertEquals(state.getLedger()
		                  .size(),
		    2);
		assertEquals(state.getLedger()
		                  .get(0),
		    op1);
		assertEquals(state.getLedger()
		                  .get(1),
		    op2);
	}

	// Delete an account that does not exist
	@Test
	public void deleteMissingAccount() {
		DeleteOp op = new DeleteOp("Alice");

		// An exception should be thrown when deleting a non existing account
		assertThrows(AccountNotFoundException.class, () -> state.addDeleteOperation(op));
		assertEquals(state.getLedger()
		                  .size(),
		    0);
	}

	// Delete an account that has balance
	@Test
	public void deleteAccountWithBalance() {
		CreateOp op1 = new CreateOp("Alice");
		TransferOp op2 = new TransferOp("broker", "Alice", 10);
		DeleteOp op3 = new DeleteOp("Alice");

		state.addCreateOperation(op1);
		state.addTransferOperation(op2);

		// An exception should be thrown when deleting a account that has
		// balance
		assertThrows(AccountHasBalanceException.class, () -> state.addDeleteOperation(op3));
		assertEquals(state.getLedger()
		                  .size(),
		    2);
		assertEquals(state.getLedger()
		                  .get(0),
		    op1);
		assertEquals(state.getLedger()
		                  .get(1),
		    op2);
	}

	// Delete the broker account
	@Test
	public void deleteBrokerAccount() {
		DeleteOp op = new DeleteOp("broker");

		// An exception should be thrown when deleting the 'broker' account
		assertThrows(DeleteBrokerException.class, () -> state.addDeleteOperation(op));
		assertEquals(state.getLedger()
		                  .size(),
		    0);
	}
}
