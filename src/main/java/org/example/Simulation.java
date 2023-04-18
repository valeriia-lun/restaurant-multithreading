package org.example;

import java.util.*;

public class Simulation {
  public static List<SimulationEvent> events;

  public static List<Customer> waitOrder;

  public static List<Machine> machines;

  public static void logEvent(SimulationEvent event) {
    events.add(event);
    System.out.println(event);
  }

  public static List<SimulationEvent> runSimulation(
      int numCustomers, int numCooks,
      int numTables,
      int machineCapacity,
      boolean randomOrders
  ) {

    events = Collections.synchronizedList(new ArrayList<SimulationEvent>());

    logEvent(SimulationEvent.startSimulation(numCustomers,
        numCooks,
        numTables,
        machineCapacity));

    waitOrder = new ArrayList<>();
    machines = new ArrayList<>();
    Customer.maxCus = numTables;

    machines.add(new Machine("BurgerMachine", FoodType.burger, machineCapacity));
    machines.add(new Machine("FriesMachine", FoodType.fries, machineCapacity));
    machines.add(new Machine("CoffeeMachine", FoodType.coffee, machineCapacity));

    Thread[] cooks = new Thread[numCooks];
    for (int i = 0; i < cooks.length; i++) {
      cooks[i] = new Thread(new Cook("Cook " + (i + 1)));
      cooks[i].start();
    }

    Thread[] customers = new Thread[numCustomers];
    LinkedList<Food> order;
    if (!randomOrders) {
      order = new LinkedList<Food>();
      order.add(FoodType.burger);
      order.add(FoodType.fries);
      order.add(FoodType.fries);
      order.add(FoodType.coffee);
      for (int i = 0; i < customers.length; i++) {
        customers[i] = new Thread(
            new Customer("Customer " + (i + 1), order)
        );
      }
    } else {
      for (int i = 0; i < customers.length; i++) {
        Random rnd = new Random(27);
        int burgerCount = rnd.nextInt(3);
        int friesCount = rnd.nextInt(3);
        int coffeeCount = rnd.nextInt(3);
        order = new LinkedList<Food>();
        for (int b = 0; b < burgerCount; b++) {
          order.add(FoodType.burger);
        }
        for (int f = 0; f < friesCount; f++) {
          order.add(FoodType.fries);
        }
        for (int c = 0; c < coffeeCount; c++) {
          order.add(FoodType.coffee);
        }
        customers[i] = new Thread(
            new Customer("Customer " + (i + 1), order)
        );
      }
    }

    for (int i = 0; i < customers.length; i++) {
      customers[i].start();
    }


    try {
      for (int i = 0; i < customers.length; i++){
        customers[i].join();
      }

      for (int i = 0; i < cooks.length; i++){
        cooks[i].interrupt();
      }

      for (int i = 0; i < cooks.length; i++){
        cooks[i].join();
      }


    } catch (InterruptedException e) {
      System.out.println("Simulation thread interrupted.");
    }

    logEvent(SimulationEvent.endSimulation());

    return events;
  }


  public static void main(String args[]) throws InterruptedException {
    int numCustomers = 10;
    int numCooks = 3;
    int numTables = 5;
    int machineCapacity = 4;
    boolean randomOrders = true;

    System.out.println("Did it work? " +
        Validate.validateSimulation(
            runSimulation(
                numCustomers, numCooks,
                numTables, machineCapacity,
                randomOrders
            )
        )
    );
  }

}



