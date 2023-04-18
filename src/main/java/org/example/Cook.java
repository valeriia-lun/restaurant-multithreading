package org.example;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Cook implements Runnable {
  private final String name;

  public Cook(String name) {
    this.name = name;
  }

  public String toString() {
    return name;
  }

  public void run() {
    Simulation.logEvent(SimulationEvent.cookStarting(this));
    Customer customer;

    synchronized (Simulation.waitOrder) {
      while (Simulation.waitOrder.isEmpty()) {
        try {
          Simulation.waitOrder.wait();
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
      customer = Simulation.waitOrder.remove(0);
      Simulation.waitOrder.notifyAll();
    }

    Simulation.logEvent(SimulationEvent.cookReceivedOrder(this, customer.order, customer.orderNum));

    synchronized (customer) {
      List<Machine> machines = Simulation.machines;
      Map<String, Long> order = customer.order
          .stream()
          .collect(Collectors.groupingBy(Food::getName, Collectors.counting()));

      Thread[] cookingThreads = new Thread[customer.order.size()];

      int counter = 0;
      while (!order.isEmpty()) {
        for (Machine machine : machines) {
          String foodType = machine.machineFoodType.name;
          if (!machine.full && order.containsKey(foodType)) {
            try {
              Thread[] machineThreads = machine.makeFood(this, customer, order.get(foodType).intValue());

              for (int i = 0; i < machineThreads.length; i++) {
                cookingThreads[counter++] = machineThreads[i];
              }
              order.remove(foodType);

            } catch (InterruptedException e) {
              throw new RuntimeException(e);
            }
          }
        }
      }

      for (int i = 0; i < cookingThreads.length; i++) {
        try {
          cookingThreads[i].join();
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
      customer.notifyAll();
    }
  }
}