package got.server.serverStates;

import got.interfaces.INetworkListener;

public interface ServerState extends INetworkListener{
	String getName();
	void enter(StateMachine stm);
	void exit();
}
