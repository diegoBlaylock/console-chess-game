package edu.blaylock.utils.gson;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Alternative to transient modifier
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Transient {
}
