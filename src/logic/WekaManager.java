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
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.CostSensitiveClassifier;
import weka.classifiers.trees.RandomForest;
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
	private String projectName;
	
	public WekaManager(String csvName, String projectName) {
		this.projectName = projectName;
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
	private List<Instances> splitSets(Instances instances, int index) {
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
	
	private List<Instances> featureSelection(Instances trainingSet, Instances testingSet) throws WekaException {
		
		List<Instances> filteredList = new ArrayList<>();
		Instances filteredTraining;
		Instances filteredTesting;
		trainingSet.setClassIndex(trainingSet.numAttributes()-1);
		testingSet.setClassIndex(testingSet.numAttributes()-1);
		
		//generate AttributeSelection evaluator and search algorithm
		AttributeSelection filter = new AttributeSelection();
		
		CfsSubsetEval eval = new CfsSubsetEval();
		
		BestFirst search = new BestFirst();
		try {
			//Terminate bestFirst parameter
			search.setSearchTermination(STOP_BEST_FIRST);
		
			filter.setEvaluator(eval);
			filter.setSearch(search);
			filter.setInputFormat(trainingSet);
		
			filteredTraining = Filter.useFilter(trainingSet, filter);
			filteredTesting = Filter.useFilter(testingSet, filter);
			
		
		}catch(Exception e) {
			throw new WekaException(e);
		}
		
		filteredList.add(filteredTraining);
		filteredList.add(filteredTesting);
		return filteredList;
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
	
	private Instances undersampling(Instances instances) throws WekaException {
		SpreadSubsample spreadSubsample = new SpreadSubsample();
		Instances filtered = null;
		String[] opts = new String[]{ "-M", "1.0"};
		instances.setClassIndex(instances.numAttributes()-1);
		try {
			spreadSubsample.setOptions(opts);
			spreadSubsample.setInputFormat(instances);
			filtered = Filter.useFilter(instances, spreadSubsample);
		}catch(Exception e ) {
			throw new WekaException(e);
		}
		return filtered;
		
	}
	
	private Instances oversampling(Instances instances) throws WekaException {
		Resample resample = new Resample();
		Instances filtered = null;
		instances.setClassIndex(instances.numAttributes()-1);
		resample.setNoReplacement(false);
		resample.setBiasToUniformClass(1.0);
		resample.setSampleSizePercent(getSampleSizePercent(instances));
		try {
			resample.setInputFormat(instances);
			filtered = Filter.useFilter(instances, resample);
		}catch(Exception e) {
			throw new WekaException(e);
		}
		return filtered;
		
	}
	
	private Instances smote(Instances instances) throws WekaException {
		SMOTE smote = new SMOTE();
		Instances filtered = null;
		smote.setPercentage(getSampleSizePercent(instances));
		instances.setClassIndex(instances.numAttributes()-1);
		try {
			smote.setInputFormat(instances);
			filtered = Filter.useFilter(instances, smote);
		}catch(Exception e) {
			throw new WekaException(e);
		}		
		return filtered;
	}
	
	
	private CostMatrix createCostMatrix(double costFalsePositive, double costFalseNegative) {
		CostMatrix costMatrix = new CostMatrix(2);
		costMatrix.setCell(0, 0, 0.0);
		costMatrix.setCell(1, 0, costFalsePositive);
		costMatrix.setCell(0, 1, costFalseNegative);
		costMatrix.setCell(1, 1, 0.0);
		
		return costMatrix;		
	}
	
	private CostSensitiveClassifier sensitiveClassifier(Classifier classifier, Instances trainingSet, boolean threshold)throws WekaException {
		
		CostSensitiveClassifier costClassifier = new CostSensitiveClassifier();
		costClassifier.setClassifier(classifier);
		costClassifier.setCostMatrix(createCostMatrix(CFP, 10*CFP));
		trainingSet.setClassIndex(trainingSet.numAttributes()-1);
		//adjust threshold
		costClassifier.setMinimizeExpectedCost(threshold);
		try {
			costClassifier.buildClassifier(trainingSet);	
		}catch(Exception e) {
			throw new WekaException(e);
		}
		
		return costClassifier;
	}
	
	private List<Instances> applyFeatureSelection(String sel, Instances trainingSet, Instances testingSet) throws WekaException{
		
		Instances filteredTraining;
		Instances filteredTesting;
		List<Instances> filteredData = new ArrayList<>();
		
		switch(sel) {
			case "no_feature_selection":
				filteredTraining = trainingSet;
				filteredTesting = testingSet;
				filteredData.add(filteredTraining);
				filteredData.add(filteredTesting);
				break;
			case "BestFirst":
				filteredData = featureSelection(trainingSet,testingSet);
				break;
			default:
				throw new IllegalArgumentException("Invalid feature selection");
		}
		return filteredData;
	}

	
	private Instances applySampling(String sampling, Instances trainingSet) throws WekaException{
		
		Instances sampledTraining = null;
		
		switch(sampling) {
			case "no_sampling":
				sampledTraining = trainingSet;
				break;
			case "Oversampling":
				sampledTraining = oversampling(trainingSet);
				break;
			case "Undersampling":
				sampledTraining = undersampling(trainingSet);
				break;
			case "SMOTE":
				sampledTraining = smote(trainingSet);
				break;
			default:
				throw new IllegalArgumentException("Invalid sampling");
		}
		
		return sampledTraining;
	}
	
	
	private CostSensitiveClassifier applyCostSensitive(String cost,Classifier classifier, Instances trainingSet) throws WekaException{
		
		//check if is null use directly classifier
		CostSensitiveClassifier costSensitive = null;
		
		switch(cost) {
			case "no_cost":
				break;
			case "Sensitive_Threshold":
				costSensitive = sensitiveClassifier(classifier, trainingSet, true);
				break;
			case "Sensitive_Learning":
				costSensitive= sensitiveClassifier(classifier, trainingSet, false);
				break;
			default:
				throw new IllegalArgumentException("Invalid cost");
		}
		
		return costSensitive;
		
	}
	
	private Classifier getClassifier(String name, Instances trainingSet) throws WekaException{
		
		Classifier classifier = null;
		trainingSet.setClassIndex(trainingSet.numAttributes()-1);
		
		switch(name){
		case "RandomForest":
			classifier = new RandomForest();
			break;
		case "NaiveBayes":
			classifier = new NaiveBayes(); 
			break;
		case "IBk":
			classifier= new IBk(); 
			break;
		default:
			throw new IllegalArgumentException("Invalid classifier");
	}
		try {
			classifier.buildClassifier(trainingSet);	
		}catch(Exception e) {
			throw new WekaException(e);
		}
		return classifier;
		
	}
	


	
	/****************************
	*firstCol:
	*	0-dataset
	*	1-#Releases in training
	****************************/
	private List<WekaData> walkingForwardStep(List<String> firstCol, Instances trainingSet, Instances testingSet, List<String> classifierList, List<String> featureSelectionList, List<String> samplingList, List<String> costList) throws WekaException {
		
		List<Instances> filteredData; 
		Instances filteredTraining;
		Instances filteredTesting;
		Instances sampledTraining;
		Classifier classif;
		CostSensitiveClassifier costSensitiveClass;
		String report;
		
		int count = 0;
		
		WekaData wekaData;
		List<WekaData> stepData = new ArrayList<>();
		
		//feature selection
		for(String featureSelection:featureSelectionList) {
			firstCol.add(featureSelection);
			filteredData = applyFeatureSelection(featureSelection, trainingSet, testingSet);
			filteredTraining = filteredData.get(0);
			filteredTesting = filteredData.get(1);
			
			//sampling
			for(String balancing: samplingList) {
				firstCol.add(balancing);
				sampledTraining = applySampling(balancing, filteredTraining);
				
				//classifiers
				for(String classifier:classifierList) {
					firstCol.add(classifier);
					classif = getClassifier(classifier,sampledTraining);
					
					//costSensitive
					for(String sensitivity:costList) {
						count ++;
						firstCol.add(sensitivity);
						costSensitiveClass = applyCostSensitive(sensitivity, classif, sampledTraining);
						sampledTraining.setClassIndex(sampledTraining.numAttributes()-1);
						filteredTesting.setClassIndex(filteredTesting.numAttributes()-1);
						wekaData = evaluateClass(firstCol, sampledTraining, filteredTesting, classif, costSensitiveClass);
						stepData.add(wekaData);
					}
				}
			}
		}
		report = count+" combination tried";
		LOGGER.log(Level.INFO, report);
		return stepData;
	}
	
	
	/****************************
	*cols:
	*	0-dataset
	*	1-#Releases in training
	*	2-featureSelection
	*	3-balancing
	*	4-classifier
	*	5-sensitivity
	****************************/
	private WekaData evaluateClass(List<String> cols, Instances trainingSet, Instances testingSet, Classifier classifier, CostSensitiveClassifier costSensitiveClassifier) throws WekaException{
		
		WekaData data = new WekaData();
		Evaluation eval;
		Double trainingData;
		Double defectiveTraining;
		Double defectiveTesting;
		
		
		trainingSet.setClassIndex(trainingSet.numAttributes()-1);
		testingSet.setClassIndex(testingSet.numAttributes()-1);
		
		try {
		
			//check if cost sensitive is applied
			if(costSensitiveClassifier == null) {
				//no cost sensitive
				eval = new Evaluation(trainingSet);
				eval.evaluateModel(classifier, testingSet);
			}else {
				//cost sensitive
				eval = new Evaluation(trainingSet, costSensitiveClassifier.getCostMatrix());
				eval.evaluateModel(costSensitiveClassifier, testingSet);
			}
		}
		catch(Exception e) {
			throw new WekaException(e);
		}
		
		//set previous attributes
		data.setDataset(cols.get(0));
		data.setTrainingRelease(Integer.parseInt(cols.get(1)));
		data.setFeatureSelection(cols.get(2));
		data.setBalancing(cols.get(3));
		data.setClassifier(cols.get(4));
		data.setSensitivity(cols.get(5));
		
		//set training and testing data
        trainingData = (double) trainingSet.size()/ (trainingSet.size()+testingSet.size());
        defectiveTraining = countBuggyInstances(trainingSet)/trainingData;
        defectiveTesting = countBuggyInstances(testingSet)/(double)testingSet.size();
		
        data.setTrainingData(trainingData);
        data.setDefectiveTraining(defectiveTraining);
        data.setDefectiveTesting(defectiveTesting);
        
        //set evaluation data
        data.setTruePositive(eval.numTruePositives(0));
        data.setFalsePositive(eval.numFalsePositives(0));
        data.setTrueNegative(eval.numTrueNegatives(0));
        data.setFalseNegative(eval.numFalseNegatives(0));
        
        data.setPrecision(eval.precision(0));
        data.setRecall(eval.recall(0));
        data.setAuc(eval.areaUnderROC(0));
        data.setKappa(eval.kappa());
		
		
		return data;
		
	}


	public List<List<WekaData>> walkForward(List<String> classifierList, List<String> featureSelectionList, List<String> samplingList, List<String> costList ) throws WekaException{
		
		List<Instances> setsList;
		Instances trainingSet;
		Instances testingSet;
		String report;
		
		String dataset;
		Integer trainingRelease;
		List<String> col = new ArrayList<>();
		//total data divided for step
		List<List<WekaData>> totalData = new ArrayList<>();
		List<WekaData> stepData;
		
		int numReleases;
		Integer i;
		

		
		try {
			DataSource source = new DataSource(this.arffName);
			Instances data = source.getDataSet();
			numReleases = data.attribute(0).numValues();
			
			//skip first step with null training set
			for(i=1; i<numReleases; i++) {
				setsList = this.splitSets(data, i);
				trainingSet = setsList.get(0);
				testingSet = setsList.get(1);
				
				trainingSet.setClassIndex(trainingSet.numAttributes()-1);
				testingSet.setClassIndex(testingSet.numAttributes()-1);

				
				//set dataset name and #trainingRelease
				dataset = this.projectName+"-step_"+i;
				trainingRelease = i;				
				
				col.add(dataset);
				col.add(trainingRelease.toString());
				
				report = "Step "+i;
				LOGGER.log(Level.INFO, report);
				
				stepData = walkingForwardStep(col, trainingSet, testingSet,classifierList,featureSelectionList, samplingList,costList);
				totalData.add(stepData);
			}
		}catch(Exception e) {
			throw new WekaException(e);
		}
		return totalData;
	}	


}
