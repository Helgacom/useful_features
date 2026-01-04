package com.helga.lib.gatherer;

import java.util.List;
import java.util.stream.Gatherer;
import java.util.stream.Gatherers;
import java.util.stream.Stream;

public class StreamGatherersExample {

    public static void useFixedWindow(List<String> list, int k) {

        list.stream()
                .gather(Gatherers.windowFixed(k))
                .forEach(sublist -> System.out.printf("%s ", sublist));
        System.out.println();

    }

    public static void useSlidingWindow(List<String> list, int k) {

        list.stream()
                .gather(Gatherers.windowSliding(k))
                .forEach(sublist -> System.out.printf("%s ", sublist));
        System.out.println();
    }

    public static void main(String[] args) {
        var list = List.of(
                "1", "2", "3", "4", "5", "6", "7",
                "8", "9", "10", "11", "12", "13", "14"
        );
        int k = 3;
        useFixedWindow(list, k); // [1, 2, 3] [4, 5, 6] [7, 8, 9] [10, 11, 12] [13, 14]
        useSlidingWindow(list, k); // [1, 2, 3] [2, 3, 4] ... [11, 12, 13] [12, 13, 14]

        // usage custom BiggestInt gather (see the same package)
        System.out.println(Stream.of(5,4,2,1,6,12,8,9)
                .gather(new BiggestInt(11))
                .findFirst()
                .get()); // 12

        System.out.println(Stream.of(5,4,2,1,6,12,8,9)
                .gather(new BiggestInt(5))
                .parallel()
                .findFirst()
                .get()); // 6

        // usage fold(Supplier initial, BiFunction folder) - a many-to-one gatherer that constructs an aggregate incrementally until no more input elements exist
        var semicolonSeparated =
                Stream.of(1,2,3,4,5,6,7,8,9)
                        .gather(
                                Gatherers.fold(
                                        () -> "",
                                        (result, element) -> {
                                            if (result.equals("")) return element.toString();
                                            return result + ";" + element;
                                        }
                                )
                        )
                        .findFirst()
                        .get();
        System.out.println(semicolonSeparated); // 1;2;3;4;5;6;7;8;9

        // usage mapConcurrent(int maxConcurrency, Function mapper) - a one-to-one gatherer that invokes mapper
        // for each input element in the stream concurrently, up to the limit specified by maxConcurrency

        Stream<String> words = Stream.of("a", "b", "c", "d");
        List<String> resultList = words.gather(Gatherers.mapConcurrent(2, String::toUpperCase)).toList();
        System.out.println(resultList); // [A, B, C, D]

        // usage scan(Supplier initial, BiFunction scanner) - a one-to-one gatherer that performs a prefix scan,
        // which is an incremental accumulation

        Stream.of(1,2,3,4,5,6,7,8,9)
                .gather(Gatherers.scan(() -> 100,
                        (current, next) -> current + next))
                .forEach(System.out::println);
        /*
        101
        103
        106
        110
        115
        121
        128
        136
        145
        * */

        // Composing Gatherers
        Gatherer<Integer, ?, Integer> sc =
                Gatherers.scan(() -> 100,
                        (current, next) -> current + next);

        Gatherer<Integer, ?, String> fo =
                Gatherers.fold(() -> "",
                        (result, element) -> {
                            if (result.equals("")) return element.toString();
                            return result + ";" + element;
                        });

        var t = Stream.of(1,2,3,4,5,6,7,8,9)
                .gather(sc.andThen(fo))
                .findFirst()
                .get();
        System.out.println(t); // 101;103;106;110;115;121;128;136;145

        var s = Stream.of(1,2,3,4,5,6,7,8,9)
                .gather(sc)
                .gather(fo)
                .findFirst()
                .get();
        System.out.println(s); // 101;103;106;110;115;121;128;136;145

        // two statements are equivalent, where a and b are gatherers
        //stream.gather(a).gather(b);
        //stream.gather(a.andThen(b));
    }
}
