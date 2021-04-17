package logic;

import java.io.IOException;

import org.json.JSONArray;

public class JiraBoundary {
	
	private JiraBoundary() {}

	public static JSONArray getReleases(String projectName) throws IOException {
		String url = "https://issues.apache.org/jira/rest/api/2/project/" + projectName.toUpperCase() + "/versions";
		return JSONManager.readJsonArrayFromUrl(url);
	}
	
}
