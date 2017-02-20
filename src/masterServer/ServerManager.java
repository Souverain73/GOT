package masterServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Souverain73 on 14.02.2017.
 */
public class ServerManager {
    PortPool portPool;
    List<Server> servers;
    private String serverJarPath = "c:\\soft\\eclipse\\workspace_Java\\GOT\\build\\GOTSERVER.jar";

    public ServerManager(PortPool portPool) {
        this.servers = new ArrayList<Server>();
        this.portPool = portPool;
    }

    public ServerInfo createServer(String name, String password) throws NoPortsLeftException {
        int tcpPort = portPool.getFreePort();
        if (tcpPort == -1){
            throw new NoPortsLeftException();
        }else{
            portPool.bindPort(tcpPort);
        }
        int udpPort = portPool.getFreePort();
        if (udpPort == -1){
            portPool.unbindPort(tcpPort);
            throw new NoPortsLeftException();
        }else{
            portPool.bindPort(udpPort);
        }
        Process serverProcess = runServerProcess(tcpPort, udpPort);
        Server result = new Server();
        result.process = serverProcess;
        result.processOutput = serverProcess.getInputStream();
        result.publicInfo = new ServerInfo(tcpPort, udpPort, name, password);

        servers.add(result);

        try {
            while (serverProcess.isAlive()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(result.processOutput));
                String line = null;

                while ((line = reader.readLine()) != null) {
                    System.out.println("line = " + line);
                    if (line.equals("[Control]:ServerReady")){
                        return result.publicInfo;
                    }
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public List<ServerInfo> getServerList(){
        return servers.stream().map(s->s.publicInfo).collect(Collectors.toList());
    }

    public ServerInfo getServerInfo(String serverName){
        return servers.stream().filter(s->s.publicInfo.name.equals(serverName)).findFirst().map(s->s.publicInfo).get();
    }


    private Process runServerProcess(int tcpPort, int udpPort){
        ProcessBuilder pb = new ProcessBuilder("java", "-jar", serverJarPath, String.valueOf(tcpPort), String.valueOf(udpPort));
        try{
            Process p = pb.start();
            return p;
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    private class Server{
        ServerInfo publicInfo;
        Process process;
        InputStream processOutput;
    }

    class LogStreamReader implements Runnable {

        private BufferedReader reader;

        public LogStreamReader(InputStream is) {
            this.reader = new BufferedReader(new InputStreamReader(is));
        }

        public void run() {
            try {
                String line = reader.readLine();
                while (line != null) {
                    System.out.println(line);
                    line = reader.readLine();
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void killAllServers(){
        for (Server server : servers){
            server.process.destroyForcibly();
        }
    }
}
