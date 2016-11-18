package got.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

import got.model.Action;

public class Network {
	public static int portTCP = 54555;
	public static int portUDP = 54777;
	
	public static void register(EndPoint endpoint){
		Kryo kryo = endpoint.getKryo();
		Packages.register(endpoint);
		kryo.register(got.model.Player.class);
		kryo.register(got.model.Player[].class);
		
		kryo.register(got.model.Fraction.class);
		kryo.register(got.model.Fraction[].class);
		
		kryo.register(got.model.Unit.class);
		kryo.register(got.model.Unit[].class);
		
		kryo.register(Action.class);
	}
	
	
}
