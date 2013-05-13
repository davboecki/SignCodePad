package de.davboecki.signcodepad.event;

import java.util.HashMap;

import de.davboecki.signcodepad.SignLoc;

public class CalSaver {
	CalSaver(){	};
	public int CalNumber=0;
	public HashMap<Integer,SignLoc> CalPos = new HashMap<Integer,SignLoc>();

}
