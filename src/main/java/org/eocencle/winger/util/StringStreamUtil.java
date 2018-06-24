package org.eocencle.winger.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class StringStreamUtil {
	
	/**
	 * inputStream转outputStream
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public ByteArrayOutputStream isToOs(InputStream in) throws Exception {
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
		int ch;
		while ((ch = in.read()) != -1) { 
			swapStream.write(ch); 
		}
		return swapStream;
	}
	
	/**
	 * outputStream转inputStream
	 * @param out
	 * @return
	 * @throws Exception
	 */
	public ByteArrayInputStream osToIs(OutputStream out) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos = (ByteArrayOutputStream) out;
		ByteArrayInputStream swapStream = new ByteArrayInputStream(baos.toByteArray());
		return swapStream;
	}
	
	/**
	 * inputStream转String
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public String isToStr(InputStream in) throws Exception {
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
		int ch;
		while ((ch = in.read()) != -1) { 
			swapStream.write(ch); 
		}
		return swapStream.toString();
	}
	
	/**
	 * OutputStream转String
	 * @param out
	 * @return
	 * @throws Exception
	 */
	public String osToStr(OutputStream out) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos = (ByteArrayOutputStream) out;
		ByteArrayInputStream swapStream = new ByteArrayInputStream(baos.toByteArray());
		return swapStream.toString();
	}
	
	/**
	 * String转inputStream
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public ByteArrayInputStream strToIs(String in) throws Exception {
		ByteArrayInputStream input = new ByteArrayInputStream(in.getBytes());
		return input;
	}
	
	/**
	 * String转outputStream
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public ByteArrayOutputStream strToOs(String in)throws Exception {
		return this.isToOs(this.strToIs(in));
	}
}
