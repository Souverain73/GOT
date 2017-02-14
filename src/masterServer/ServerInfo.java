package masterServer;

/**
 * Created by Souverain73 on 14.02.2017.
 */
public class ServerInfo {
    public int tcpPort;
    public int updPort;
    public String name;
    public String password;
    public int playersCount;

    public ServerInfo(int tcpPort, int updPort, String name, String password) {
        this.tcpPort = tcpPort;
        this.updPort = updPort;
        this.name = name;
    }
}
