package org.thesalutyt.js.api.interpreter;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.thesalutyt.js.api.AbstractEnvironment;
import org.thesalutyt.js.api.scope.Scope;
import org.thesalutyt.js.api.scope.SimpleAsync;
import org.thesalutyt.js.api.scope.SimpleEventManager;
import org.thesalutyt.js.interfaces.Documentate;
import org.thesalutyt.js.json.JSON;
import org.thesalutyt.js.script.Script;

import java.util.ArrayList;

@SuppressWarnings("unused")
@Documentate(
        description = "This class is an actual interpreter."
)
public class Interpreter {
    private Scriptable scope;
    private final String rootDir;
    private final AbstractEnvironment env;
    private final ArrayList<Scope> scopes = new ArrayList<>();

    public Interpreter(AbstractEnvironment env) {
        this.env = env;
        this.rootDir = env.getRootDir();

        EventLoop.getLoopInstance().runImmediate(() -> {
           scopes.add(new SimpleAsync());
           scopes.add(new SimpleEventManager());
           scopes.add(new Script(rootDir));
           scopes.add(new JSON());
        });
    }

    @Documentate(
            description = "Adds a scope to the interpreter."
    )
    public void addScope(Scope scope) {
        scopes.add(scope);
    }

    @Documentate(
            description = "Initializes all scopes."
    )
    public void initScopes() {
        EventLoop.getLoopInstance().runImmediate(() -> {
            scopes.forEach(s -> s.putIntoScope(scope));

            env.updateScope(scope);
        });
    }

    public ArrayList<Scope> getScopes() {
        return scopes;
    }

    public Scriptable getScope() {
        return scope;
    }

    public void close() {
        EventLoop.closeLoopInstance();
    }

    public AbstractEnvironment getEnv() {
        return env;
    }

    public String getRootDir() {
        return rootDir;
    }

    public void executeString(String str) {
        EventLoop.getLoopInstance().runImmediate(() -> Context.getCurrentContext().evaluateString(
                scope,
                str,
                "<cmd>",
                1,
                null
        ));
    }

    public void executeString(String str, String sourceName) {
        EventLoop.getLoopInstance().runImmediate(() -> Context.getCurrentContext().evaluateString(
                scope,
                str,
                sourceName,
                1,
                null
        ));
    }
}