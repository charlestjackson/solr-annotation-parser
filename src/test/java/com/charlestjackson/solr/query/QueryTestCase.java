package com.charlestjackson.solr.query;

import java.util.Arrays;

import junit.framework.TestCase;

import org.junit.Test;
import static org.junit.Assert.*;

import com.charlestjackson.solr.query.SolrAnnotationQuery;
import com.charlestjackson.solr.query.requests.BasicQuery;

/**
 * @author cjackson
 *
 */
public class QueryTestCase extends TestCase {
	
	@Test
	public void testSolrAnnotationQueryBasic() {
		BasicQuery req = new BasicQuery();
        req.setField1("a");
        req.setField2("b");
        
        req.setFilterField1("c");
        req.setFilterField2(Arrays.asList(new String[] { "d", "e", "f"}));
        req.setFilterField3(new String[] { "g", "h", "i" });
        
        req.setNumRows(20);
        req.setStartPos(3);
        
        SolrAnnotationQuery q = new SolrAnnotationQuery(req);
        String qs = q.toString();
        
        assertTrue("fq for myfilterfield1 not found or incorrect: " + qs, qs.contains("fq=myfilterfield1%3Ac"));
        assertTrue("fq for myfilterfield3 not found or incorrect: " + qs, qs.contains("fq=myfilterfield3%3A%5Bg+OR+h+OR+i%5D"));
        assertTrue("fq for myfilterfield2 not found or incorrect: " + qs, qs.contains("fq=myfilterfield2%3A%5Bd+OR+e+OR+f%5D"));
        assertTrue("q not found or incorrect: " + qs, qs.contains("q=myfield1%3Aa+AND+myfield2%3Ab"));
        assertTrue("rows not found or incorrect: " + qs, qs.contains("rows=20"));
        assertTrue("start not found or incorrect: " + qs, qs.contains("start=3"));
        assertTrue("sort not found or incorrect: " + qs, qs.contains("sort=myfield1+asc%2Cmyfield2+desc"));
	}
	
}
