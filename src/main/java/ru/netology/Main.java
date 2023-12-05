package ru.netology;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Main {

    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {
        int numThreads = 1000;

        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(() -> {
                String route = generateRoute("RLRFR", 100);
                int rightTurnCount = countRightTurns(route);

                synchronized (sizeToFreq) {
                    sizeToFreq.merge(rightTurnCount, 1, Integer::sum);
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        printStatistics();
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }

    public static int countRightTurns(String route) {
        int rightTurnCount = 0;
        for (char c : route.toCharArray()) {
            if (c == 'R') {
                rightTurnCount++;
            }
        }
        return rightTurnCount;
    }

    public static void printStatistics() {
        int maxFreq = 0;
        int maxFreqSize = 0;

        System.out.println("Statistics:");

        synchronized (sizeToFreq) {
            for (Map.Entry<Integer, Integer> entry : sizeToFreq.entrySet()) {
                int size = entry.getKey();
                int freq = entry.getValue();

                System.out.println("- " + size + " (" + freq + " times)");

                if (freq > maxFreq) {
                    maxFreq = freq;
                    maxFreqSize = size;
                }
            }
        }

        System.out.println("\nMost frequent size: " + maxFreqSize + " (occurred " + maxFreq + " times)");

        System.out.println("Other sizes:");
        synchronized (sizeToFreq) {
            int finalMaxFreqSize = maxFreqSize;
            sizeToFreq.forEach((size, freq) -> {
                if (size != finalMaxFreqSize) {
                    System.out.println("- " + size + " (" + freq + " times)");
                }
            });
        }
    }
}
