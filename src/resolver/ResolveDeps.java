package resolver;
import com.jcabi.aether.Aether;
import com.jcabi.aether.Classpath;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.Repository;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.DependencyResolutionException;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.util.artifact.JavaScopes;


public class ResolveDeps {
	
	public static String resolve(String fileRepo, String pomXml) throws InterruptedException {
	  	StringBuffer classpathMe = new StringBuffer ();
	    File local = new File(fileRepo+"/libs");
	    Collection<RemoteRepository> remotes = new LinkedList<RemoteRepository>();
	    remotes.add(new RemoteRepository(
		        "maven-central",
		        "default",
		        "http://central.maven.org/maven2/"
		      ));
	    try {
	    	final File pomXmlFile = new File(pomXml);
	    	final Reader reader = new FileReader(pomXmlFile);
            final Model model;
            final MavenXpp3Reader xpp3Reader = new MavenXpp3Reader();
            
        try{
        	TimeUnit.SECONDS.sleep(2);
            model = xpp3Reader.read(reader);
            List<Dependency> dependencies;
            List<Repository> remotePOM;
            remotePOM = model.getRepositories();
            
            for(Repository r: remotePOM){
            	remotes.add(new RemoteRepository(
            			r.getId(),
            			"default",
            			r.getUrl()
            	      ));
            	
            }
            
            if(model.getDependencyManagement()==null){
            	dependencies = model.getDependencies();	
            }else{
                DependencyManagement dm = model.getDependencyManagement();
                dependencies = dm.getDependencies();
            }
            
			Iterator<org.apache.maven.model.Dependency> it = dependencies.iterator();
			while (it.hasNext()) {
			      org.apache.maven.model.Dependency depend = it.next();
			      try{
			    	  String version = depend.getVersion();
			    	  
			    	  if(version==null){
			    		  version = "LATEST";
			    	  }else{
			    		  version = depend.getVersion();
			    	  }
			    	  
			    	  if (version.contains("${")){
			    		  version = "LATEST";
			    	  }
			    	  final Collection<Artifact> deps = new Aether(remotes, local).resolve(
						        new DefaultArtifact(depend.getGroupId(), depend.getArtifactId(), depend.getClassifier(), depend.getType(), version),
						        JavaScopes.SYSTEM
						      );
			    	  Iterator<Artifact> artIt = deps.iterator();
				      while (artIt.hasNext()) {
				    	  Artifact art = artIt.next();
				    	  
				    	  classpathMe.append(art.getFile().getAbsolutePath()+";");
				    	  String filePath = art.getFile().getAbsolutePath();
				        	try {
							PrintWriter out = new PrintWriter(
						    		new BufferedWriter(
						    				new FileWriter(fileRepo+"/libraries.csv", true)));
							if(depend.getScope()==null){
								out.println(filePath+"#"+art.getFile().getName()+"#"+version+"#"+pomXml+"#"+"null");
								scanPom(art.getFile().getName()+"#"+version+"#"+depend.getGroupId()+"#"+depend.getArtifactId(),fileRepo);
							}else{
							    out.println(filePath+"#"+art.getFile().getName()+"#"+version+"#"+pomXml+"#"+depend.getScope());
							    scanPom(art.getFile().getName()+"#"+version+"#"+depend.getGroupId()+"#"+depend.getArtifactId(),fileRepo);
							}
						    out.close();
					    
				        	} catch (IOException e) {
				        		//exception handling left as an exercise for the reader
				        		System.out.println(e.getMessage());
				        	}
				      }
			      } catch (DependencyResolutionException e) {
			    	//exception handling left as an exercise for the reader
		        		System.out.println(e.getMessage());
			      }
			      
			    } 
            } finally {
                reader.close();
            }
        } catch (XmlPullParserException ex) {
                throw new RuntimeException("Error parsing POM!", ex);
                
        } catch (final IOException ex) {
                throw new RuntimeException("Error reading POM!", ex);
               
		} finally {
			return classpathMe.toString();
        }
		
	    	
	  }
	

	private static void scanPom (String dirString, String baseSite){
				
		Collection <File> found = FileUtils.listFilesAndDirs
				(new File(baseSite+"/libs"), TrueFileFilter.INSTANCE, DirectoryFileFilter.DIRECTORY);
		for (File f : found) {
			if (f.getAbsolutePath().contains(".pom") && f.getAbsolutePath().contains(".pom.sha1")==false){
				if (f.isFile()){
					System.out.println(dirString+":"+f.getAbsolutePath());
					try {
						PrintWriter out = new PrintWriter(
					    		new BufferedWriter(
					    				new FileWriter(baseSite+"/listing.csv", true)));
						String path = f.getAbsolutePath().replaceAll(baseSite, ",");
					    out.println(dirString+"#"+path);
					    out.close();
					    System.out.println(path);
				    
			       } catch (IOException e) {
				    //exception handling left as an exercise for the reader
			       }
				}
			}
		}
		File dir = new File(baseSite+"/libs");
		try {
			FileUtils.deleteDirectory(dir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Delete repos: "+dir.getAbsolutePath());	
		
	}
	
}
