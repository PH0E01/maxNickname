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

public class Main {
    public static final int TEXTS_TO_GENERATE = 1000000; // Количество текстов для генерации
    public static final int QUEUE_CAPACITY = 100; // Максимальный размер очереди

    public static BlockingQueue<String> queueA = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    public static BlockingQueue<String> queueB = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    public static BlockingQueue<String> queueC = new ArrayBlockingQueue<>(QUEUE_CAPACITY);

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(4); // Создаем пул из 4 потоков, один для генерации и по одному для каждой очереди

        executor.submit(() -> generateTexts());
        executor.submit(() -> findMaxCharacter('a', queueA));
        executor.submit(() -> findMaxCharacter('b', queueB));
        executor.submit(() -> findMaxCharacter('c', queueC));

        executor.shutdown();
    }

    public static void generateTexts() {
        Random random = new Random();
        for (int i = 0; i < TEXTS_TO_GENERATE; i++) {
            StringBuilder text = new StringBuilder();
            int length = random.nextInt(10) + 1; // Генерируем случайную длину от 1 до 10
            for (int j = 0; j < length; j++) {
                char c = (char) ('a' + random.nextInt(3)); // Генерируем случайный символ 'a', 'b' или 'c'
                text.append(c);
            }

            try {
                if (text.charAt(0) == 'a') {
                    queueA.put(text.toString());
                } else if (text.charAt(0) == 'b') {
                    queueB.put(text.toString());
                } else if (text.charAt(0) == 'c') {
                    queueC.put(text.toString());
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void findMaxCharacter(char character, BlockingQueue<String> queue) {
        int maxCount = 0;
        String maxText = "";

        while (!queue.isEmpty() || !Thread.currentThread().isInterrupted()) {
            try {
                String text = queue.take();
                int count = (int) text.chars().filter(ch -> ch == character).count();
                if (count > maxCount) {
                    maxCount = count;
                    maxText = text;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("Максимальное количество символов '" + character + "' в тексте: " + maxCount);
        System.out.println("Текст с максимальным количеством символов '" + character + "': " + maxText);
    }
}
