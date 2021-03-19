package com.github.mklueh.sexycsv.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by m.kluehspies on 11/03/2021
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CSVColumn {

    String value() default NULL;

    int position() default -1;

    String NULL = "THIS IS THE DEFAULT NULL VALUE";

}