package meal;

import com.leo.annotation.Factory;

@Factory( id = "Tiramisu",type = Meal.class)
public class Tiramisu implements Meal {

  @Override public float getPrice() {
    return 4.5f;
  }
}