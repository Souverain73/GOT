package masterServer;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Souverain73 on 14.02.2017.
 */
public class ServerManager {
    PortPool portPool;
    List<Server> servers;

    public ServerManager(PortPool portPool) {
        this.portPool = portPool;
    }

    public ServerInfo createServer(String name, String password) throws NoPortsLeftException {
        int tcpPort = portPool.getFreePort();
        int udpPort = portPool.getFreePort();
        if (udpPort * tcpPort < 0) throw new NoPortsLeftException();
        Process serverProcess = runServerProcess(tcpPort, udpPort);
        Server result = new Server();
        result.process = serverProcess;
        result.publicInfo = new ServerInfo(tcpPort, udpPort, name, password);

        servers.add(result);

        return result.publicInfo;
    }

    public List<ServerInfo> getServerList(){
        return servers.stream().map(s->s.publicInfo).collect(Collectors.toList());
    }


    private Process runServerProcess(int tcpPort, int udpPort){
        ProcessBuilder pb = new ProcessBuilder("java", "-jar", "GOTSERVER.jar", String.valueOf(tcpPort), String.valueOf(udpPort));
        try{
            Process p = pb.start();
            return p;
        }catch (IOException e){

        }
        return null;
    }

    private class Server{
        ServerInfo publicInfo;
        Process process;
    }
}
