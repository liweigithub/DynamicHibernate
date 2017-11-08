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
public class SumQtyFunction implements SQLFunction{
	@Override
	public Type getReturnType(Type type, Mapping mapping) {
		return StandardBasicTypes.INTEGER;
	}

	@Override
	public String render(Type firstArgumentType, List arguments, SessionFactoryImplementor factory) throws QueryException {
		if (arguments.size() != 1) {
			throw new IllegalArgumentException("BitAndFunction requires 1 arguments!");
		}
		return "sum("+arguments.get(0).toString()+")";
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
