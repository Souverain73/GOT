package DGE.gameObjects;

import java.util.HashMap;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;

import DGE.gameStates.GameState;
import DGE.graphics.Texture;
import DGE.graphics.TextureManager;
import DGE.utils.LoaderParams;

public class GameMapObject implements GameObject {
	private HashMap<String, MapPartObject> map;
	
	public GameMapObject(){
		map = new HashMap<String, MapPartObject>();
	}
	
	private void addRegion(MapPartObject mapPart){
		if (!map.containsKey(mapPart.getName()))
			map.put(mapPart.getName(), mapPart);
	}
	
	@Override
	public boolean init(LoaderParams params) {
		String fileName = (String)params.get("filename");
		readMapFromFile(fileName);
		return false;
	}

	@Override
	public void draw(GameState st) {
		map.values().forEach(part->part.draw(st));
	}

	@Override
	public void update(GameState st) {
		map.values().forEach(part->part.update(st));
	}
	
	private void readMapFromFile(String fileName){
		try{
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(fileName);
			Node root = doc.getDocumentElement();
			NodeList nodes = root.getChildNodes();
			for (int i=0; i<nodes.getLength(); i++){
				Node node = nodes.item(i);
				if (node.getNodeName().equals("regions")){
					parseRegions(node);
				}else if (node.getNodeName().equals("neighbors")){
					parseNeighbors(node);
				}
			}
		}catch(Exception e){
			System.out.println("Can't read map file: "+fileName);
		}
	}

	private void parseRegions(Node root){
		try{
			NodeList regions = root.getChildNodes();
			for(int i=0; i<regions.getLength(); i++){
				if (regions.item(i).getNodeType() == Node.TEXT_NODE) continue;
				Node region = regions.item(i);
				LoaderParams params = new LoaderParams();
				//��������
				params.put("name", attribValue(region, "name"));
				params.put("influence", Integer.valueOf(valueOrDefault(attribValue(region, "influence"),"0")));
				params.put("resources", Integer.valueOf(valueOrDefault(attribValue(region, "resources"),"0")));
				params.put("building", Integer.valueOf(valueOrDefault(attribValue(region, "building"),"0")));
						
				params.put("x", Integer.valueOf(attribValue(region, "x")));
				params.put("y", Integer.valueOf(attribValue(region, "y")));
				params.put("w", Integer.valueOf(attribValue(region, "w")));
				params.put("h", Integer.valueOf(attribValue(region, "h")));				
				//inner nodes
				NodeList regionParams = region.getChildNodes();
				for(int j=0; j<regionParams.getLength(); j++){
					if (regionParams.item(j).getNodeType() == Node.TEXT_NODE) continue;
					Node paramNode = regionParams.item(j);
					if (paramNode.getNodeName().equals("texture")){
						String texName = attribValue(paramNode, "filename");
						Texture tex = TextureManager.instance().loadTexture(texName);
						params.put("texture", tex);
					}
				}
				MapPartObject mapPart = new MapPartObject();
				mapPart.init(params);
				addRegion(mapPart);
			}
		}catch(Exception e){
			System.out.println("Error while reading regions\n Exception");
			e.printStackTrace();
		}
	}
	
	private void parseNeighbors(Node root){
		try{
		NodeList regions = root.getChildNodes();
		for(int i=0; i<regions.getLength(); i++){
			if (regions.item(i).getNodeType() == Node.TEXT_NODE) continue;
			MapPartObject region = map.get(attribValue(regions.item(i), "name"));
			NodeList neighbors = regions.item(i).getChildNodes();
			for (int j=0; j<neighbors.getLength(); j++){
				if (neighbors.item(j).getNodeType() == Node.TEXT_NODE) continue;
				region.addNeighbor(map.get(attribValue(neighbors.item(j), "name")));
			}
		}}catch(Exception e){
			System.out.println("Error while reading neighbors");
		}
	}
	
	private String attribValue(Node node, String attributeName){
		Node attrib = node.getAttributes().getNamedItem(attributeName);
		if (attrib == null) return null; 
		else return attrib.getNodeValue();
	}
	
	private String valueOrDefault(String value, String def){
		if (value == null) return def;
		else return value;
	}
}
