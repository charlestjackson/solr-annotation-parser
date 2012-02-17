package com.charlestjackson.solr.query.requests;

import java.util.List;

import com.charlestjackson.solr.query.annotations.Fq;
import com.charlestjackson.solr.query.annotations.QueryField;
import com.charlestjackson.solr.query.annotations.QueryParams;
import com.charlestjackson.solr.query.annotations.Rows;
import com.charlestjackson.solr.query.annotations.Sort;
import com.charlestjackson.solr.query.annotations.Start;

/**
 * @author cjackson
 *
 */
@QueryParams
@Sort({ "myfield1 asc", "myfield2 desc" })
public class BasicQuery {

	private String field1;
	private String field2;
	private String filterField1;
	private List<String> filterField2;
	private String[] filterField3;
	private int numRows;
	private int startPos;
	private String[] sorts;
	
	@QueryField("myfield1")
	public String getField1() {
		return field1;
	}

	public void setField1(String field1) {
		this.field1 = field1;
	}

	@Fq("myfilterfield1")
	public String getFilterField1() {
		return filterField1;
	}

	public void setFilterField1(String filterField1) {
		this.filterField1 = filterField1;
	}

	@Rows
	public int getNumRows() {
		return numRows;
	}

	public void setNumRows(int numRows) {
		this.numRows = numRows;
	}

	@QueryField("myfield2")
	public String getField2() {
		return field2;
	}

	public void setField2(String field2) {
		this.field2 = field2;
	}

	@Fq("myfilterfield3")
	public String[] getFilterField3() {
		return filterField3;
	}

	public void setFilterField3(String[] filterField3) {
		this.filterField3 = filterField3;
	}

	@Fq("myfilterfield2")
	public List<String> getFilterField2() {
		return filterField2;
	}

	public void setFilterField2(List<String> filterField2) {
		this.filterField2 = filterField2;
	}

	@Start
	public int getStartPos() {
		return startPos;
	}

	public void setStartPos(int startPos) {
		this.startPos = startPos;
	}
}
