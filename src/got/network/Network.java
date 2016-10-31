package got.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.Server;

import got.gameObjects.ActionObject;

public class Network {
	public static int portTCP = 54555;
	public static int portUDP = 54777;
	
	public static void register(EndPoint endpoint){
		Kryo kryo = endpoint.getKryo();
		Packages.register(endpoint);
		kryo.register(got.Player.class);
		kryo.register(got.Player[].class);
		kryo.register(got.Fraction.class);
		kryo.register(got.Fraction[].class);
		kryo.register(ActionObject.Action.class);
	}
	
	
}
