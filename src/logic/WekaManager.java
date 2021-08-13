package logic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils.DataSource;
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
	
	//return 0 trainingSet
	//return 1 testingSet
	public List<Instances> splitSets(Instances instances, int index) {
		//from release 1 to index as training set
		//release index+1 as testing set
		List<Instances> splittedLists = new ArrayList<>();
		
		//generate empty instances but with same header
		Instances trainingSet = new Instances(instances, 0);
		Instances testingSet = new Instances(instances,0);
		Integer i;
		
		
		//training set
		for(i=1; i<=index; i++) {
			Integer releaseNumber = i;
			instances.parallelStream().filter(instance -> instance.stringValue(0).equals(releaseNumber.toString())).forEachOrdered(trainingSet::add);
		}
		
		splittedLists.add(trainingSet);
		
		
		//testing set
		final Integer testIndex;
		testIndex = i;
		
		instances.parallelStream().filter(instance -> instance.stringValue(0).equals(testIndex.toString())).forEachOrdered(testingSet::add);
		splittedLists.add(testingSet);
		
		return splittedLists;
		
	}
	
	
	//TODO resolve smell for Exception when complete the method
	//params filter and selections(?)
	public void walkForward() throws Exception{
		
		List<Instances> setsList;
		Instances trainingSet;
		Instances testingSet;
		
		int numReleases;
		int i;
		
		DataSource source = new DataSource(this.arffName);
		Instances data = source.getDataSet();
		
		numReleases = data.attribute(0).numValues();
		
		//skipping first step with null training set
		for(i=1; i<numReleases; i++ ) {
			setsList = this.splitSets(data, i);
			trainingSet = setsList.get(0);
			testingSet = setsList.get(1);
			
		}
		
	}
	


}
