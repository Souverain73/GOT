package got.gameObjects;

import org.joml.Vector2f;

import static got.Constants.*;
import got.Player;
import got.gameStates.GameState;
import got.graphics.DrawSpace;
import got.graphics.Font;
import got.graphics.GraphicModule;
import got.graphics.Text;
import got.graphics.Texture;
import got.graphics.TextureManager;

public class NetPlayersPanel extends AbstractGameObject {
	private Player[] players;
	private NetPlayerPanel[] panels;
	
	public NetPlayersPanel() {
		this(new Player[0]);
	}
	
	public NetPlayersPanel(Player[] list){
		players = new Player[MAX_PLAYERS];
		panels = new NetPlayerPanel[MAX_PLAYERS];
		
		//create visual pannels for all players;
		if (list!=null)
			addPlayers(list);
		
		addChild(new ImageObject(TextureManager.instance().loadTexture("nrs/panel.png"),
				new Vector2f(0,0), 200, 320));
	}
	
	public void addPlayers(Player[] list){
		for (int i=0; i<list.length; i++){
			addPlayer(list[i]);
		}
	}
	
	public void addPlayer(Player player){
		//skip if player is null and if player already exist
		if (player == null || players[player.id]!=null) return;
		
		//create panel for player
		NetPlayerPanel npp = new NetPlayerPanel(player.getNickname());
		npp.setPos(new Vector2f(0, 10+player.id*50)); 
		panels[player.id] = npp;
		
		addChild(npp);
	}
	
	public void removePlayer(Player player){
		this.removePlayer(player.id);
	}
	
	public void removePlayer(int id){
		if (players[id]!=null){
			players[id] = null;
		}
		
		if (panels[id]!=null){
			removeChild(panels[id]);
			panels[id].finish();
			panels[id] = null;
		}
	}
	
	public void setPlayerReady(int id, boolean ready){
		if (panels[id]!=null){
			panels[id].setReady(ready);
		}
	}
	
	public class NetPlayerPanel extends AbstractGameObject {
		Font font = new Font("test");
		Text nick;
		Texture readyGemTex, notReadyGemTex;
		ImageObject readyImg;
		ImageObject bgImg;
		
		public NetPlayerPanel(String nickname) {
			//init textures
			readyGemTex = TextureManager.instance().loadTexture("nrs/ready_gem.png");
			notReadyGemTex = TextureManager.instance().loadTexture("nrs/notready_gem.png");
			//create bakground image
			bgImg = new ImageObject(TextureManager.instance().loadTexture("nrs/panel.png"), new Vector2f(0,0), 200, 50);
			bgImg.setSpace(DrawSpace.SCREEN);
			addChild(bgImg);
			//create ready gem image
			readyImg = new ImageObject(notReadyGemTex, new Vector2f(150, 0), 50, 50);
			addChild(readyImg);
			
			nick = Text.newInstance(nickname, font);
		}
		
		@Override
		public void draw(GameState state) {
			GraphicModule.instance().setDrawSpace(DrawSpace.SCREEN);
			super.draw(state);
			Vector2f cp = getPos();
			nick.draw(cp.x, cp.y+5, 1.0f, 1.0f);
		}
		
		public void setReady(boolean ready){
			if (ready){
				readyImg.setTexture(readyGemTex);
			}else{
				readyImg.setTexture(notReadyGemTex);
			}
		}
	}
}
