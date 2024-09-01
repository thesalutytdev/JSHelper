package org.thesalutyt.js.api.scope;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.thesalutyt.js.interfaces.Documentate;
import org.thesalutyt.js.interfaces.JSResource;

import java.lang.reflect.Method;
import java.util.ArrayList;

@Documentate(
        description = "Abstract scope. All custom scopes must inherit from this class."
)
public abstract class Scope extends ScriptableObject implements JSResource {
    public ArrayList<Method> methodsToAdd = new ArrayList<>();

    public abstract void putIntoScope(Scriptable scope);

    protected Scope() {};

    public abstract String getResourceId();

    public abstract String getClassName();



}
