package org.broadleafcommerce.demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

public class DataLoader {
	
	@PersistenceContext(unitName = "blPU")
    protected EntityManager em;
	
	public void init() {
		String baseDirectory = new File(DataLoader.class.getClassLoader().getResource("mysql/load_data.sql").getFile()).getParentFile().getAbsolutePath();
		StringBuffer sb = new StringBuffer();
		boolean eof = false;
		String temp = null;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(DataLoader.class.getClassLoader().getResourceAsStream("mysql/load_data.sql")));
			while(!eof) {
				temp = reader.readLine();
				if (temp != null) {
					int pos = temp.indexOf("@@BASE_DIR@@");
					if (pos >= 0) {
						temp = temp.substring(0, pos) + baseDirectory + temp.substring(pos + "@@BASE_DIR@@".length(), temp.length());
					}
					sb.append(temp);
					sb.append("\n");
				} else {
					eof = true;
				}
			}
		} catch (Throwable e) {
			throw new RuntimeException(e);
		} finally {
			if (reader != null) {
				try{ reader.close(); } catch (Throwable e) {}
			}
		}
		Query query = em.createNativeQuery(sb.toString());
		query.executeUpdate();
	}

}
