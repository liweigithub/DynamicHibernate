package com.hyaroma.dao.hibernate;
import freemarker.template.Template;

/**
 * @author  wstv
 * 执行模板 动态切换sql  hql
 */
public class StatementTemplate {
	private Template template;
	private TYPE type;
	public StatementTemplate(TYPE type, Template template) {
		this.template = template;
		this.type = type;
	}

	public TYPE getType() {
		return type;
	}

	public void setType(TYPE type) {
		this.type = type;
	}

	public Template getTemplate() {
		return template;
	}

	public void setTemplate(Template template) {
		this.template = template;
	}

	public static enum TYPE {
		HQL,SQL
	}
}

