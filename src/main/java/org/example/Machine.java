package org.example;

public class Machine {
	public final String machineName;
	public final Food machineFoodType;

	private final int capacity;
	public volatile boolean full;
	private int run;
	public final Object lock = new Object();

	public Machine(String nameIn, Food foodIn, int capacityIn) {
		this.machineName = nameIn;
		this.machineFoodType = foodIn;
		
		this.capacity = capacityIn;
		this.full = capacityIn <= 0;
		this.run = 0;
		Simulation.logEvent(SimulationEvent.machineStarting(this,foodIn,capacityIn));
	}

	public Thread[] makeFood(Cook cook,Customer customer,int num) throws InterruptedException {
		return null;
	}

	private class CookAnItem implements Runnable {
		
		private Cook cook;
		private Customer customer;
		
		public CookAnItem(Cook cook, Customer customer) {
			this.cook = cook;
			this.customer = customer;
		}
		
		public void run() {
		}
	}
 

	public String toString() {
		return machineName;
	}
}