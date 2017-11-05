package org.eocencle.winger.javassist;

import java.io.InputStream;
import java.net.URL;

public interface ClassPath {
	InputStream openClassfile(String arg0) throws NotFoundException;

	URL find(String arg0);

	void close();
}
