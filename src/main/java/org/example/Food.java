package org.example;

public class Food {
	public final String name;
	public final int cookTimeMS;

	public String getName() {
		return name;
	}

	public Food(String name, int cookTimeMS) {
		this.name = name;
		this.cookTimeMS = cookTimeMS;
	}

	public String toString() {
		return name;
	}
}