package me.clickism.clickvillagers.message;

import java.lang.annotation.Documented;

@Documented
public @interface WithParameters {
    String[] value();
}
