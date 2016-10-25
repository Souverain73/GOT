package got.interfaces;

import com.esotericsoftware.kryonet.Connection;

/**
 * @author Souverain73
 *	Network listener must handle network packages
 *	All Game States implements this interface for network communication
 */
public interface INetworkListener {
	/**This method reacts on network package.
	 * @param connection network connection
	 * @param pkg network package
	 */
	void recieve(Connection connection, Object pkg);
}