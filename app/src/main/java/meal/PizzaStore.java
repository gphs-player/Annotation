package meal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PizzaStore {
    private MealFactoryByHand factory = new MealFactoryByHand();
    public Meal order(String mealName) {

//        if (mealName == null) {
//            throw new IllegalArgumentException("Name of the meal is null!");
//        }
//
//        if ("Margherita".equals(mealName)) {
//            return new MargheritaPizza();
//        }
//
//        if ("Calzone".equals(mealName)) {
//            return new CalzonePizza();
//        }
//
//        if ("Tiramisu".equals(mealName)) {
//            return new Tiramisu();
//        }
//
//        throw new IllegalArgumentException("Unknown meal '" + mealName + "'");
        return factory.create(mealName);
    }

    public static String readConsole() throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        return bufferedReader.readLine();
    }
    public static void main(String[] args) throws IOException {
        PizzaStore pizzaStore = new PizzaStore();
        Meal meal = pizzaStore.order(readConsole());
        System.out.println("Bill: $" + meal.getPrice());
    }
}