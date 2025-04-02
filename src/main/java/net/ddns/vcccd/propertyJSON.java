package net.ddns.vcccd;

import java.util.ArrayList;
import java.util.UUID;

//Class used to fetch property JSON data from players
	public class propertyJSON{
		String worldName;
		UUID playerUUID;
		ArrayList<Integer> xRange;
		ArrayList<Integer> yRange;
		ArrayList<Integer> zRange;
		
		public propertyJSON(String worldName, UUID playerUUID, ArrayList<Integer> xRange, ArrayList<Integer> yRange, ArrayList<Integer> zRange) {
			this.worldName = worldName;
	        this.playerUUID = playerUUID;
	        this.xRange = xRange;
	        this.yRange = yRange;
	        this.zRange = zRange;
		}
	}
