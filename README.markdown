### Solr Annotation Parser

The Solr Annotation Parser is meant to be an extension to the querying capabilities of [Solrj](http://wiki.apache.org/solr/Solrj). Rather than building the query through Solrj's API calls, this method annotates 'request' classes to signify which parameters should contribute to the various parts of the query (q, fq, rows, start, etc). 

#### Quick Start

To make a class a query parameter class, just add the @QueryParams annotation:

```java
@QueryParams
public class MyQueryParams {
    
}
```

By default, any parameters defined in a @QueryParams class will be used as pieces of the q parameter. Default field name is the name of the bean property being accessed. For example:

```java
@QueryParams
public class MyQueryParams {

    private String field1;
    private String field2;

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public String getField2() {
        return field2;
    }

    public void setField2(String field2) {
        this.field2 = field2;
    }
}
```

This will be translated to the query q=field1:&lt;value&gt; AND field2:&lt;value&gt;. A default operator can be assigned for a class by specifying the defaultOperator value in @QueryParams like so:

```java
@QueryParams(defaultOperator = Operator.OR)
public class MyQueryParams {

}
```

#### Filter Queries

Filter queries are an important part of any Solr query due to their impact on caching and query performance. Any attribute in a @QueryParams class can be made a filter query by adding the @Fq annotation to that member's getter:

```java
@QueryParams
public class MyQueryParams {
    private String filterField1;
    private List<String> filterField2;

    @Fq
    public String getFilterField1() {
        return filterField1;
    }

    public void setFilterField1(String filterField1) {
        this.filterField1 = filterField1;
    }

    @Fq("myfilterfield2")
    public List<String> getFilterField2() {
        return filterField2;
    }

    public void setFilterField2(List<String> filterField2) {
        this.filterField2 = filterField2;
    }
}
```

**Note** two additional behaviors in this example. The first is that filterField2 is a List of Strings rather than a single String value. **All query fields** can be Strings, Lists of String, Arrays of Strings, or any Object whose toString method returns something valid for a Solr query. For the @Fq annotation, the default operator applied to a List or Array is Operator.OR, however this can be overridden by specifying the operator property of @Fq:

```java
@Fq(operator = Operator.AND)
```

The second is the overriding of the default field name for filterField2 to myfilterfield2.

With these two behaviors in mind, the class above would be translated in Solr query terms as fq=filterField1:[value]&fq=myfilterfield2:[&lt;val1&gt; OR &lt;val2&gt; ...]

#### Query field annotation

The default behavior for an unannotated attribute in a class is to use it as part of the q parameter and use the attribute's name as the field name to query. This behavior can be overridden by annotating the getter of an attribute with the @QueryField annotation.

```java
@QueryParams
public class MyQueryParams {

    private String myQueryField;

    @QueryField("myfieldname")
    public String getMyQueryField() {
        return myQueryField;
    }

    public void setMyQueryField(String myQueryField) {
        this.myQueryField = myQueryField;
    }
}
```

With this annotation, the query would now look like q=myfieldname:&lt;val&gt;

Query fields can also be either Strings, Lists of Strings, Arrays of Strings, or any other Object that returns a valid Solr query value with its toString method. Like the @Fq annotation, the operator applied to multiple values can be specified with the operator property of @QueryField. The default operator is Operator.AND.

```java
@QueryParams
public class MyQueryParams {
    private List<String> myQueryField;

    @QueryField(operator = Operator.OR)
    public List<String> getMyQueryField() {
        return myQueryField;
    }

    public void setMyQueryField(List<String> myQueryField) {
        this.myQueryField = myQueryField;
    }
}
```

Would be translated as q=myQueryField:[&lt;val1&gt; OR &lt;val2&gt; OR ...]

#### Start and rows

Paging is an important part of Solr's querying capabilities. It's very easy to add paging parameters to a @QueryParams class. The @Start annotation on an attribute's getter tells the query parser that the value of that attribute should be used as the start parameter for the query. Similarly, the @Rows annotation on an attribute's getter is used for the rows parameter. **Attributes used for @Start and @Rows must be either Integers or ints** beyond that, there is no restriction on these annotations.

```java
@QueryParams
public class MyQueryParams {
    private Integer myStart;
    private int myRows;

    @Start
    public Integer getMyStart() {
        return myStart;
    }

    @Rows
    public int getMyRows() {
        return myRows;
    }
}
```

#### Sorting

Currently, sorting is defined at the class level and is therefore static (not defined at runtime) per request class. This may change at a later date, but I don't think there are many use-cases where dynamic sorting is really necessary. 

To define a sort for the request, add the @Sort annotation to the class. One or multiple sorts can be defined. 

```java
@QueryParams
@Sort({ "myfield1 asc", "myfield2 desc" })
public class MyQueryParams {

}
```

Will output sort=myfield1 asc,myfield2 desc. Alternatively, you can define all sorts as one value, as long as you provide the comma delimiting.

```java
@QueryParams
@Sort("myfield1 asc,myfield2 desc")
public class MyQueryParams {

}
```

#### TODOs and Future Revisions

* Add capability to define facets (most likely per-class like @Sort).
* Add capability to define highlighting, also per-class.
* Nested query params? (if this is something you'd like, drop me a note or something because I'm not sure if it's useful enough for the effort involved)

Ultimately, I'd like to add support for all or close to all of the possible query parameters that Solr provides. Again, if there's something you'd like to see, like if you have a burning need for MLT parameters or something, drop me a line and I'll see what I can do. Otherwise, I'll try and add these in whatever order I choose.
