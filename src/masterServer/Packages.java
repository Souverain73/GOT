package masterServer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

/**
 * Created by Souverain73 on 14.02.2017.
 */
public class Packages {
    public static void register(EndPoint endpoint){
        Kryo kryo = endpoint.getKryo();

        kryo.register(CreateServerResult.class);

        kryo.register(ServerInfo.class);
        kryo.register(ServerInfo[].class);

        kryo.register(PlayerInfo.class);
        kryo.register(PlayerInfo[].class);

        kryo.register(GetServerList.class);
        kryo.register(ServerList.class);
        kryo.register(UpdateStatus.class);
        kryo.register(RequestNewServer.class);
        kryo.register(ServerStatus.class);
        kryo.register(RequestAnswer.class);
    }

    public static class GetServerList {
    }

    public static class ServerList {
        public ServerInfo[] data;

        public ServerList(ServerInfo[] data) {
            this.data = data;
        }

        public ServerList() {
        }
    }

    public static class UpdateStatus {
        public String serverName;

        public UpdateStatus(String serverName) {
            this.serverName = serverName;
        }

        public UpdateStatus() {
        }
    }

    public static class ServerStatus {
        public ServerInfo info;
        public PlayerInfo[] playerData;

        public ServerStatus(ServerInfo info, PlayerInfo[] playerData) {
            this.info = info;
            this.playerData = playerData;
        }

        public ServerStatus() {
        }
    }

    public static class RequestNewServer {
        public String name;

        public String password;

        public RequestNewServer(String name, String password) {
            this.name = name;
            this.password = password;
        }
        public RequestNewServer() {
        }
    }

    public static class RequestAnswer {
        public CreateServerResult result;
        public ServerInfo info;

        public RequestAnswer(CreateServerResult result, ServerInfo info) {
            this.result = result;
            this.info = info;
        }

        public RequestAnswer(CreateServerResult result) {
            this.result = result;
        }

        public RequestAnswer() {
        }
    }
}
