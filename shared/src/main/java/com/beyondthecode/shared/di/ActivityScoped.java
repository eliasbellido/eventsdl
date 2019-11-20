package com.beyondthecode.shared.di;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Por defecto, si no hay un scope annotation presente, el inyector crea una instancia,
 * usa la instancia para una inyección, y luego la olvida. Si un scope annotation está presente,
 * el inyector podría retener la instancia para un posible reuso en una inyección posterior.
 *
 * TL;DR:
 * No scope = new instance created every time
 * [@Singleton] = only one instance
 * [@CustomScope] = instance reused depending on the component's lifecycle
 * */
@Documented
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface ActivityScoped {
}
