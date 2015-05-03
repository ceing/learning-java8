package lambda;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public final class Lambda {

  private Lambda() {

  }

  static class Person implements Named, EchoName {

    private String name;

    Person(String name) {
      this.name = name;
    }

    public String getName() {
      return this.name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String toString() {
      return String.format("My name is %s", this.name);
    }

    @Override
    public String echoName() {
      return Named.super.echoName(); // 重名了必须要手动指定实现
    }

  }

  static interface Named {
    default String echoName() { // 默认方法的实现
      return "SamChi";
    }
  }

  static interface EchoName {
    default String echoName() {
      return "James";
    }
  }

  public static void main(String[] args) {
    List<String> strList =
        Arrays.asList("Sam", "Jack", "Peter", "Tom", "Lucy", "Anders", "James", "Lily");
    System.out.println("before sort:" + strList);
    strList.sort((str1, str2) -> Integer.compare(str1.length(), str2.length())); // 根据泛型来自动推导
    System.out.println("after sort:" + strList);
    strList.forEach(str -> System.out.println(String.format("= %s =", str))); // 可类型推导的单个参数无需加小括号
    strList.forEach((final String str) -> System.out.println(String.format("= %s =", str))); // 可以加final修饰符

    // java.util.function包中的函数式接口
    BiFunction<String, String, Integer> comparator =
        (str1, str2) -> Integer.compare(str1.length(), str2.length());
    System.out.println(comparator instanceof Object);
    System.out.println(comparator.getClass().getCanonicalName());

    Object obj = comparator;
    System.out.println(obj.getClass().getCanonicalName());
    // obj = (String str1, String str2) -> Integer.compare(str1.length(), str2.length()); 这样引用就不可以了。

    Runnable sleeper = () -> {
      System.out.println("Zzzzzz");
      try {
        TimeUnit.SECONDS.sleep(1L);
      } catch (InterruptedException e) {
        e.printStackTrace(); // 在Lambda表达式中捕获异常
      }
    };
    Thread t = new Thread(sleeper);
    t.start();

    strList.forEach(str -> System.out.println(str));
    strList.forEach(System.out::println);
    strList.sort(String::compareToIgnoreCase);

    Stream<Person> personsStream = strList.stream().map(Person::new);
    personsStream.forEach(System.out::println);

    int a = 0, b = 1;

    new Thread(() -> {
      System.out.println("a is :" + a);
      System.out.println("b is :" + b);
      // a++; 不可以改变外围作用域的值
      // int a = 3; 不可以覆盖定义外围作用域的值
      }).start();

    Person p = new Person("Jack");
    System.out.println(">>>>>" + p.getName());
  }
}
