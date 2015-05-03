package stream;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class StreamTest {

  private StreamTest() {

  }

  public static void main(String[] args) throws Exception {
    // 基于数组创建Stream
    Stream<String> stream = Stream.of("Sam", "Jack", "Tom", "James", "Lucy", "David", "Betty");
    stream = Stream.empty(); // 创建空的Stream
    System.out.println(stream.getClass().getCanonicalName());

    Stream<Double> randomGen = Stream.generate(Math::random); // 基于生成器创建的Stream
    randomGen.limit(10).forEach(System.out::println);

    try (Stream<String> lines = Files.lines(Paths.get("pom.xml"))) {
      lines.limit(1).forEach(System.out::println);
    }

    stream = Stream.of("Sam", "Jack", "Tom", "James", "Lucy", "David", "Betty");

    Predicate<String> lenPredicate = str -> str.length() > 3;
    stream = stream.filter(lenPredicate); // filter方法接受一个Predicate类型的函数

    stream.map(s -> "Hello " + s).forEach(System.out::println); // 使用map函数对集合进行转化

    stream = Stream.of("Sam", "Jack", "Tom", "James", "Lucy", "David", "Betty");
    Stream<Character> charStream = stream.flatMap(w -> charStream(w)); // 将多个流进行平铺
    charStream.forEach(System.out::println);

    stream = Stream.of("Sam", "Jack", "Tom", "James", "Lucy", "David", "Betty");
    Optional<String> maxLengthName =
        stream.max((str1, str2) -> Integer.compare(str1.length(), str2.length()));
    System.out.println(">>>> max length name:" + maxLengthName.get());

    stream = Stream.of("Sam", "Jack", "Tom", "James", "Lucy", "David", "Betty");
    Optional<String> startsWithJ = stream.filter(str -> str.startsWith("J")).findFirst();
    System.out.println(">>>> first with J:" + startsWithJ.get());


  }

  public static Stream<Character> charStream(String s) {
    List<Character> result = new ArrayList<>();
    for (char c : s.toCharArray()) {
      result.add(c);
    }
    return result.stream();
  }
}
