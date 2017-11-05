package org.eocencle.winger.javassist;

public class ClassPathList {
	ClassPathList next;
	ClassPath path;

	ClassPathList(ClassPath p, ClassPathList n) {
		this.next = n;
		this.path = p;
	}
}
