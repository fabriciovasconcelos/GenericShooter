package com.world;

public class Vector2i {
	public int x,y;
	
	public Vector2i(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public boolean equals(Object object) {
		Vector2i vac = (Vector2i) object;
		if(vac.x == this.x && vac.y == this.y) {
			return true;
		}
		return false;
	}
}
