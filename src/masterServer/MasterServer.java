package masterServer;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.NoSuchElementException;

import static com.esotericsoftware.jsonbeans.JsonValue.ValueType.object;

/**
 * Created by Souverain73 on 14.02.2017.
 */
public class MasterServer {
    private static ServerManager sm;
    public static void main(String[] args) {
        sm = new ServerManager(new PortPool(51000, 52000));

        Server netServer = new Server();
        netServer.addListener(new MasterListener());
        //todo: bind server and run

        Runtime.getRuntime().addShutdownHook(new Thread(sm::killAllServers));

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String line = br.readLine().trim();
                String[] command = line.split("\\s");
                if (command.length == 0) {
                    continue;
                }
                if (command[0].equals("stop")) {
                    System.exit(0);
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private static class MasterListener extends Listener{
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
            if (pkg instanceof Packages.RequestServerList){
                connection.sendTCP(new Packages.ServerList(sm.getServerList().toArray(new ServerInfo[0])));
            }

            if (pkg instanceof Packages.UpdateStatus) {
                Packages.UpdateStatus request = (Packages.UpdateStatus) pkg;
                ServerInfo si;
                try{
                    si = sm.getServerInfo(request.serverName);
                }catch (NoSuchElementException e){
                    si = null;
                }
                connection.sendTCP(new Packages.ServerStatus(si, null));
            }

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

