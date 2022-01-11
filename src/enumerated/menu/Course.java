//: enumerated/menu/Course.java
package enumerated.menu;
import net.mindview.util.*;

/**
 * 如果想创建枚举的枚举，可以创建一个新的enum，然后用其实例包装Food中的每一个enum类
 */
public enum Course {
  APPETIZER(Food.Appetizer.class),
  MAINCOURSE(Food.MainCourse.class),
  DESSERT(Food.Dessert.class),
  COFFEE(Food.Coffee.class);
  private Food[] values;
  private Course(Class<? extends Food> kind) {
    values = kind.getEnumConstants();
  }
  public Food randomSelection() {
    return Enums.random(values);
  }
} ///:~
