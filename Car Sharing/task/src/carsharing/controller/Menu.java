package carsharing.controller;

import carsharing.dao.Car;
import carsharing.dao.Company;
import carsharing.dao.Customer;
import carsharing.data.CarEntry;

import javax.sql.DataSource;
import java.util.List;
import java.util.Scanner;

public class Menu {

    private final InputController controller;
    private final Company company;
    private final Car car;
    private final Customer customer;

    public Menu(DataSource dataSource) {
        controller = new InputController(new Scanner(System.in));

        company = new Company(dataSource);
        car = new Car(dataSource);
        customer = new Customer(dataSource);
    }

    public void start() {
        boolean inProgress = true;
        int currentMenu = 0;

        while (inProgress) {
            int value;
            switch (currentMenu) {
                case 1:
                    println("""
                            1. Company list
                            2. Create a company
                            0. Back""");
                    value = controller.waitForInt();

                    currentMenu = switch (value) {
                        case 0 -> 0;
                        case 1 -> 2;
                        case 2 -> 3;
                        default -> 1;
                    };
                    break;
                case 2:
                    println("""
                            Company list:%s""".formatted(company.getAll()));
                    value = company.getSize() > 0 ? controller.waitForInt() : 0;

                    if (value == 0) {
                        currentMenu = 1;
                    } else if (company.isIndexOnRange(value - 1)) {
                        company.setSelectedIndex(value - 1);
                        currentMenu = 4;
                    }
                    break;
                case 3:
                    println("""
                            Enter the company name:""");

                    String companyName = controller.waitForString();
                    company.add(companyName);

                    println("The company was created!");

                    currentMenu = 1;
                    break;
                case 4:
                    println("""
                            %s company
                            1. Car list
                            2. Create a car
                            0. Back""".formatted(company.getSelectedName()));
                    value = controller.waitForInt();

                    currentMenu = switch (value) {
                        case 0 -> 1;
                        case 1 -> 5;
                        case 2 -> 6;
                        default -> 4;
                    };
                    break;
                case 5:
                    println("""
                            Car list:%s""".formatted(car.getAll(company.getSelectedId())));

                    currentMenu = 4;
                    break;
                case 6:
                    println("""
                            Enter the car name:""");

                    String carName = controller.waitForString();
                    car.add(carName, company.getSelectedId());

                    println("The car was added!");

                    currentMenu = 4;
                    break;
                case 7:
                    println("""
                            Enter the customer name:""");

                    String customerName = controller.waitForString();
                    customer.add(customerName);

                    println("The customer was created!");

                    currentMenu = 0;
                    break;
                case 8:
                    println("""
                            Choose a customer:%s""".formatted(customer.getAll()));
                    value = customer.getSize() > 0 ? controller.waitForInt() : 0;

                    if (value == 0) {
                        currentMenu = 0;
                    } else if (customer.isIndexOnRange(value - 1)) {
                        customer.setSelectedIndex(value - 1);
                        currentMenu = 9;
                    }
                    break;
                case 9:
                    println("""
                            1. Rent a car
                            2. Return a rented car
                            3. My rented car
                            0. Back""");
                    value = controller.waitForInt();

                    currentMenu = switch (value) {
                        case 0 -> 0;
                        case 1 -> 10;
                        case 2 -> 12;
                        case 3 -> 13;
                        default -> 9;
                    };
                    break;
                case  10:
                    if (customer.getSelectedCustomerRentedCar() >= 0) {
                        println("You've already rented a car!");

                        currentMenu = 9;
                        break;
                    }

                    println("""
                            Choose a company:%s""".formatted(company.getAll()));

                    value = company.getSize() > 0 ? controller.waitForInt() : 0;

                    if (value == 0) {
                        currentMenu = 9;
                    } else if (company.isIndexOnRange(value - 1)) {
                        company.setSelectedIndex(value - 1);
                        currentMenu = 11;
                    }
                    break;
                case  11:
                    List<Integer> blacklist = customer.getCustomersRentedCars();
                    List<CarEntry> carEntries = car.getCarByCompanyId(company.getSelectedId()).stream().filter(x -> !blacklist.contains(x.id())).toList();

                    if (carEntries.isEmpty()) {
                        println("No available cars in the '%s' company".formatted(company.getSelectedName()));
                        currentMenu = 9;
                    } else {
                        StringBuilder tmp = new StringBuilder();

                        for (int i = 0; i < carEntries.size(); i++) {
                            tmp.append("\n").append(i + 1).append(". ").append(carEntries.get(i).name());
                        }
                        tmp.append("\n0. Back");

                        println("""
                            Choose a car:%s""".formatted(tmp.toString()));

                        value = controller.waitForInt();
                        int index = value - 1;

                        if (value == 0) {
                            currentMenu = 9;
                        } else if (index >= 0 && index < carEntries.size()) {
                            car.setSelectedIndex(index);
                            customer.updateRented(carEntries.get(index).id());

                            println("You rented '%s'".formatted(car.getSelectedName()));
                            currentMenu = 9;
                        }
                    }
                    break;
                case  12:
                    if (customer.getSelectedCustomerRentedCar() <= -1) {
                        println("You didn't rent a car!");
                    } else {
                        customer.returnRented();
                        println("You've returned a rented car!");
                    }

                    currentMenu = 9;
                    break;
                case  13:
                    CarEntry carEntry = car.getCarById(customer.getSelectedCustomerRentedCar());
                    if (carEntry == null) {
                        println("You didn't rent a car!");
                    } else {
                        println("""
                            Your rented car:
                            %s
                            Company:
                            %s""".formatted(carEntry.name(), carEntry.companyName()));
                    }

                    currentMenu = 9;
                    break;
                default:
                    println("""
                            1. Log in as a manager
                            2. Log in as a customer
                            3. Create a customer
                            0. Exit""");
                    value = controller.waitForInt();

                    currentMenu = switch (value) {
                        case 0 -> -1;
                        case 1 -> 1;
                        case 2 -> 8;
                        case 3 -> 7;
                        default -> 0;
                    };
            }

            if (currentMenu == -1) {
                inProgress = false;
            }

        }
    }

    private void println(String string) {
        System.out.println("\n" + string);
    }

}
