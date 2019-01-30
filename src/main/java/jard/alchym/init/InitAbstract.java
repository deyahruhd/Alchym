package jard.alchym.init;

import jard.alchym.AlchymReference;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/***
 *  InitAbstract.java
 *  An abstract base class for all mod-initializing modules to inherit from.
 *
 *  Created by jard at 11:16 PM on December 20, 2018.
 ***/
public abstract class InitAbstract <T> {
    protected final InitAlchym alchym;
    private final Registry<T> registry;

    InitAbstract (Registry <T> registry, InitAlchym alchym) {
        this.registry = registry;
        this.alchym = alchym;
    }

    void preRegister (String id, T obj) { }
    final T register (String id, T obj) {
        preRegister (id, obj);
        return Registry.register (registry, new Identifier (AlchymReference.MODID, id), obj);
    }

    public abstract void initialize ();
}
