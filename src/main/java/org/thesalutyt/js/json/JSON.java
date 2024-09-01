package org.thesalutyt.js.json;

import jdk.nashorn.internal.objects.Global;
import jdk.nashorn.internal.parser.JSONParser;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.Scriptable;
import org.thesalutyt.js.api.scope.Scope;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.util.Scanner;

public class JSON extends Scope {
    public static String stringify(Object obj) {
        return obj.toString();
    }

    public static Object parse(String str) {
        return new JSONParser(str, Global.instance(), true).parse();
    }

    public static Object parse(String str, Boolean strict) {
        return new JSONParser(str, Global.instance(), strict).parse();
    }

    public static void dump(String filePath, Object json) {
        File file = new File(filePath);
        if (file.isDirectory()) throw new RuntimeException("File is a directory");

        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.write(stringify(json));
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                assert writer != null;
                writer.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static Object load(String filePath) {
        try {
            return parse(readFile(filePath));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object load(String filePath, Boolean strict) {
        try {
            return parse(readFile(filePath), strict);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String readFile(String fileFullPath) throws FileNotFoundException {
        File file = new File(fileFullPath);
        Scanner scanner = new Scanner(file);
        StringBuilder fileContains = new StringBuilder();
        while (scanner.hasNextLine()) {
            fileContains.append(scanner.nextLine());
        }

        return fileContains.toString();
    }

    public void putIntoScope(Scriptable scope) {
        JSON ef = new JSON();
        ef.setParentScope(scope);

        try {
            Method stringify = JSON.class.getMethod("stringify", Object.class);
            methodsToAdd.add(stringify);
            Method parse = JSON.class.getMethod("parse", String.class);
            methodsToAdd.add(parse);
            Method parseStrict = JSON.class.getMethod("parse", String.class, Boolean.class);
            methodsToAdd.add(parseStrict);
            Method dump = JSON.class.getMethod("dump", String.class, Object.class);
            methodsToAdd.add(dump);
            Method load = JSON.class.getMethod("load", String.class);
            methodsToAdd.add(load);
            Method loadStrict = JSON.class.getMethod("load", String.class, Boolean.class);
            methodsToAdd.add(loadStrict);
            Method readFile = JSON.class.getMethod("readFile", String.class);
            methodsToAdd.add(readFile);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        for (Method m : methodsToAdd) {
            FunctionObject methodInstance = new FunctionObject(m.getName(),
                    m, ef);
            ef.put(m.getName(), ef, methodInstance);
        }

        scope.put("JSON", scope, ef);
    }

    @Override
    public String getClassName() {
        return "JSON";
    }

    @Override
    public String getResourceId() {
        return "JSON";
    }
}
