package DGE.utils;

import java.util.HashMap;

public class LoaderParams extends HashMap<String, Object> {
	/**
	 * 
	 */
	public LoaderParams(){
		super();
	}
	
	public LoaderParams(String [] keyValues){
		super();
		for (int i=0; i*2+1<keyValues.length; i++){
			put(keyValues[i*2], keyValues[i*2+1]);
		}
	}
	
	private static final long serialVersionUID = 1L;
}
