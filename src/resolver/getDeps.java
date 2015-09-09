package resolver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

public class getDeps {
	
	public static void main(String[] args) throws IOException{
		
		final String projs = "output/pom.xml";
		final String baseSite = "output/libs";
		System.out.println(getDeps(baseSite, projs));
			
	}
	private static void scanPom (String dirString, String baseSite){
		
		Collection <File> found = FileUtils.listFilesAndDirs
				(new File(baseSite+"/libs"), TrueFileFilter.INSTANCE, DirectoryFileFilter.DIRECTORY);
		//List<String>  sPom = new ArrayList();
		
		for (File f : found) {
			if (f.getAbsolutePath().contains(".pom") && f.getAbsolutePath().contains(".pom.sha1")==false){
				if (f.isFile()){
					//sPom.add(f.getAbsolutePath());
					System.out.println(dirString+":"+f.getAbsolutePath());
				}
			}
		}	
		
	}
	
	private static String getDeps(String mavenRepo, String mavenPom){ 
		String result = "";
		try {
			result = ResolveDeps.resolve(mavenRepo,mavenPom);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
    	
	}
	
	
}
