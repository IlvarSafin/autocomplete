package org.example;

import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

public class NewApp {
    public static void main(String[] args) throws IOException {
        long start;
        Parser parser = new Parser();
        parser.readCSV();
        Scanner scanner = new Scanner(System.in);
        String in = "";
        while (true) {
            System.out.print("Write condition: ");
            in = scanner.nextLine();
            if ("!quit".equals(in)){
                break;
            }
            System.out.print("\nWrite beginning: ");
            String str = scanner.nextLine();
            System.out.println();
            if ("!quit".equals(str)){
                break;
            }
            start = System.currentTimeMillis();
            List<Check> checks = new ArrayList<>();
            StringBuilder line = parser.parseToCondition(in, 0, checks);
            if (line == null){
                continue;
            }
            List<Integer> numList = parser.search(str);
            if (numList == null){
                continue;
            }
            List<String[]> airports = parser.readCSVTwo(checks, line, numList);
            if (airports == null){
                System.out.println("Nothing");
                continue;
            }
            airports.forEach(e -> {
                Arrays.stream(e).forEach(s -> System.out.print(s + " "));
                System.out.println();
            });
            System.out.printf("Airports count: %d\nTime: %d ms\n", airports.size(), (System.currentTimeMillis() - start));
        }
    }
}
