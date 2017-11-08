package com.hyaroma.dao.hibernate;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.util.*;

/**
 * @author wstv
 *
 */
public class DynamicStatementBuilderImpl implements IDynamicStatementBuilder, ResourceLoaderAware {
	public String[] getFileNames() {
		return fileNames;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(DynamicStatementBuilderImpl.class);

	/**
	 * 模板hql内容
	 */
	private Map<String, String> namedHQLQueries;

	/**
	 * 模板sql 内容
	 */
	private Map<String, String> namedSQLQueries;
	private String[] fileNames;
	private ResourceLoader resourceLoader;
	/**
	 * 查询语句名称缓存，不允许重复
	 */
	private Set<String> nameCache = new HashSet<String>();

	public void setFileNames(String[] fileNames) {
		this.fileNames = fileNames;
	}

	public Map<String, String> getNamedHQLQueries() {
		return namedHQLQueries;
	}

	public Map<String, String> getNamedSQLQueries() {
		return namedSQLQueries;
	}

	public void init() throws IOException {
		namedHQLQueries = new HashMap<String, String>();
		namedSQLQueries = new HashMap<String, String>();
		boolean flag = this.resourceLoader instanceof ResourcePatternResolver;
		for (String file : fileNames) {
			if (flag) {
				Resource[] resources = ((ResourcePatternResolver) this.resourceLoader).getResources(file);
				buildMap(resources);
			} else {
				Resource resource = resourceLoader.getResource(file);
				buildMap(resource);
			}
		}
		//clear name cache
		nameCache.clear();
	}

	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	private void buildMap(Resource[] resources) throws IOException {
		if (resources == null) {
			return;
		}
		for (Resource resource : resources) {
			buildMap(resource);
		}
	}

	/**
	 * 解析dynamic 文件 将信息放入cache
	 * @param resource
	 */
	@SuppressWarnings({ "rawtypes" })
	private void buildMap(Resource resource) {
		InputSource inputSource = null;
		try {
			inputSource = new InputSource(resource.getInputStream());
			SAXReader reader = new SAXReader();
			Document doc = reader.read(inputSource);
			if (isDynamicStatementXml(doc)) {
				final Element dynamicHibernateStatement = doc.getRootElement();
				Iterator rootChildren = dynamicHibernateStatement.elementIterator();
				while (rootChildren.hasNext()) {
					final Element element = (Element) rootChildren.next();
					final String elementName = element.getName();
					if ("sql-query".equals(elementName)) {
						putStatementToCacheMap(resource, element, namedSQLQueries);
					} else if ("hql-query".equals(elementName)) {
						putStatementToCacheMap(resource, element, namedHQLQueries);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.toString());
			e.printStackTrace();
		} finally {
			if (inputSource != null && inputSource.getByteStream() != null) {
				try {
					inputSource.getByteStream().close();
				} catch (IOException e) {
					LOGGER.error(e.toString());
				}
			}
		}

	}

	private void putStatementToCacheMap(Resource resource, final Element element, Map<String, String> statementMap)
			throws Exception {
		String sqlQueryName = element.attribute("name").getText();
		if (nameCache.contains(sqlQueryName)) {
			throw new Exception("sql-query | hql-query名称重复，文件:" + resource.getURI() + "，sql-query | hql-query 不能重复存在.");
		}
		nameCache.add(sqlQueryName);
		String queryText = element.getText();//模板内容信息
		statementMap.put(sqlQueryName, queryText);
	}

	private static boolean isDynamicStatementXml(Document doc) {
		return "dynamic-statement".equals(doc.getRootElement().getName());
	}
}

