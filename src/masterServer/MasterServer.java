package masterServer;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import static com.esotericsoftware.jsonbeans.JsonValue.ValueType.object;

/**
 * Created by Souverain73 on 14.02.2017.
 */
public class MasterServer {
    private static ServerManager sm;
    public static void main(String[] args) {
        sm = new ServerManager(new PortPool(51000, 51999));
    }

    public MasterServer(){
        Server netServer = new Server();
        netServer.addListener(new MasterListener());
    }

    private class MasterListener extends Listener{
        @Override
        public void connected(Connection connection) {
            super.connected(connection);
        }

        @Override
        public void disconnected(Connection connection) {
            super.disconnected(connection);
        }

        @Override
        public void received(Connection connection, Object pkg) {
            super.received(connection, object);
            if (pkg instanceof Packages.RequestNewServer) {
                Packages.RequestNewServer request = (Packages.RequestNewServer) pkg;
                Packages.RequestAnswer ra = new Packages.RequestAnswer();
                try {
                    ServerInfo serverInfo = sm.createServer(request.name, request.password);
                    ra.result = CreateServerResult.CREATED;
                    ra.info = serverInfo;
                }catch (NoPortsLeftException e){
                    ra.result = CreateServerResult.NO_PORTS_LEFT;
                }
                connection.sendTCP(ra);
            }
        }

        @Override
        public void idle(Connection connection) {
            super.idle(connection);
        }
    }
}

