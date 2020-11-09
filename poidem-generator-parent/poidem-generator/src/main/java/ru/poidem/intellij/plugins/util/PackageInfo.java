package ru.poidem.intellij.plugins.util;

import com.intellij.database.model.DasArgument;
import com.intellij.database.model.ObjectKind;
import com.intellij.database.psi.DbElement;
import com.intellij.database.psi.DbPackage;
import com.intellij.database.psi.DbRoutine;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.intellij.database.model.DasRoutine.Kind.FUNCTION;
import static com.intellij.database.model.DasRoutine.Kind.PROCEDURE;

/**
 * 09.11.2020
 *
 * @author SSalnikov
 */
public class PackageInfo {

    private final DbPackage dbPackage;

    private List<Routine> routines= new ArrayList<>();

    public PackageInfo(DbPackage dbPackage) {
        this.dbPackage = dbPackage;
        loadRoutines();
    }

    public String getName() {
        return dbPackage.getName();
    }

    public String getSchema() {
        DbElement parent = dbPackage.getParent();
        if(parent!=null) {
            return parent.getName();
        } else {
            return "";
        }

    }

    public List<Routine> getRoutines() {
        return routines;
    }

    public void loadRoutines(){
            for(DbElement dr : dbPackage.getDasChildren(ObjectKind.ROUTINE)){
                DbRoutine opr = (DbRoutine) dr;
                if(opr.getRoutineKind()== FUNCTION) {
                    Routine r = new Routine();
                    r.setName(opr.getName());
                    r.setType(opr.getRoutineKind());
                    r.setReturnArg(Util.getArg(opr.getReturnArgument()));
                    for (DasArgument a : opr.getArguments()) {
                        if(StringUtils.isNotEmpty(a.getName())) {
                            r.getArgs().add(Util.getArg(a));
                        }
                    }
                    r.setComment(opr.getComment());
                    routines.add(r);
                } else if(opr.getRoutineKind() == PROCEDURE){
                    Routine r = new Routine();
                    r.setName(opr.getName());
                    r.setType(opr.getRoutineKind());
                    for (DasArgument a : opr.getArguments()) {
                        if(StringUtils.isNotEmpty(a.getName())) {
                            r.getArgs().add(Util.getArg(a));
                        }
                    }
                    r.setComment(opr.getComment());
                    routines.add(r);
                }
            }
    }
}
