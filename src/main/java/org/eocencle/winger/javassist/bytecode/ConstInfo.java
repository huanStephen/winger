package org.eocencle.winger.javassist.bytecode;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public abstract class ConstInfo {
	int index;

	public ConstInfo(int i) {
		this.index = i;
	}

	public abstract int getTag();

	public String getClassName(ConstPool cp) {
		return null;
	}

	public void renameClass(ConstPool cp, String oldName, String newName, HashMap cache) {
	}

	public void renameClass(ConstPool cp, Map classnames, HashMap cache) {
	}

	public abstract int copy(ConstPool arg0, ConstPool arg1, Map arg2);

	public abstract void write(DataOutputStream arg0) throws IOException;

	public abstract void print(PrintWriter arg0);

	public String toString() {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		PrintWriter out = new PrintWriter(bout);
		this.print(out);
		return bout.toString();
	}
}
