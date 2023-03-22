package pt.tecnico.distledger.server;

import pt.tecnico.distledger.server.domain.ServerState;
import pt.tecnico.distledger.server.domain.operation.CreateOp;
import pt.tecnico.distledger.server.domain.exceptions.AccountAlreadyExistsException;
import pt.tecnico.distledger.server.domain.exceptions.CreateBrokerException;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class CreateAccountTest {
	private static ServerState state;

	@BeforeEach
	public void setUp() {
		state = new ServerState();
	}

	@AfterEach
	public void tearDown() {
		state = null;
	}

	// Create new account
	@Test
	public void createAccount() {
		String account = "Alice";
		CreateOp op = new CreateOp(account);
		state.addCreateOperation(op);

		assertEquals(state.getLedger().size(), 1);
		assertEquals(state.getLedger().get(0), op);
		assertEquals(state.getAccountBalance(account), 0);
	}

	// Create the same account twice
	@Test
	public void createAccountTwice() {
		CreateOp op1 = new CreateOp("Alice");
		CreateOp op2 = new CreateOp("Alice");

		state.addCreateOperation(op1);

		// An exception should be thrown when creating a duplicate account
		assertThrows(AccountAlreadyExistsException.class, () -> state.addCreateOperation(op2));
		assertEquals(state.getLedger().size(), 1);
		assertEquals(state.getLedger().get(0), op1);
	}

	// Create an account with the name 'broker'
	@Test
	public void createBrokerAccount() {
		CreateOp op = new CreateOp("broker");

		// An exception should be thrown when creating the 'broker' account
		assertThrows(CreateBrokerException.class, () -> state.addCreateOperation(op));
		assertEquals(state.getLedger().size(), 0);
	}
}
