package logic;

import java.io.IOException;
import java.util.List;

public class Main {
	
	public static void main(String[] args) throws IOException {
		String projName = "qpid";
		ReleaseRetriever rr = new ReleaseRetriever(projName);
		rr.retrieveReleases();
		System.out.printf("Index \t VersionID \t Version Name \t Date \t\n");
		List<Release> releases = rr.getReleases();
		Release rel;
		for (int i = 0; i< rr.getSize(); i++){
			rel = releases.get(i);
			System.out.printf("%d \t %s \t %s \t %s  \t\n", rel.getReleaseIndex(), rel.getReleaseID(), rel.getAllReleaseName(), rel.getReleaseDate().toString());
		}		
	}
}
