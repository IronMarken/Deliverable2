package logic;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;


public class WekaManager {
	
	private static final Logger LOGGER = Logger.getLogger(WekaManager.class.getName()); 
	private static final String EXT = ".arff";
	
	private String arffName;
	private String csvName;
	
	public WekaManager(String csvName) {
		this.csvName = csvName;
		this.arffName = csvName.replace(".csv", EXT);	
	}
	
	public boolean fileExists() {
		File arffFile = new File(this.arffName);
		return arffFile.exists();
	}
	
	
	public void csvToArff() throws IOException {
		
		LOGGER.log(Level.INFO, "Converting csv file into arff file");
		
		//load CSV
		CSVLoader loader = new CSVLoader();
		loader.setSource(new File(this.csvName));
		Instances data = loader.getDataSet();
	
		//create and save ARFF
		ArffSaver saver = new ArffSaver();

		saver.setInstances(data);
		saver.setFile(new File(this.arffName));
		
		saver.writeBatch();

		LOGGER.log(Level.INFO, "Csv file converted into arff file");
	
	}

}
