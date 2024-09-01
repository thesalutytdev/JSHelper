package org.thesalutyt.js.api;

import org.mozilla.javascript.Scriptable;
import org.thesalutyt.js.interfaces.Documentate;

@Documentate(
    description = "Abstract environment. All custom environments must inherit from this class."
)
public abstract class AbstractEnvironment {
    protected String id;
    protected String name;
    protected double version;
    protected String rootDir;
    protected Scriptable scope;

    public AbstractEnvironment(String id, String name, double version, String rootDir) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.rootDir = rootDir;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getVersion() {
        return version;
    }

    public String getRootDir() {
        return rootDir;
    }

    public Scriptable getScope() {
        return scope;
    }

    public void updateScope(Scriptable scope) {
        this.scope = scope;
    }
}
