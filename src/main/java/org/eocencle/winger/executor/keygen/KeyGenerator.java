package org.eocencle.winger.executor.keygen;

import java.sql.Statement;

import org.eocencle.winger.javassist.bytecode.analysis.Executor;
import org.eocencle.winger.mapping.MappedStatement;

public interface KeyGenerator {
	void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter);

	void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter);
}
