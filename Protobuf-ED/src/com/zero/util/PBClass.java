package com.zero.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PBClass {
	public String parName;
	public String className;
	public String protoName;
	public Set<String> imports = new HashSet<>();
	public List<PBInfo> pbInfos = new ArrayList<>();
}