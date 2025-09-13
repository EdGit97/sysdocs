package com.ed.sysdocs;

import java.io.IOException;
import java.util.Map;

import org.xBaseJ.xBaseJException;

import com.ed.pojo.Property;
import com.ed.pojo.Property.Properties;
import com.ed.sysdocs.dao.PropertiesDbfDao;

public class PropsTest {
	public static final String rootDir = "C:/Src/sysdocs/"; 
	
	public static void main(String [] args) throws SecurityException, xBaseJException, IOException, CloneNotSupportedException {
		PropertiesDbfDao dbf = new PropertiesDbfDao(rootDir);
		Map<Property.Properties, Property> pm = dbf.loadMap();
		
		dumpMap(pm);
		
		Property ml = pm.get(Property.Properties.localPrefix);
		
		ml.setValue("lcl");
		
		dbf.update(ml);
		
		pm = dbf.loadMap();
		dumpMap(pm);
		
		Property p2 = dbf.read(Properties.schedFolder);
		
		System.out.println(p2.getKeyName() + "=" + p2.getValue());
		
		System.out.println(dbf.delete(ml));
		dbf.pack();
				
	}
	
	private static void dumpMap(Map<Property.Properties, Property> m) {
		
		for (Property.Properties pp : m.keySet()) {
			Property p = m.get(pp);
			
			System.out.println(p.getKeyName() + "=" + p.getValue());
			
		}
		
	}

}
