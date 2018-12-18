package com.yl.utils;

import java.io.Serializable;

/**
 * @author rbf
 * @createdate 2018/12/4 17:39
 * @desriction
 */
public class BeanMapper implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = -4007501082692838583L;
	
	private String javaType;
    private String javaName;
    private String javabz;
    private String columnName;
    private String jdbcType;

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public String getJavaName() {
        return javaName;
    }

    public void setJavaName(String javaName) {
        this.javaName = javaName;
    }

    public String getJavabz() {
        return javabz;
    }

    public void setJavabz(String javabz) {
        this.javabz = javabz;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(String jdbcType) {
        this.jdbcType = jdbcType;
    }
}
