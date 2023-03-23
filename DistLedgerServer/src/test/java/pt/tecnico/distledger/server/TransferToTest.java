package pt.tecnico.distledger.server;

import pt.tecnico.distledger.server.domain.ServerState;
import pt.tecnico.distledger.server.domain.operation.CreateOp;
import pt.tecnico.distledger.server.domain.operation.TransferOp;
import pt.tecnico.distledger.server.domain.exceptions.AccountNotFoundException;
import pt.tecnico.distledger.server.domain.exceptions.InsufficientBalanceException;
import pt.tecnico.distledger.server.domain.exceptions.InvalidBalanceException;
import pt.tecnico.distledger.server.domain.exceptions.InvalidTransferException;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class TransferToTest {
	private static ServerState state;

	@BeforeEach
	public void setUp() {
		state = new ServerState();
	}

	@AfterEach
	public void tearDown() {
		state = null;
	}

	// Perform a transfer
	@Test
	public void transferTo() {
		state.addCreateOperation(new CreateOp("Alice"));

		TransferOp op = new TransferOp("broker", "Alice", 10);
		state.addTransferOperation(op);

		assertEquals(state.getLedger()
		                  .size(),
		    2);
		assertEquals(state.getLedger()
		                  .get(1),
		    op);

		assertEquals(state.getAccountBalance("Alice"), 10);
		assertEquals(state.getAccountBalance("broker"), 990);
	}

	// Perform a transfer from a non existing account
	@Test
	public void transferFromMissingAccount() {
		CreateOp op1 = new CreateOp("Alice");
		state.addCreateOperation(op1);

		TransferOp op2 = new TransferOp("Bob", "Alice", 10);

		// An exception should be thrown when transferring from a non existing
		// account
		assertThrows(AccountNotFoundException.class,
		    () -> state.addTransferOperation(op2));
		assertEquals(state.getLedger()
		                  .size(),
		    1);
		assertEquals(state.getLedger()
		                  .get(0),
		    op1);

		assertEquals(state.getAccountBalance("Alice"), 0);
	}

	// Perform a transfer to a non existing account
	@Test
	public void transferToMissingAccount() {
		CreateOp op1 = new CreateOp("Alice");
		state.addCreateOperation(op1);

		TransferOp op2 = new TransferOp("Alice", "Bob", 10);

		// An exception should be thrown when transferring to a non existing
		// account
		assertThrows(AccountNotFoundException.class,
		    () -> state.addTransferOperation(op2));
		assertEquals(state.getLedger()
		                  .size(),
		    1);
		assertEquals(state.getLedger()
		                  .get(0),
		    op1);

		assertEquals(state.getAccountBalance("Alice"), 0);
	}

	// Perform a transfer from an account to the same account
	@Test
	public void transferToSameAccount() {
		CreateOp op1 = new CreateOp("Alice");
		state.addCreateOperation(op1);

		TransferOp op2 = new TransferOp("Alice", "Alice", 10);

		// An exception should be thrown when transferring to the same account
		assertThrows(InvalidTransferException.class,
		    () -> state.addTransferOperation(op2));
		assertEquals(state.getLedger()
		                  .size(),
		    1);
		assertEquals(state.getLedger()
		                  .get(0),
		    op1);

		assertEquals(state.getAccountBalance("Alice"), 0);
	}

	// Perform a transfer with negative amount
	@Test
	public void transferNegativeAmount() {
		CreateOp op1 = new CreateOp("Alice");
		state.addCreateOperation(op1);

		TransferOp op2 = new TransferOp("broker", "Alice", -10);

		// An exception should be thrown when transferring a negative amount
		assertThrows(InvalidBalanceException.class,
		    () -> state.addTransferOperation(op2));
		assertEquals(state.getLedger()
		                  .size(),
		    1);
		assertEquals(state.getLedger()
		                  .get(0),
		    op1);

		assertEquals(state.getAccountBalance("Alice"), 0);
		assertEquals(state.getAccountBalance("broker"), 1000);
	}

	// Perform a transfer with amount = 0
	@Test
	public void transferNullAmount() {
		CreateOp op1 = new CreateOp("Alice");
		state.addCreateOperation(op1);

		TransferOp op2 = new TransferOp("broker", "Alice", 0);

		// An exception should be thrown when transferring no coins
		assertThrows(InvalidBalanceException.class,
		    () -> state.addTransferOperation(op2));
		assertEquals(state.getLedger()
		                  .size(),
		    1);
		assertEquals(state.getLedger()
		                  .get(0),
		    op1);

		assertEquals(state.getAccountBalance("Alice"), 0);
		assertEquals(state.getAccountBalance("broker"), 1000);
	}

	// Perform a transfer with balance < amount
	@Test
	public void transferNotEnoughBalance() {
		CreateOp op1 = new CreateOp("Alice");
		state.addCreateOperation(op1);

		TransferOp op2 = new TransferOp("broker", "Alice", 1050);

		// An exception should be thrown when transferring more coins than the
		// account
		// has
		assertThrows(InsufficientBalanceException.class,
		    () -> state.addTransferOperation(op2));
		assertEquals(state.getLedger()
		                  .size(),
		    1);
		assertEquals(state.getLedger()
		                  .get(0),
		    op1);

		assertEquals(state.getAccountBalance("Alice"), 0);
		assertEquals(state.getAccountBalance("broker"), 1000);
	}

	// Perform a transfer with balance = amount
	@Test
	public void transferBalanceEqualsAmount() {
		CreateOp op1 = new CreateOp("Alice");
		state.addCreateOperation(op1);

		TransferOp op2 = new TransferOp("broker", "Alice", 1000);

		state.addTransferOperation(op2);

		assertEquals(state.getLedger()
		                  .size(),
		    2);
		assertEquals(state.getLedger()
		                  .get(1),
		    op2);

		assertEquals(state.getAccountBalance("Alice"), 1000);
		assertEquals(state.getAccountBalance("broker"), 0);
	}
}
