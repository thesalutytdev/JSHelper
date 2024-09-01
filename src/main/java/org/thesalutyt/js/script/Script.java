package org.thesalutyt.js.script;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.thesalutyt.js.api.scope.Scope;
import org.thesalutyt.js.interfaces.Documentate;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Documentate(
    description = "Script. Allows to run JS scripts."
)
public class Script extends Scope {
    private String rootDir;
    public static DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public Script(String rootDir) {
        this.rootDir = rootDir;
    }

    public String getRootDir() {
        return rootDir;
    }

    public void log(Object object) {
        Date now = Calendar.getInstance().getTime();
        System.out.println("[" + Script.DEFAULT_DATE_FORMAT.format(now) + "] " + object.toString());
    }

    public void log(String object) {
        System.out.println(object);
    }

    public void runScript(String name) {
        Path fullPath = Paths.get(this.rootDir + "/" + name).toAbsolutePath();
        if (fullPath.startsWith(this.rootDir)) {
            try {
                InputStreamReader reader = new InputStreamReader(new FileInputStream(fullPath.toString()), StandardCharsets.UTF_8);
                Context ctx = Context.getCurrentContext();
                ctx.evaluateReader(
                        this.getParentScope(),
                        reader,
                        fullPath.toString(),
                        1,
                        null
                );
            } catch (final FileNotFoundException e) {
                System.out.println("Invalid path (" + name + "): file not found");
            } catch (final IOException e) {
                System.out.println("Invalid path (" + name + "): IOException " + e);
            } catch (final RhinoException e) {
                System.out.println("Script error: " + e);
            } catch (final Exception e) {
                System.out.println("Java exception: " + e.getMessage());
            }
        } else {
            System.out.println("Invalid path (" + name + "): path ends outside root script directory");
        }
    }

    @Override
    public void putIntoScope(Scriptable scope) {
        Script script = new Script(this.rootDir);
        script.setParentScope(scope);

        try {
            Method runScript = Script.class.getMethod("runScript", String.class);
            methodsToAdd.add(runScript);
            Method log = Script.class.getMethod("log", Object.class);
            methodsToAdd.add(log);
            Method log2 = Script.class.getMethod("log", String.class);
            methodsToAdd.add(log);
            Method getRootDir = Script.class.getMethod("getRootDir");
            methodsToAdd.add(getRootDir);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        for (Method m : methodsToAdd) {
            FunctionObject methodInstance = new FunctionObject(m.getName(),
                    m, script);
            script.put(m.getName(), script, methodInstance);
        }

        scope.put("Script", scope, script);
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
