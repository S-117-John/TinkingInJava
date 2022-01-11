//: net/mindview/util/Enums.java
package net.mindview.util;
import java.util.*;

public class Enums {
  private static Random rand = new Random(47);

  /**
   * <T extends Enum<T>>表示T时enum实例
   * Class<T>作为参数，可以利用Class对象得到enum实例的数组
   * @param ec
   * @param <T>
   * @return
   */
  public static <T extends Enum<T>> T random(Class<T> ec) {
    return random(ec.getEnumConstants());
  }
  public static <T> T random(T[] values) {
    return values[rand.nextInt(values.length)];
  }
} ///:~
