package com.ed.sysdocs;

import java.io.IOException;
import java.util.List;

import org.xBaseJ.xBaseJException;

import com.ed.pojo.Property;
import com.ed.pojo.Property.Properties;

public class PropsTest2 {
	public static final String rootDir = "C:/Src/sysdocs/"; 
	
	public static void main(String [] args) throws SecurityException, xBaseJException, IOException, CloneNotSupportedException {
		PropertiesOps po = new PropertiesOps(rootDir);
		
		dumpList(po.list());
		
		Property p1 = po.get(Properties.localPrefix);
		
		dumpProp(p1);
		
		po.set(Properties.localPrefix, "test");
		po.save();
		
		po.load();
		dumpList(po.list());
		
		po.delete(Properties.schedFolder);
		po.save();
		
		po.load();
		dumpList(po.list());
		
		Property p2 = po.get(Properties.schedFolder);
		
		dumpProp(p2);
		
		po.add(Properties.schedFolder, "\\stest\\");
		po.save();
		po.load();
		dumpList(po.list());
		
	}
	
	private static void dumpProp(Property p) {
		
		System.out.println(p.getKeyName() + "=" + p.getValue());
		
	}
	
	private static void dumpList(List<Property> l) {
		
		if (l.isEmpty()) {
			System.out.println("Empty property list");
		}
		else {
			System.out.println("Property list");
		}
			
		for (Property p : l) {
			dumpProp(p);
		}
		
		System.out.println();
		
	}

}
