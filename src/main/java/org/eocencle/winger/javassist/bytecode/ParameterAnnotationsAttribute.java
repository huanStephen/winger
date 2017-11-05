package org.eocencle.winger.javassist.bytecode;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eocencle.winger.javassist.bytecode.AnnotationsAttribute.Copier;
import org.eocencle.winger.javassist.bytecode.annotation.Annotation;
import org.eocencle.winger.javassist.bytecode.annotation.AnnotationsWriter;

public class ParameterAnnotationsAttribute extends AttributeInfo {
	public static final String visibleTag = "RuntimeVisibleParameterAnnotations";
	public static final String invisibleTag = "RuntimeInvisibleParameterAnnotations";

	public ParameterAnnotationsAttribute(ConstPool cp, String attrname, byte[] info) {
		super(cp, attrname, info);
	}

	public ParameterAnnotationsAttribute(ConstPool cp, String attrname) {
		this(cp, attrname, new byte[] { 0 });
	}

	ParameterAnnotationsAttribute(ConstPool cp, int n, DataInputStream in) throws IOException {
		super(cp, n, in);
	}

	public int numParameters() {
		return this.info[0] & 255;
	}

	public AttributeInfo copy(ConstPool newCp, Map classnames) {
		Copier copier = new Copier(this.info, this.constPool, newCp, classnames);

		try {
			copier.parameters();
			return new ParameterAnnotationsAttribute(newCp, this.getName(), copier.close());
		} catch (Exception arg4) {
			throw new RuntimeException(arg4.toString());
		}
	}

	public Annotation[][] getAnnotations() {
		try {
			return (new AnnotationsAttribute.Parser(this.info, this.constPool)).parseParameters();
		} catch (Exception arg1) {
			throw new RuntimeException(arg1.toString());
		}
	}

	public void setAnnotations(Annotation[][] params) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		AnnotationsWriter writer = new AnnotationsWriter(output, this.constPool);

		try {
			int e = params.length;
			writer.numParameters(e);
			int i = 0;

			while (true) {
				if (i >= e) {
					writer.close();
					break;
				}

				Annotation[] anno = params[i];
				writer.numAnnotations(anno.length);

				for (int j = 0; j < anno.length; ++j) {
					anno[j].write(writer);
				}

				++i;
			}
		} catch (IOException arg7) {
			throw new RuntimeException(arg7);
		}

		this.set(output.toByteArray());
	}

	void renameClass(String oldname, String newname) {
		HashMap map = new HashMap();
		map.put(oldname, newname);
		this.renameClass(map);
	}

	void renameClass(Map classnames) {
		AnnotationsAttribute.Renamer renamer = new AnnotationsAttribute.Renamer(this.info, this.getConstPool(), classnames);

		try {
			renamer.parameters();
		} catch (Exception arg3) {
			throw new RuntimeException(arg3);
		}
	}

	void getRefClasses(Map classnames) {
		this.renameClass(classnames);
	}

	public String toString() {
		Annotation[][] aa = this.getAnnotations();
		StringBuilder sbuf = new StringBuilder();
		int k = 0;

		while (k < aa.length) {
			Annotation[] a = aa[k++];
			int i = 0;

			while (i < a.length) {
				sbuf.append(a[i++].toString());
				if (i != a.length) {
					sbuf.append(" ");
				}
			}

			if (k != aa.length) {
				sbuf.append(", ");
			}
		}

		return sbuf.toString();
	}
}
