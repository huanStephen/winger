package org.eocencle.winger.executor.keygen;

import java.sql.Statement;

import org.eocencle.winger.javassist.bytecode.analysis.Executor;
import org.eocencle.winger.mapping.MappedStatement;

public class NoKeyGenerator implements KeyGenerator {
	public void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
	}

	public void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
	}
}
