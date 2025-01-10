package me.clickism.clickvillagers.util;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Parameterizer {

    protected static final String FORMAT = "{%s}";

    private final String string;
    protected final Map<String, Object> params = new HashMap<>();

    protected boolean colorize = true;

    protected Parameterizer(String string) {
        this.string = string;
    }

    public Parameterizer put(String key, @NotNull Object value) {
        this.params.put(key, value);
        return this;
    }

    public Parameterizer putAll(Parameterizer parameterizer) {
        this.params.putAll(parameterizer.params);
        return this;
    }

    public Parameterizer disableColorizeParameters() {
        this.colorize = false;
        return this;
    }

    public String replace(String string) {
        String result = Utils.colorize(string);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String placeholder = String.format(FORMAT, entry.getKey());
            result = result.replace(placeholder, entry.getValue().toString());
        }
        if (colorize) result = Utils.colorize(result);
        return result;
    }

    @Override
    public String toString() {
        return replace(this.string);
    }

    public static Parameterizer empty() {
        return new Parameterizer("");
    }

    public static Parameterizer of(String string) {
        return new Parameterizer(string);
    }
}
