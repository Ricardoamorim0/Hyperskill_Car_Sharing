package carsharing.controller;

import java.util.Scanner;

public class InputController {

    private final Scanner scanner;

    public InputController(Scanner scanner) {
        this.scanner = scanner;
    }

    public String waitForString() {
        scanner.nextLine();
        return scanner.nextLine();
    }

    public int waitForInt() {
        String value = scanner.next();
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return waitForInt();
        }
    }

}
