package masterServer;

/**
 * Created by Souverain73 on 14.02.2017.
 */
public class PortPool {
    private static final int DEFAULT_START_PORT = 51000;
    private static final int DEFAULT_END_PORT = 51999;

    private int startPort;
    private int endPort;

    private boolean [] isBinded;

    public PortPool() {
        this(DEFAULT_START_PORT, DEFAULT_END_PORT);
    }

    public PortPool(int startPort, int endPort) {
        this.startPort = startPort;
        this.endPort = endPort;
        this.isBinded = new boolean[this.endPort - this.startPort];
    }

    public int getFreePort(){
        for (int i = 0; i < isBinded.length; i++) {
            if (!isBinded[i]) return startPort + i;
        }
        return -1;
    }

    public void bindPort(int port){
        isBinded[port-startPort] = true;
    }

    public void unbindPort(int port){
        isBinded[port-startPort] = false;
    }
}
