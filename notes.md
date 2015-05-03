# Lambda 表达式

## 基本语法

Java中Lambda的基本语法格式：

(参数列表) -> 表达式

比如:

```
(String first, String second) -> Integer.compare(first.length, second.length);
```

如果Lambda函数体中包含不止一个表达式，那么可以使用花括号引用代码段:

```
(String first, String second) -> {
	if (first.length() < second.length()) return -1;
	else if (first.length() > second.length()) return 1;
	else return 0;
}
```

Lambda表达式的参数可以基于引用的泛型进行类型推导：

```
List<String> strList =
        Arrays.asList("Sam", "Jack", "Peter", "Tom", "Lucy", "Anders", "James", "Lily");
        
System.out.println("before sort:" + strList);
strList.sort((str1, str2) -> Integer.compare(str1.length(), 	str2.length())); // 根据泛型来自动推导
System.out.println("after sort:" + strList);
```
如果参数可以自动推导，并且只有一个参数，那么连小括号都可以省略：

```
strList.forEach(str -> System.out.println(String.format("= %s =", str)));
```

允许加final修饰符和注解，但是加了这些就不能使用自动推导了：

```
strList.forEach((final String str) -> System.out.println(String.format("= %s =", str))); // 可以加final修饰符
```

## 函数式接口

对于只包含一个方法的接口，可以通过使用Lambda表达式来创建该接口的对象，这种接口被称为函数式接口。而且这样做要比传统的内部类的实现方式效率更高。

Java8中并没有引入函数类型（比如String, String -> Int）的概念，而是选择通过函数式接口的方式来引用函数。不能将一个Lambda表达式赋值给一个Object类型，因为Object不是一个函数式接口。

Java在java.util.function包中定义了很多常用的函数式接口形式，比如:

```
BiFunction<String, String, Integer> comparator =
        (str1, str2) -> Integer.compare(str1.length(), str2.length());
```

从概念上说，只要包含一个方法的接口都是函数式接口，但也可以通过@FunctionalInterface注解来标记函数式接口，这样做可以在编译的时候让编译器强制检查是否符合函数式接口的规范。

## 普通方法转换为Lambda

Java中支持将普通方法转换为Lambda表达式，比如：

```
strList.forEach(str -> System.out.println(str));
```

可以转换为:

```
strList.forEach(System.out::println);
```

::操作符用于将普通方法转换为Lambda表达式，支持以下三种形式：

```
对象::实例方法
类::静态方法
类::实例方法
```

前两种方式中，方法引用等同于提供方法参数的lambda表达式。x -> System.out.println(x)即System.out::println，(x, y) -> Math.pow(x,y)即Math::pow

第三种形式，第一个参数会默认成为执行方法的对象，比如:

String::compareToIgnoreCase等同于(x, y) -> x.compareToIgnoreCase(y)

## 构造方法转换为Lambda

构造函数也可以转换为lambda表达式，格式为: ClassName::new，比如：

```
static class Person {

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
}

Stream<Person> personsStream = strList.stream().map(Person::new);
personsStream.forEach(System.out::println);

```

## 作用域

lambda表达式可以访问定义自己的方法作用域的变量，但是不可以在lambda表达式中进行更改，比如：

```
int a = 0, b = 1;

new Thread(() -> {
   System.out.println("a is :" + a);
   System.out.println("b is :" + b);
   // a++; 不可以这样
   // int a = 3; 不可以覆盖定义外围作用域的值
}).start();
```

当在lambda表达式中使用this的时候，将会引用创建该Lambda表达式方法的this参数


## 默认方法

Java8中允许在接口中实现默认方法:

```
static interface Named {
    default String echoName() { // 默认方法的实现
      return "SamChi";
    }
}
```

默认方法同名冲突原则：

1. 如果一个父类提供了具体的实现方法，那么接口中具有相同名称和参数的默认方法会被忽略。（类优先原则）
2. 如果实现的两个接口同时提供了相同名称和参数的默认方法，那么必须手动覆盖此方法来解决这个问题。

## 接口中的静态方法

Java8中支持在接口中定义静态方法，比如Comparator接口中定义了一个reverseOrder的静态方法，方便把转换排序：

```
public static <T extends Comparable<? super T>> Comparator<T> reverseOrder() {
        return Collections.reverseOrder();
}
```

