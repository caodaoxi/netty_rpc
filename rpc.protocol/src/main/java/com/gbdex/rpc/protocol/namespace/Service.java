package com.gbdex.rpc.protocol.namespace;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Service {

	public String name() default "";

	public String namespace() default "";

	public String targetInterface() default "";

}
