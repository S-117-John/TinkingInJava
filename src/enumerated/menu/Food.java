//: enumerated/menu/Food.java
// Subcategorization of enums within interfaces.
package enumerated.menu;

/**
 * 用enum表示不同类别的食物，同时还希望每个enum元素仍然保持Food类型。
 * 对于enum，实现接口是使其子类化的唯一方法
 */
public interface Food {
  enum Appetizer implements Food {
    SALAD, SOUP, SPRING_ROLLS;
  }
  enum MainCourse implements Food {
    LASAGNE, BURRITO, PAD_THAI,
    LENTILS, HUMMOUS, VINDALOO;
  }
  enum Dessert implements Food {
    TIRAMISU, GELATO, BLACK_FOREST_CAKE,
    FRUIT, CREME_CARAMEL;
  }
  enum Coffee implements Food {
    BLACK_COFFEE, DECAF_COFFEE, ESPRESSO,
    LATTE, CAPPUCCINO, TEA, HERB_TEA;
  }
} ///:~
