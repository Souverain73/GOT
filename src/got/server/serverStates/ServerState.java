package got.server.serverStates;

import got.interfaces.INetworkListener;

public interface ServerState extends INetworkListener{
	String getName();
	int getID();
	void enter(StateMachine stm);
	void exit();
}
