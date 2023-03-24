package pt.tecnico.distledger.sharedutils;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class StubHandler<T> {
	private ManagedChannel channel;
	private T stub;

	public StubHandler(String host, int port, StubBuilder<T> stubBuilder) {
		String target = host + ":" + port;
		this.channel = ManagedChannelBuilder.forTarget(target)
		                                    .usePlaintext()
		                                    .build();
		this.stub = stubBuilder.build(channel);
	}

	public T getStub() {
		return stub;
	}

	public ManagedChannel getChannel() {
		return channel;
	}

	public void shutdown() {
		channel.shutdown();
	}
}
