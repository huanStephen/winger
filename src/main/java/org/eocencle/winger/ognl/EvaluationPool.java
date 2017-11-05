package org.eocencle.winger.ognl;

import java.util.List;

public class EvaluationPool {
	public EvaluationPool() {
		this(0);
	}

	public EvaluationPool(int initialSize) {
	}

	public Evaluation create(SimpleNode node, Object source) {
		return this.create(node, source, false);
	}

	public Evaluation create(SimpleNode node, Object source, boolean setOperation) {
		return new Evaluation(node, source, setOperation);
	}

	public void recycle(Evaluation value) {
	}

	public void recycleAll(Evaluation value) {
	}

	public void recycleAll(List value) {
	}

	public int getSize() {
		return 0;
	}

	public int getCreatedCount() {
		return 0;
	}

	public int getRecoveredCount() {
		return 0;
	}

	public int getRecycledCount() {
		return 0;
	}
}
