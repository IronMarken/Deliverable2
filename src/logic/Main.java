package logic;

import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;

public class Main {
	
	public static void main(String[] args) throws IOException, GitAPIException {
		
		String gitUrl = "https://github.com/apache/avro";
		//String gitUrl = "https://github.com/apache/bookkeeper";
		
		//Parse project name
		String[] splitted = gitUrl.split("/");
		String projName = splitted[splitted.length -1];
		
		GitBoundary gb = new GitBoundary(gitUrl);
		ReleaseNameAdapter rna = new ReleaseNameAdapter(0, "release-");
		ReleaseManager rr = new ReleaseManager(projName, gb);
		rr.setAdapter(rna);
		rr.retrieveReleases();
		
		System.out.println("RELEASED\n\n");
		System.out.printf("Index \t VersionID \t Version Name \t Git Name \t SHA \t Data \t\n");
		List<Release> releases = rr.getReleases();
		rr.retrieveClasses();
		Release rel;
		List<String> classes;
		int i,j;
		for (i = 0; i< rr.getSize(); i++){
			rel = releases.get(i);
			System.out.printf("%d \t %s \t %s \t %s \t%s \t %s \t\n", rel.getReleaseIndex(), rel.getReleaseID(), rel.getJiraName(), rel.getGitName(), rel.getSha(), rel.getReleaseDate());
		}
		
		System.out.println("Release git name \t Class name\n");
		for(i = 0; i < rr.getSize(); i++) {
			rel = releases.get(i);
			classes = rel.getClasses();
			/*if(rel.getGitName().equals( "release-1.10.0"))
				for(j = 0; j < classes.size(); j++ ) {
					System.out.printf("%s \t %s\n", rel.getGitName(),classes.get(j));
				}*/
			System.out.printf("%s \t %d\n", rel.getGitName(), classes.size());
		}
		
		
		/*
		int size = releases.size();
		
		System.out.println("\n\nUNRELEASED\n\n");
		System.out.printf("Index \t VersionID \t Version Name \t Git Name \t SHA \t\n");
		releases = rr.getUnreleased();
		for (int i = 0; i< releases.size(); i++){
			rel = releases.get(i);
			System.out.printf("%d \t %s \t %s \t\n", rel.getReleaseIndex(), rel.getReleaseID(), rel.getJiraName(), rel.getGitName(), rel.getSha());
		}

		size += releases.size();
		System.out.println("\n\nTotal size "+size);*/
	}
}
