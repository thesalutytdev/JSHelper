package org.thesalutyt.js.api.scope;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.Scriptable;
import org.thesalutyt.js.api.interpreter.EventLoop;
import org.thesalutyt.js.interfaces.Documentate;

import java.lang.reflect.Method;

@Documentate(
    description = "Simple async. Its just a simple timeout and interval."
)
public class SimpleAsync extends Scope {
    @Documentate(
            description = "Sets a timeout. Returns the timeout id."
    )
    public Integer setTimeout(BaseFunction fn, Integer delay) {
        Runnable callback = () -> {
            Context ctx = Context.getCurrentContext();
            fn.call(ctx, this, this, new Object[0]);
        };

        return EventLoop.getLoopInstance().runTimeout(callback, delay);
    }

    @Documentate(
            description = "Clears a timeout."
    )
    public void clearTimeout(Integer id) {
        EventLoop.getLoopInstance().resetTimeout(id);
    }

    @Documentate(
            description = "Sets an interval. Returns the interval id."
    )
    public Integer setInterval(BaseFunction fn, Integer delay) {
        Runnable callback = () -> {
            Context ctx = Context.getCurrentContext();
            fn.call(ctx, this, this, new Object[0]);
        };


        return EventLoop.getLoopInstance().runInterval(callback, delay);
    }

    @Documentate(
            description = "Clears an interval."
    )
    public void clearInterval(Integer id) {
        EventLoop.getLoopInstance().resetInterval(id);
    }

    @Override
    public void putIntoScope(Scriptable scope) {
        SimpleAsync as = new SimpleAsync();
        as.setParentScope(scope);

        try {
            Method setTimeout = SimpleAsync.class.getMethod("setTimeout", BaseFunction.class, Integer.class);
            methodsToAdd.add(setTimeout);
            Method clearTimeout = SimpleAsync.class.getMethod("clearTimeout", Integer.class);
            methodsToAdd.add(clearTimeout);
            Method setInterval = SimpleAsync.class.getMethod("setInterval", BaseFunction.class, Integer.class);
            methodsToAdd.add(setInterval);
            Method clearInterval = SimpleAsync.class.getMethod("clearInterval", Integer.class);
            methodsToAdd.add(clearInterval);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        for (Method m : methodsToAdd) {
            FunctionObject methodInstance = new FunctionObject(m.getName(),
                    m, as);
            as.put(m.getName(), as, methodInstance);
        }

        scope.put("Async", scope, as);
    }

    @Override
    public String getResourceId() {
        return "SimpleAsync";
    }

    @Override
    public String getClassName() {
        return getClass().getName();
    }
}
