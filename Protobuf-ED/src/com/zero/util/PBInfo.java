package com.zero.util;

public class PBInfo {
	public boolean collection;
	public boolean pfile;
	public String type;
	public String name;
	
	public boolean isCollection() {
		return collection;
	}
	public void setCollection(boolean collection) {
		this.collection = collection;
	}
	public boolean isPfile() {
		return pfile;
	}
	public void setPfile(boolean pfile) {
		this.pfile = pfile;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}