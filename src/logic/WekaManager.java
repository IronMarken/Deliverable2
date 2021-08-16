package logic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.classifiers.Classifier;
import weka.classifiers.CostMatrix;
import weka.classifiers.meta.CostSensitiveClassifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.supervised.instance.Resample;
import weka.filters.supervised.instance.SMOTE;
import weka.filters.supervised.instance.SpreadSubsample;
import weka.filters.unsupervised.attribute.NumericToNominal;


public class WekaManager {
	
	private static final Logger LOGGER = Logger.getLogger(WekaManager.class.getName()); 
	private static final String EXT = ".arff";
	private static final int STOP_BEST_FIRST = 4;
	private static final double CFP = 1.0;
	
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
	
	
	public void csvToArff() throws WekaException, IOException {
		
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
	    	throw new WekaException(e);
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
	
	public Instances featureSelection(Instances instances) throws WekaException {
		Instances filteredInstances;
		
		//generate AttributeSelection evaluator and search algorithm
		AttributeSelection filter = new AttributeSelection();
		
		CfsSubsetEval eval = new CfsSubsetEval();
		
		BestFirst search = new BestFirst();
		try {
			//Terminate bestFirst parameter
			search.setSearchTermination(STOP_BEST_FIRST);
		
			filter.setEvaluator(eval);
			filter.setSearch(search);
			filter.setInputFormat(instances);
		
			filteredInstances = Filter.useFilter(instances, filter); 
		}catch(Exception e) {
			throw new WekaException(e);
		}
		
		return filteredInstances;
	}
	

	private double getSampleSizePercent(Instances instances) {

		int buggy;
		int size;
		int notBuggy;
		double majority;
		double minority;
		
		size = instances.size();
		buggy = countBuggyInstances(instances);
		notBuggy = size - buggy;
		
		if(buggy > notBuggy) {
			majority = buggy;
			minority = notBuggy;
		}else {
			minority = buggy;
			majority = notBuggy;
		}
		
		//check 0 on minority
		if(minority == 0) {
			return 0;
		}
		else
			return 100*(majority-minority)/minority;
		
	}
	
	
	private int countBuggyInstances(Instances data) {
		int counter;
		counter = 0;
		for(Instance instance: data){
			//get last attribute (Buggy)
			//from arff yes=1 no=0
		    counter += (int)instance.value(data.numAttributes()-1) == 1 ? 1 : 0;
		}
		
		return counter;
	}
	
	public Filter undersamplingFilter(Instances instances) throws WekaException {
		SpreadSubsample spreadSubsample = new SpreadSubsample();
		String[] opts = new String[]{ "-M", "1.0"};
		try {
			spreadSubsample.setOptions(opts);
			spreadSubsample.setInputFormat(instances);
		}catch(Exception e ) {
			throw new WekaException(e);
		}
		return spreadSubsample;
		
	}
	
	public Filter oversamplingFilter(Instances instances) throws WekaException {
		Resample resample = new Resample();
		resample.setNoReplacement(false);
		resample.setBiasToUniformClass(1.0);
		//obtain majority = minority
		resample.setSampleSizePercent(getSampleSizePercent(instances));
		try {
			resample.setInputFormat(instances);
		}catch(Exception e) {
			throw new WekaException(e);
		}
		return resample;
		
	}
	
	public Filter smoteFilter(Instances instances) throws WekaException {
		SMOTE smote = new SMOTE();
		//how many minority instances needed to obtain minority=majority
		smote.setPercentage(getSampleSizePercent(instances));
		try {
			smote.setInputFormat(instances);
		}catch(Exception e) {
			throw new WekaException(e);
		}
		
		return smote;
	}
	
	
	private CostMatrix createCostMatrix(double costFalsePositive, double costFalseNegative) {
		CostMatrix costMatrix = new CostMatrix(2);
		costMatrix.setCell(0, 0, 0.0);
		costMatrix.setCell(1, 0, costFalsePositive);
		costMatrix.setCell(0, 1, costFalseNegative);
		costMatrix.setCell(1, 1, 0.0);
		
		return costMatrix;		
	}
	
	public CostSensitiveClassifier sensitiveClassifier(Classifier classifier, Instances trainingSet, boolean threshold)throws WekaException {
		
		CostSensitiveClassifier costClassifier = new CostSensitiveClassifier();
		costClassifier.setClassifier(classifier);
		costClassifier.setCostMatrix(createCostMatrix(CFP, 10*CFP));
		//adjust threshold
		costClassifier.setMinimizeExpectedCost(threshold);
		try {
			costClassifier.buildClassifier(trainingSet);	
		}catch(Exception e) {
			throw new WekaException(e);
		}
		
		return costClassifier;
	}


	public void walkForward() throws WekaException{
		
		List<Instances> setsList;
		Instances trainingSet;
		Instances testingSet;
		
		int numReleases;
		int i;
		
		try {
			DataSource source = new DataSource(this.arffName);
			Instances data = source.getDataSet();
			numReleases = data.attribute(0).numValues();
			
			//skipping first step with null training set
			for(i=1; i<numReleases; i++ ) {
				setsList = this.splitSets(data, i);
				trainingSet = setsList.get(0);
				testingSet = setsList.get(1);
				
				//set prediction attribute
				trainingSet.setClassIndex(trainingSet.numAttributes()-1);
				testingSet.setClassIndex(testingSet.numAttributes()-1);
			}
		}catch(Exception e) {
			throw new WekaException(e);
		}
		
		

		
	}
	


}
