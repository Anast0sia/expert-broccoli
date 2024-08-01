import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    public static final int QUEUE_SIZE = 100;
    public static final int TEXT_COUNT = 10_000;
    public static final int SYMBOL_COUNT = 100_000;
    public static BlockingQueue<String> a = new ArrayBlockingQueue<>(QUEUE_SIZE);
    public static BlockingQueue<String> b = new ArrayBlockingQueue<>(QUEUE_SIZE);
    public static BlockingQueue<String> c = new ArrayBlockingQueue<>(QUEUE_SIZE);

    public static void main(String[] args) {
        new Thread(() -> {
            for (int i = 0; i < TEXT_COUNT; i++) {
                String text = generateText("abc", SYMBOL_COUNT);
                try {
                    a.put(text);
                    b.put(text);
                    c.put(text);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        new Thread(() -> onThread(a, 'a')).start();
        new Thread(() -> onThread(b, 'b')).start();
        new Thread(() -> onThread(c, 'c')).start();
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static void onThread(BlockingQueue<String> queue, char symbol) {
        String maxText = "";
        int max = 0;
        for (int i = 0; i < QUEUE_SIZE; i++) {
            try {
                int count = 0;
                String text = queue.take();
                for (char c : text.toCharArray()) {
                    if (c == symbol) {
                        count++;
                    }
                }
                if (count > max) {
                    max = count;
                    maxText = text;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("Количество символов " + symbol + " в тексте встретилось " + max + " раз. Текст: " +
                maxText.substring(0, 100));
    }
}