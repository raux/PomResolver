# PomResolver

Using the aether jcabi tool, we simulate the Maven dependency tool to extract all transitive dependencies of a library dependency defined in the pom.xml.

Operation:

1. Place pom.xml in the output folder.
2. Run execute using getDeps as the main class.
3. Ouput is in the output/libs folder. libraries.cvs shows direct dependencies, listing shows all the transitive dependencies.

Format for dependency is libraries.csv --> filepath+"#"+Name+"#"+version+"#"+pomfilepath+"#"+scope
                          listing.csv --> dependencyFile+"#"+Name+"#"+version+"#"+GroupId+"#"+getArtifactId()
