package DGE;

public class Constants {
//WINDOW
	public static final int SCREEN_WIDTH = 640;
	public static final int SCREEN_HEIGHT = 480;
	
//CAMERA
	public static final float MAX_SCALE = 1.3f;
	public static final float MIN_SCALE = 0.2f;
	
//ACTION SELECTOR	
	public static final float ACTIONSELECTOR_IMAGE_SCALE = 0.5f;
	public static final float ACTION_SELECTOR_RADIUS = 200;
	
//ACTION
	public static final float ACTION_IMAGE_SIZE = 100;

//UNITS
	public static final float UNIT_SIZE = 50;
	public static final float UNIT_SCALE = 1; 
	public static final float UNIT_STEP = 3;
	
//CLICK PRIORITY (more first)
	public static final float INTERFACE_Z = 1;
	public static final float BACKGROUND_Z = 0;
	public static final float ACTIONS_Z = 0.5f;
	public static final float UNIT_Z = 0.5f;
}
