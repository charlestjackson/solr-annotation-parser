package com.charlestjackson.solr.query;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.ModifiableSolrParams;

import com.charlestjackson.solr.query.annotations.*;

/**
 * @author cjackson
 *
 */
public class SolrAnnotationQuery extends ModifiableSolrParams {

	public SolrAnnotationQuery(Object obj) {
		Class qc = obj.getClass();
		
		if (!qc.isAnnotationPresent(QueryParams.class))
			throw new RuntimeException("can't build SolrAnnotationQuery without a @QueryParams class");
		
		QueryParams params = (QueryParams)qc.getAnnotation(QueryParams.class);
		qc.getAnnotation(QueryParams.class);
		Operator defaultOperator = params.defaultOperator();
		
		if (qc.isAnnotationPresent(Sort.class)) {
			Sort s = (Sort)qc.getAnnotation(Sort.class);
			for (String val : s.value()) {
				addValueToParam(CommonParams.SORT, val);
			}
		}
		
		List<Method> members = new ArrayList<Method>();
		Class superClazz = qc;
		while (superClazz != null && superClazz != Object.class) {
			if (superClazz.isAnnotationPresent(QueryParams.class)) {
				members.addAll(Arrays.asList(superClazz.getMethods()));
			}
			
			superClazz = superClazz.getSuperclass();
		}
		
		try {
			List<QPart> queryMembers = new ArrayList<QPart>();
			for (Method member : members) {
				if (member.getName().startsWith("get") && !member.getName().equals("getClass")) {
					if (member.isAnnotationPresent(QueryField.class)) {
						queryMembers.add(new QPart(member, defaultOperator));
					} else if (member.isAnnotationPresent(Fq.class)) {
						add(CommonParams.FQ, new QPart(member, defaultOperator).makeQuery(obj));
					} else if (member.isAnnotationPresent(Rows.class)) {
						set(CommonParams.ROWS, (Integer)new QPart(member, defaultOperator).getValue(obj));
					} else if (member.isAnnotationPresent(Start.class)) {
						set(CommonParams.START, (Integer)new QPart(member, defaultOperator).getValue(obj));
					} else {
						// no annotation defaults to just being included as part of the query
						queryMembers.add(new QPart(member, defaultOperator));
					}
				}
			}
			
			StringBuffer q = new StringBuffer();
			boolean first = true;
			for (QPart qpart : queryMembers) {
				if (!first) {
					q.append(" ");
					q.append(defaultOperator.toString());
					q.append(" ");
				}
				q.append(qpart.makeQuery(obj));
				first = false;
			}
			
			set(CommonParams.Q, q.toString());
		} catch (InvocationTargetException invokeEx) {
			throw new RuntimeException(invokeEx);
		} catch (IllegalAccessException illegalEx) {
			throw new RuntimeException(illegalEx);
		}
	}
	  
	private String join(String a, String b, String sep) {
		StringBuilder sb = new StringBuilder();
		if (a!=null && a.length()>0) {
			sb.append(a);
			sb.append(sep);
		} 
		if (b!=null && b.length()>0) {
			sb.append(b);
		}
		return sb.toString().trim();
	}
	  
	private void addValueToParam(String name, String value) {
		String tmp = this.get(name);
		tmp = join(tmp, value, ",");
		this.set(name, tmp);
	}
	   
	private String join(String[] vals, String sep, String removeVal) {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<vals.length; i++) {
			if (removeVal==null || !vals[i].equals(removeVal)) {
				sb.append(vals[i]);
				if (i<vals.length-1) {
					sb.append(sep);
				}
			}
		}
		return sb.toString().trim();
	}
}
