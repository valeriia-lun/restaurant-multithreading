package org.example;

import java.util.ArrayList;
import java.util.List;

public class Validate {
  private static class InvalidSimulationException extends Exception {
    public InvalidSimulationException() {
    }
  }

  private static void check(boolean check, String message) throws InvalidSimulationException {
    if (!check) {
      System.err.println("SIMULATION INVALID : " + message);
      throw new InvalidSimulationException();
    }
  }

  public static boolean validateSimulation(List<SimulationEvent> events) {
    try {
      check(events.get(0).event == SimulationEvent.EventType.SimulationStarting,
          "Simulation didn't start with initiation event");
      check(events.get(events.size() - 1).event ==
              SimulationEvent.EventType.SimulationEnded,
          "Simulation didn't end with termination event");

      int numCustomers = events.get(0).simParams[0];
      int numCooks = events.get(0).simParams[1];
      int numTables = events.get(0).simParams[2];
      int capacity = events.get(0).simParams[3];
      List<Customer> customers = new ArrayList<>();
      List<Cook> cooks = new ArrayList<>();
      boolean notfull = true;
      int sum = 0;
      for (SimulationEvent event : events) {
        if (event.event == SimulationEvent.EventType.CustomerEnteredCoffeeShop) {
          sum++;
        } else if (event.event == SimulationEvent.EventType.CustomerLeavingCoffeeShop) {
          sum--;
        }
        if (event.cook != null && !cooks.contains(event.cook)) {
          cooks.add(event.cook);
        }
        if (event.customer != null && !customers.contains(event.customer)) {
          customers.add(event.customer);
        }
        notfull = notfull && (sum <= numTables);
      }
      check(notfull, "Customer more than tables in shop");
      check(customers.size() == numCustomers, "Customer not specified");
      check(cooks.size() == numCooks, "Cook not specified");
      boolean allInorder = true;
      for (int i = 0; events.get(i).event == SimulationEvent.EventType.CustomerEnteredCoffeeShop && i < events.size(); i++) {
        boolean inorder = false;
        Customer customer = events.get(i).customer;
        int j = i + 1;
        for (j = i + 1; events.get(j).event == SimulationEvent.EventType.CustomerPlacedOrder && j < events.size(); j++) {
          if (events.get(j).customer == customer) {
            // only place one order
            if (!inorder) {
              inorder = true;
            } else {
              inorder = false;
            }
          }
        }
        if (!inorder) {
          allInorder = false;
          break;
        }
        inorder = false;
        for (j = j + 1; events.get(j).event == SimulationEvent.EventType.CookReceivedOrder && j < events.size(); j++) {
          if (events.get(j).orderNumber == customer.orderNum) {
            inorder = true;
            break;
          }
        }
        if (!inorder) {
          allInorder = false;
          break;
        }
        inorder = false;
        for (j = j + 1; events.get(j).event == SimulationEvent.EventType.CookCompletedOrder && j < events.size(); j++) {
          if (events.get(j).orderNumber == customer.orderNum) {
            inorder = true;
            break;
          }
        }
        if (!inorder) {
          allInorder = false;
          break;
        }
        inorder = false;
        for (j = j + 1; events.get(j).event == SimulationEvent.EventType.CookCompletedOrder && j < events.size(); j++) {
          if (events.get(j).orderNumber == customer.orderNum) {
            inorder = true;
            break;
          }
        }
        if (!inorder) {
          allInorder = false;
          break;
        }
        inorder = false;
        for (j = j + 1; events.get(j).event == SimulationEvent.EventType.CustomerReceivedOrder && j < events.size(); j++) {
          if (events.get(j).customer == customer) {
            inorder = true;
            break;
          }
        }
        if (!inorder) {
          allInorder = false;
          break;
        }
        inorder = false;
        for (j = j + 1; events.get(j).event == SimulationEvent.EventType.CustomerLeavingCoffeeShop && j < events.size(); j++) {
          if (events.get(j).customer == customer) {
            inorder = true;
            break;
          }
        }
        if (!inorder) {
          allInorder = false;
          break;
        }
        allInorder = allInorder && inorder;
      }
      check(allInorder, "Action not in order");
      notfull = true;
      for (Machine machine : Simulation.machines) {
        int total = capacity;
        int run = 0;
        for (int i = 0; events.get(i).machine == machine; i++) {
          if (events.get(i).event == SimulationEvent.EventType.MachineStartingFood) {
            run++;
          }
          if (events.get(i).event == SimulationEvent.EventType.MachineDoneFood) {
            run--;
          }
          notfull = notfull && (run <= total);
        }
      }
      check(notfull, "Machine more than capacity");

      return true;
    } catch (InvalidSimulationException e) {
      return false;
    }
  }
}
