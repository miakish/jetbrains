package ru.poidem.intellij.plugins.util;

import java.util.ArrayList;
import java.util.List;

import static com.intellij.database.model.DasRoutine.Kind;

/**
 * 09.11.2020
 *
 * @author SSalnikov
 */
public class Routine {

    private String name;
    private Kind type;
    private List<Arg> args = new ArrayList<>();
    private Arg returnArg;
    private String comment;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Arg getReturnArg() {
        return returnArg;
    }

    public void setReturnArg(Arg returnArg) {
        this.returnArg = returnArg;
    }

    public List<Arg> getArgs() {
        return args;
    }

    public void setArgs(List<Arg> args) {
        this.args = args;
    }

    public Kind getType() {
        return type;
    }

    public void setType(Kind type) {
        this.type = type;
    }
}
