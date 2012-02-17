package com.charlestjackson.solr.query;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import com.charlestjackson.solr.query.annotations.Fq;
import com.charlestjackson.solr.query.annotations.Operator;
import com.charlestjackson.solr.query.annotations.QueryField;

/**
 * @author cjackson
 *
 */
public class QPart {
	private String name;
    private Field field;
    private Method getter;
    private Class type;
    private boolean isArray = false, isList=false;
    private Operator op;
    
    public QPart(AccessibleObject member, Operator defaultOp) {
    	op = defaultOp;
    	
    	if (member instanceof Field) {
    		field = (Field)member;
    		type = field.getType();
    	} else {
    		getter = (Method)member;
    		type = getter.getReturnType();
//    		findSetter();
    	}
    	
    	extractName();
    	
    	if (type.isAssignableFrom(List.class))
    		isList = true;
    	
    	if (type.isArray()) {
    		isArray = true;
    		type = type.getComponentType();
    	}
    		
    }
    
    private void extractName() {
    	if (field != null) {
    		if (field.isAnnotationPresent(QueryField.class)) {
    			QueryField qf = field.getAnnotation(QueryField.class);
    			op = qf.operator();
    			if (qf.value() != null) {
    				name = qf.value();
    				return;
    			}
    		}
    		name = field.getName();
    		return;
    	}
    	
    	if (getter != null) {
    		if (getter.isAnnotationPresent(QueryField.class)) {
    			QueryField qf = getter.getAnnotation(QueryField.class);
    			op = qf.operator();
	    		if (qf.value() != null) {
	    			name = qf.value();
	    			return;
	    		}	
    		}
    		
    		if (getter.isAnnotationPresent(Fq.class)) {
    			Fq fq = getter.getAnnotation(Fq.class);
    			op = fq.operator();
    			if (fq.value() != null) {
    				name = fq.value();
    				return;
    			}
    		}
    		
			String mname = getter.getName();
    		mname = mname.replace("get", "");
    		mname = mname.substring(0, 1).toLowerCase() + mname.substring(1);
    		name = mname;
    	}	    	
    }
    
    public Object getValue(Object instance) throws IllegalAccessException, InvocationTargetException {
    	if (field != null)
    		return field.get(instance);
    	
		return getter.invoke(instance, (Object[])null);
    }
    
    public Class getType() {
    	return type;
    }
    
    public String makeQuery(Object instance) throws IllegalAccessException, InvocationTargetException {
    	StringBuffer q = new StringBuffer(name);
    	q.append(":");
    	if (isList) {
    		q.append("[");
    		List vals = (List)getValue(instance);
    		boolean first = true;
    		for (Object val : vals) {
    			if (!first) {
    				q.append(" ");
	    			q.append(op.toString());
	    			q.append(" ");	
    			}
    			q.append(val);
    			first = false;
    		}
    		q.append("]");
    	} else if (isArray) {
    		q.append("[");
    		Object[] vals = (Object[])getValue(instance);
    		boolean first = true;
    		for (Object val : vals) {
    			if (!first) {
    				q.append(" ");
    				q.append(op.toString());
    				q.append(" ");
    			}
    			q.append(val);
    			first = false;
    		}
    		q.append("]");
    	} else {
    		q.append(getValue(instance));
    	}
    	
    	return q.toString();
    }
    
    public String[] getValueStrings(Object instance) throws InvocationTargetException, IllegalAccessException {
    	Object obj = getValue(instance);
		String[] vals;
		if (isList) {
			List l = (List) obj;
			vals = new String[l.size()];
			for (int i = 0; i < l.size(); i++) {
				vals[i] = l.get(i).toString();
			}
			return vals;
		} else if (isArray) {
			Object[] a = (Object[]) obj;
			vals = new String[a.length];
			for (int i = 0; i < a.length; i++) {
				vals[i] = a[i].toString();
			}
			return vals;
		} else {
			vals = new String[] { obj.toString() };
		}
		
		return vals;
    }
}
