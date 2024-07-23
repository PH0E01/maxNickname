//import java.util.Random;
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.BlockingQueue;
//
//public class Main {
//    public static final int TEXT_TO_GENERATE = 1000000; //Количество текстов для генерации
//    public static final int QUEUE_CAPACITY = 100; // Максимальный размер очереди
//
//    public static BlockingQueue<String> queueA = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
//    public static BlockingQueue<String> queueB = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
//    public static BlockingQueue<String> queueC = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
//
//    public static void main(String[] args) {
//        Thread generatorThread = new Thread(() -> generateTexts());
//        Thread threadA = new Thread(() -> findMaxCharacter('a', queueA));
//        Thread threadB = new Thread(() -> findMaxCharacter('b', queueB));
//        Thread threadC = new Thread(() -> findMaxCharacter('C', queueC));
//
//        generatorThread.start();
//        threadA.start();
//        threadB.start();
//        threadC.start();
//
//        try {
//            generatorThread.join();
//            threadA.join();
//            threadB.join();
//            threadC.join();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public static void generateTexts() {
//        Random random = new Random();
//        for (int i = 0; i < TEXT_TO_GENERATE; i++) {
//            StringBuilder text = new StringBuilder();
//            int length = random.nextInt(10) + 1; //Случайная длина будеьт от 1 до 10
//            for (int j = 0; j < length; j++) {
//                char c = (char) ('a' + random.nextInt(3)); //Случайный символ a b или c
//                text.append(c);
//            }
//            try {
//                if (text.charAt(0) == 'a') {
//                    queueA.put(text.toString());
//                } else if (text.charAt(0) == 'b') {
//                    queueB.put(text.toString());
//                } else if (text.charAt(0) == 'c') {
//                    queueC.put(text.toString());
//                }
//
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }
//
//    public static void findMaxCharacter(char character, BlockingQueue<String> queue) {
//        int maxCount = 0;
//        String maxText = "";
//
//        while (!queue.isEmpty() || !Thread.currentThread().isInterrupted()) {
//            try {
//                String text = queue.take();
//                int count = (int)
//                        text.chars().filter(ch -> ch == character).count();
//                if (count > maxCount) {
//                    maxCount = count;
//                    maxText = text;
//                }
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//        }
//
//        System.out.println("Максимальное количество символой ' " + character + " ' в тексте: " + maxText);
//        System.out.println("Teкст с максимальным количеством символов ' " + character + " ': " + maxText);
//
//
//    }
//}


import java.util.Random;
import java.util.concurrent.*;

public class TextGenerator {

    private static final String LETTERS = "abc";
    private static final int TEXT_LENGTH = 100_000;
    private static final int TEXT_COUNT = 10_000;
    private static final int QUEUE_CAPACITY = 100;

    public static void main(String[] args) {
        ArrayBlockingQueue<String> queueA = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
        ArrayBlockingQueue<String> queueB = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
        ArrayBlockingQueue<String> queueC = new ArrayBlockingQueue<>(QUEUE_CAPACITY);

        Thread generatorThread = new Thread(() -> {
            try {
                for (int i = 0; i < TEXT_COUNT; i++) {
                    String text = generateText(LETTERS, TEXT_LENGTH);
                    queueA.put(text);
                    queueB.put(text);
                    queueC.put(text);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread analyzA = new Thread(() -> analyz(queueA, 'a'));
        Thread analyzB = new Thread(() -> analyz(queueB, 'b'));
        Thread analyzC = new Thread(() -> analyz(queueC, 'c'));

        generatorThread.start();
        analyzA.start();
        analyzB.start();
        analyzC.start();

        try {
            generatorThread.join();
            queueA.put("");
            queueB.put("");
            queueC.put("");

            analyzA.join();
            analyzB.join();
            analyzC.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();

        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static void analyz(ArrayBlockingQueue<String> queue, char character) {
        int maxCount = 0;
        String maxText = "";

        try {
            while (true) {
                String text = queue.take();
                if (text.isEmpty()) {
                    break;
                }
                int count = countCharacter(text, character);
                if (count > maxCount) {
                    maxCount = count;
                    maxText = text;
                }
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Max count of ' " + character + " ' " + maxCount);
        System.out.println("Text with max count of ' " + character + " ' " + maxText.substring(0, 100) + "     ");
    }

    public static int countCharacter(String text, char character) {
        int count = 0;
        for (char c : text.toCharArray()) {
            if (c == character) {
                count++;
            }
        }
        return count;
    }


}