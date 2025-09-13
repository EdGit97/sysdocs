package com.ed.sysdocs;

import java.io.IOException;
import org.xBaseJ.xBaseJException;

import com.ed.sysdocs.dao.DBFUtilities;
import com.ed.sysdocs.dao.MediaDbfDao;
import com.ed.pojo.DbfMetaData;

public class DBFStruTest extends DBFUtilities {
	public static final String rootDir = "C:/Src/sysdocs/"; 
	
	public DBFStruTest() {
		super(rootDir);
	}
	
	public static void main(String [] args) throws SecurityException, xBaseJException, IOException {
		DBFStruTest st = new DBFStruTest();
		String fs = st.buildFileSpec(rootDir, MediaDbfDao.dataFileName);
		String ndx1 = st.buildFileSpec(rootDir, MediaDbfDao.index1Name);
		String ndx2 = st.buildFileSpec(rootDir, MediaDbfDao.index2Name);
		DbfMetaData md = st.getMetaData(fs, ndx1, ndx2);
		
		System.out.println(md.generateTableMetaData());
		
	}

	/* (non-Javadoc)
	 * @see com.ed.sysdocs.dao.DBFUtilities#create()
	 */
	
	@Override
	public void create() throws SecurityException, xBaseJException, IOException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.ed.sysdocs.dao.DBFUtilities#index()
	 */
	
	@Override
	public void index() throws SecurityException, xBaseJException, IOException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.ed.sysdocs.dao.DBFUtilities#pack()
	 */
	
	@Override
	public void pack() throws xBaseJException, IOException, SecurityException, CloneNotSupportedException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.ed.sysdocs.dao.DBFUtilities#loadMetaData()
	 */
	
	@Override
	public DbfMetaData loadMetaData() throws xBaseJException, IOException, SecurityException {
		// TODO Auto-generated method stub
		return null;
	}

}