另外还提供了一个键提取函数，可以像Python指定key那样，指定对象中要排序的字段：

Comparator.comparing(Person::getName)

# Stream

## Stream的特点

1. Stream自己不存储元素，元素被存储在底层的集合中，或者根据需要生产出来。
2. Stream操作不会改变源对象，它们会返回一个持有结果的新stream
3. Stream操作可能是延迟执行的，这意味着它们会在等待结果的时候才执行。

Stream同SQL一样，遵循的是做什么而不是怎么去做的原则。

## 创建Stream

通过使用Collection接口中的stream方法，可以将任何集合转化成一个Stream，如果是数组，那么也可以通过Stream.of方法来将其转换为一个Stream

```
Stream<String> stream =
        Stream.of("Sam", "Jack", "Tom", "James", "Lucy", "David", "Betty");
```
Stream.of可以接受数组或者可变长参数。

创建空的Stream:

```
stream = Stream.empty(); // 创建空的Stream
System.out.println(stream.getClass().getCanonicalName());
```

像Python生成器一样创建无限的Stream

```
Stream<Double> randomGen = Stream.generate(Math::random); // 基于生成器创建
randomGen.limit(10).forEach(System.out::println);
```

另外像Files、Regex之类的工具也提供了Stream API的支持，比如:

```
try (Stream<String> lines = Files.lines(Paths.get("pom.xml"))) {
   lines.limit(1).forEach(System.out::println);
}
```

## filter

filter方法的参数是一个Predicate<T>类型的对象，将一个对象T映射成Boolean：

```
Predicate<String> lenPredicate = str -> str.length() > 3;
    stream = stream.filter(lenPredicate); // filter方法接受一个Predicate类型的函数
```

## map

对stream中的每个值应用函数，并将函数收集到一个新的stream当中

## flatMap

将多个流进行平铺

```
stream = Stream.of("Sam", "Jack", "Tom", "James", "Lucy", "David", "Betty");
Stream<Character> charStream = stream.flatMap(w -> charStream(w));
charStream.forEach(System.out::println);
```

```
public static Stream<Character> charStream(String s) {
    List<Character> result = new ArrayList<>();
    for (char c : s.toCharArray()) {
      result.add(c);
    }
    return result.stream();
}
```

## skip、limit、distinct、sorted

略


## 聚合操作

max和min返回的都是Optional类型，比如:

```
Optional<String> maxLengthName =
        stream.max((str1, str2) -> Integer.compare(str1.length(), str2.length()));
    System.out.println(">>>> max length name:" + maxLengthName.get());
```

findFirst可以返回非空集合当中的第一个值，通常跟filter方法合起来使用，例如:

```
Optional<String> startsWithJ = stream.filter(str -> str.startsWith("J")).findFirst();
    System.out.println(">>>> first with J:" + startsWithJ.get());
```

在并行中，如果想要找到任意一个并立即返回，那么可以使用findAny方法

如果只是想知道流中是否有匹配的元素，那么可以使用anyMatch方法，接受一个predicate参数，返回一个boolean值：

allMatch和noneMatch方法会遍历整个stream元素，判断全部符合指定的条件

## Optional<T>

Optional<T>是针对元素T进行的一个NonNullPointer的封装，它不会返回null。调用get方法的时候，如果存在被封装的对象，那么直接返回，如果为null，那么抛出NoSuchElementException。

通过使用isPresent方法可以判断被包装的对象是否有值。

通过使用ifPresent方法可以指定如果判断不为null后执行的操作:

```
startsWithJ.ifPresent(System.out::println); // 如果不为null,那么可以指定处理此值的函数
```
通过使用map方法对值进行转换操作：

```
Optional<String> withHello = startsWithJ.map(s -> String.format("Hello %s", s));
    System.out.println(withHello.orElse(""));
```


通过使用Optional.of静态方法可以创建一个Optional对象，通过使用Optional.empty来创建一个空的Optional对象

使用flatMap方法来组合连续的Optional对象:

如果有一个会返回Optional<T>的方法f()，并且目标类型T有一个会返回Optional<U>的方法g，因为f()返回的是Optional对象，因此连续调用s.f().g()这样是不可取的，要达到连续调用的目标，只能通过flatMap方法，比如:

Optional<U> = s.f().flatMap(T::g);

## Reduce

略





