package logic;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;


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
		Instances newData = null;
		
		//Modify release index from numeric to nominal
		NumericToNominal convert = new NumericToNominal();
	    //set range
		String[] options= new String[2];
	    options[0]="-R";
	    options[1]="1-2";  
		
	    try {
	    	convert.setOptions(options);
	    	convert.setInputFormat(data);
	    
	    	newData=Filter.useFilter(data, convert);
	    }catch (Exception e) {
	    	throw new IOException();
	    }
		

		
		//create and save ARFF
		ArffSaver saver = new ArffSaver();

		saver.setInstances(newData);
		saver.setFile(new File(this.arffName));
		
		saver.writeBatch();

		LOGGER.log(Level.INFO, "Csv file converted into arff file");
	
	}
	
	
	
	

}
