package com.lyy.yyaddressbook.entity;

import java.io.Serializable;

public class Group implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5741372960884093730L;
	public String id;
	public String name;
	public int count;

	@Override
	public String toString() {
		return "Group [id=" + id + ", name=" + name + "]";
	}

}
