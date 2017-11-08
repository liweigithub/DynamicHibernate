package com.hyaroma.dao.hibernate.function;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.QueryException;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

/**
 * @author  wstv
 */
public class BitAndFunction implements SQLFunction {

	@Override
	public Type getReturnType(Type type, Mapping mapping) {
		return StandardBasicTypes.INTEGER;
	}

	@Override
	public String render(Type firstArgumentType, List arguments, SessionFactoryImplementor factory) throws QueryException {
		if (arguments.size() != 2) {
			throw new IllegalArgumentException("BitAndFunction requires 2 arguments!");
		}
		return arguments.get(0).toString() + " & " + arguments.get(1).toString();
	}

	@Override
	public boolean hasArguments() {
		return true;
	}

	@Override
	public boolean hasParenthesesIfNoArguments() {
		return true;
	}



}
