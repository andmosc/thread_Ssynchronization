import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final Map<Integer, Integer> map = new TreeMap<>();
    static volatile boolean firstMess = true;
    private static final int nThreads = 10;
    private static final int lengthString = 500;

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);

        long startTs = System.currentTimeMillis();

        Thread th1 = new Thread(() -> currentleader());
        th1.start();

        for (int i = 0; i < 1_000_000; i++) {
            executorService.execute(() -> generateRoute("RLRFR", lengthString));
            //generateRoute("RLRFR", 500);
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }

        th1.interrupt();
        printMap();


        long endTs = System.currentTimeMillis();
        System.out.println("Time: " + (endTs - startTs) + "ms");
    }

    private static void currentleader() {
        while (!Thread.interrupted()) {
            synchronized (map) {
                try {
                    map.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("Max: " + Collections.max(map.values()));
            }
        }
    }

    private static void printMap() {
        Set<Map.Entry<Integer, Integer>> entries = map.entrySet();
        entries.stream().sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                .limit(4)
                .forEach(v -> {
                    if (firstMess) {
                        System.out.println("Самое частое количество повторений "
                                + v.getKey() + " ( встретилось " + v.getValue() + " раз)");
                        System.out.println("Другие размеры: ");
                        firstMess = false;
                    } else {
                        System.out.println("- " + v.getKey() + " (" + v.getValue() + " раз)");
                    }
                });
        System.out.println("....");
    }


    public static void generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        int count = 0;
        char letter;
        for (int i = 0; i < length; i++) {
            letter = letters.charAt(random.nextInt(letters.length()));
            if (letter == 'R') {
                count++;
            }
            route.append(letter);
        }
        route.append(" - ").append(count);

        synchronized (map) {
            map.merge(count, 1, Integer::sum);
            map.notify();
        }
    }
}
