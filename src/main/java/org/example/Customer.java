package org.example;

import java.util.List;

public class Customer implements Runnable {
	private final String name;
	public final List<Food> order;
	public final int orderNum;    
	
	private static int runningCounter = 0;
	
	private static int cusCounter = 0;
	public static int maxCus = 40;
	public static Object lock = new Object();

	public Customer(String name, List<Food> order) {
		this.name = name;
		this.order = order;
		this.orderNum = ++runningCounter;
	}

	public String toString() {
		return name;
	}

	public void run() {
		Simulation.logEvent(SimulationEvent.customerStarting(this));

		synchronized (lock){
			while (cusCounter >= maxCus){
				try {
					lock.wait();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}

			cusCounter++;
		}

		Simulation.logEvent(SimulationEvent.customerEnteredCoffeeShop(this));

		synchronized (Simulation.waitOrder){
			Simulation.logEvent(SimulationEvent.customerPlacedOrder(this, this.order, this.orderNum));
			Simulation.waitOrder.add(this);
			Simulation.waitOrder.notifyAll();
		}

		try {
			this.wait();
			Simulation.logEvent(SimulationEvent.customerReceivedOrder(this, this.order, this.orderNum));
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		Simulation.logEvent(SimulationEvent.customerLeavingCoffeeShop(this));

		synchronized (lock){
			cusCounter--;
			lock.notifyAll();
		}

	}
}