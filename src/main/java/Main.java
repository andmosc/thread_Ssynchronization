import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final Map<Integer, Integer> map = new TreeMap<>();
    static volatile boolean firstMess = true;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(100);

        long startTs = System.currentTimeMillis();

        for (int i = 0; i < 1_000_000; i++) {
            executorService.execute(() -> generateRoute("RLRFR", 5000));
            //generateRoute("RLRFR", 5000);
        }

        //One good way to shut down the ExecutorService (which is also recommended by Oracle)
        try {
            if (!executorService.awaitTermination(100, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }

        printMap();

        long endTs = System.currentTimeMillis();
        System.out.println("Time: " + (endTs - startTs) + "ms");
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
        route.append(" - " + count);

        synchronized (map) {
            map.merge(count, 1, Integer::sum);
        }
    }
}
