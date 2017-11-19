package org.eocencle.winger.frame;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Request {
	private IntegerProperty id = new SimpleIntegerProperty();
	private IntegerProperty result = new SimpleIntegerProperty();
	private StringProperty protocol = new SimpleStringProperty();
	private StringProperty host = new SimpleStringProperty();
	private StringProperty url = new SimpleStringProperty();
	private IntegerProperty body = new SimpleIntegerProperty();
	private StringProperty caching = new SimpleStringProperty();
	private StringProperty contentType = new SimpleStringProperty();
	private StringProperty process = new SimpleStringProperty();
	private StringProperty comments = new SimpleStringProperty();
	private StringProperty custom = new SimpleStringProperty();
	
	public IntegerProperty idProperty() {
		return this.id;
	}
	public Integer getId() {
		return this.id.get();
	}
	public void setId(Integer id) {
		this.id.set(id);
	}
	public IntegerProperty resultProperty() {
		return this.result;
	}
	public Integer getResult() {
		return result.get();
	}
	public void setResult(Integer result) {
		this.result.set(result);
	}
	public StringProperty protocolProperty() {
		return this.protocol;
	}
	public String getProtocol() {
		return protocol.get();
	}
	public void setProtocol(String protocol) {
		this.protocol.set(protocol);
	}
	public StringProperty hostProperty() {
		return this.host;
	}
	public String getHost() {
		return host.get();
	}
	public void setHost(String host) {
		this.host.set(host);
	}
	public StringProperty urlProperty() {
		return this.url;
	}
	public String getUrl() {
		return url.get();
	}
	public void setUrl(String url) {
		this.url.set(url);
	}
	public IntegerProperty bodyProperty() {
		return this.body;
	}
	public Integer getBody() {
		return body.get();
	}
	public void setBody(Integer body) {
		this.body.set(body);
	}
	public StringProperty cachingProperty() {
		return this.caching;
	}
	public String getCaching() {
		return caching.get();
	}
	public void setCaching(String caching) {
		this.caching.set(caching);
	}
	public StringProperty contentTypeProperty() {
		return this.contentType;
	}
	public String getContentType() {
		return contentType.get();
	}
	public void setContentType(String contentType) {
		this.contentType.set(contentType);
	}
	public StringProperty processProperty() {
		return this.process;
	}
	public String getProcess() {
		return process.get();
	}
	public void setProcess(String process) {
		this.process.set(process);
	}
	public StringProperty commentsProperty() {
		return this.comments;
	}
	public String getComments() {
		return comments.get();
	}
	public void setComments(String comments) {
		this.comments.set(comments);
	}
	public StringProperty customProperty() {
		return this.custom;
	}
	public String getCustom() {
		return custom.get();
	}
	public void setCustom(String custom) {
		this.custom.set(custom);
	}
}
