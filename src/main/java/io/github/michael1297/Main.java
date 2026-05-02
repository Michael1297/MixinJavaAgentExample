package io.github.michael1297;

import com.thirdparty.demo.BusinessLogic;
import io.github.michael1297.demo.Greeter;
import io.github.michael1297.examples.access.Wallet;
import io.github.michael1297.examples.lifecycle.OrderService;
import io.github.michael1297.examples.modifyconst.Banner;
import io.github.michael1297.examples.putfield.Budget;
import io.github.michael1297.examples.override.MathOps;
import io.github.michael1297.examples.redirect.MessageService;
import io.github.michael1297.examples.returnval.Pricer;
import io.github.michael1297.examples.shadowcall.Echo;

/**
 * Та же демонстрация, что в {@code ClassTransformExample}, но классы подменяются через Sponge Mixin + LegacyLauncher.
 * <p>
 * Сборка: {@code mvn -q package}. Запуск <strong>с миксинами</strong> — только через {@link net.minecraft.launchwrapper.Launch}
 * и {@link io.github.michael1297.launch.MixinDemoTweaker} (см. {@code MIXIN_RU.md}).
 * </p>
 */
public final class Main {

    public static void main(final String[] args) {
        Greeter greeter = new Greeter();
        System.out.println(greeter.greet("World"));

        BusinessLogic logic = new BusinessLogic();
        System.out.println(logic.execute());

        System.out.println("--- examples: redirect / shadow / after-return / return-value / override ---");
        System.out.println(new MessageService().nextMessage());
        System.out.println("wallet coins=" + new Wallet().getCoins());
        new OrderService().process();
        System.out.println("pricer price=" + new Pricer().price());
        System.out.println("math sum(2,3)=" + new MathOps().sum(2, 3));
        System.out.println("echo say=" + new Echo().say());

        Budget budget = new Budget();
        budget.setCap(600);
        System.out.println("budget cap after setCap(600)=" + budget.getCap());
        budget.setCap(100);
        System.out.println("budget cap after setCap(100)=" + budget.getCap());
        System.out.println("banner label=" + new Banner().label());
    }
}
