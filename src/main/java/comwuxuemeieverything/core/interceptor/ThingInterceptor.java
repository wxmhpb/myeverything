package comwuxuemeieverything.core.interceptor;
import comwuxuemeieverything.core.model.Thing;
@FunctionalInterface
public interface ThingInterceptor {
    void apply(Thing thing);

}