package org.thesalutyt.js.api.scope;

import org.mozilla.javascript.*;
import org.thesalutyt.js.api.interpreter.EventLoop;
import org.thesalutyt.js.interfaces.Documentate;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

@Documentate(
    description = "Simple event manager. Its just a simple event emitter."
)
public class SimpleEventManager extends Scope {
    private static ArrayList<String> availableEvents = new ArrayList<>();
    public HashMap<String, ArrayList<BaseFunction>> events = new HashMap<>();

    static  {
        EventLoop.getLoopInstance().runImmediate(() -> {
            availableEvents.add("start");
            availableEvents.add("stop");
            availableEvents.add("error");
        });
    }

    public void addEventListener(String eventName, BaseFunction fn) {
        EventLoop.getLoopInstance().runImmediate(() -> {
            if (!availableEvents.contains(eventName)) throw new RuntimeException("Unknown event: " + eventName);
            if (!events.containsKey(eventName)) {
                ArrayList<BaseFunction> list = new ArrayList<>();
                list.add(fn);
                events.put(eventName, list);
            }
        });
    }

    public void removeEventListener(String eventName, BaseFunction fn) {
        EventLoop.getLoopInstance().runImmediate(() -> {
            if (!availableEvents.contains(eventName)) throw new RuntimeException("Unknown event: " + eventName);
            if (!events.containsKey(eventName)) return;
            events.remove(eventName);
        });
    }

    public void runEvent(String eventName, NativeArray args) {
        EventLoop.getLoopInstance().runImmediate(() -> {
            if (!availableEvents.contains(eventName)) throw new RuntimeException("Unknown event: " + eventName);
            if (!events.containsKey(eventName)) return;
            for (BaseFunction fn : events.get(eventName)) {
                Context ctx = Context.getCurrentContext();
                fn.call(ctx, this, this, args.toArray());
            }
        });
    }

    public void runEvent(String eventName) {
        EventLoop.getLoopInstance().runImmediate(() -> {
            if (!availableEvents.contains(eventName)) throw new RuntimeException("Unknown event: " + eventName);
            if (!events.containsKey(eventName)) return;
            for (BaseFunction fn : events.get(eventName)) {
                Context ctx = Context.getCurrentContext();
                fn.call(ctx, this, this, new Object[0]);
            }
        });
    }

    @Override
    public void putIntoScope(Scriptable scope) {

        SimpleEventManager as = new SimpleEventManager();
        as.setParentScope(scope);

        try {
            Method add = SimpleEventManager.class.getMethod("addEventListener", String.class, BaseFunction.class);
            methodsToAdd.add(add);
            Method remove = SimpleEventManager.class.getMethod("removeEventListener", String.class, BaseFunction.class);
            methodsToAdd.add(remove);
            Method run = SimpleEventManager.class.getMethod("runEvent", String.class, NativeArray.class);
            methodsToAdd.add(run);
            Method run2 = SimpleEventManager.class.getMethod("runEvent", String.class);
            methodsToAdd.add(run2);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        for (Method m : methodsToAdd) {
            FunctionObject methodInstance = new FunctionObject(m.getName(),
                    m, as);
            as.put(m.getName(), as, methodInstance);
        }

        scope.put("EventManager", scope, as);
    }

    @Override
    public String getResourceId() {
        return "";
    }

    @Override
    public String getClassName() {
        return "";
    }
}
