package io.github.michael1297.spring;

import com.thirdparty.demo.BusinessLogic;
import io.github.michael1297.demo.Greeter;
import io.github.michael1297.examples.access.Wallet;
import io.github.michael1297.examples.modifyconst.Banner;
import io.github.michael1297.examples.redirect.MessageService;
import io.github.michael1297.examples.returnval.Pricer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class DemoController {

    @GetMapping("/demo")
    public Map<String, Object> demo() {
        final Map<String, Object> m = new LinkedHashMap<>();
        m.put("greeter", new Greeter().greet("Spring"));
        m.put("business", new BusinessLogic().execute());
        m.put("message", new MessageService().nextMessage());
        m.put("walletCoins", new Wallet().getCoins());
        m.put("pricer", new Pricer().price());
        m.put("banner", new Banner().label());
        return m;
    }
}
