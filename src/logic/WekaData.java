package logic;

public class WekaData {
	
	private String dataset;
	private Integer trainingRelease;
	//% data on training / total data
	private Double trainingData;
	//% defective in training
	private Double defectiveTraining;
	//% defective in testing
	private Double defectiveTesting;
	private String classifier;
	private String featureSelection;
	private String balancing;
	private String sensitivity;
	private Double truePositive;
	private Double falsePositive;
	private Double trueNegative;
	private Double falseNegative;
	private Double precision;
	private Double recall;
	private Double auc;
	private Double kappa;
	
	public void setDataset(String dataset) {
		this.dataset = dataset;
	}
	
	public String getDataset() {
		return this.dataset;
	}
	
	public Integer getTrainingRelease() {
		return trainingRelease;
	}

	public void setTrainingRelease(Integer trainingRelease) {
		this.trainingRelease = trainingRelease;
	}

	public String getTrainingData() {
		Double perc = trainingData*100;
		return perc.toString()+"%";
	}

	public void setTrainingData(Double trainingData) {
		this.trainingData = trainingData;
	}

	public String getDefectiveTraining() {
		Double perc = defectiveTraining*100;
		return perc.toString()+"%";
	}

	public void setDefectiveTraining(Double defectiveTraining) {
		this.defectiveTraining = defectiveTraining;
	}

	public String getDefectiveTesting() {
		Double perc = defectiveTesting*100;
		return perc.toString()+"%";
	}

	public void setDefectiveTesting(Double defectiveTesting) {
		this.defectiveTesting = defectiveTesting;
	}

	public String getClassifier() {
		return classifier;
	}

	public void setClassifier(String classifier) {
		this.classifier = classifier;
	}

	public String getFeatureSelection() {
		return featureSelection;
	}

	public void setFeatureSelection(String featureSelection) {
		this.featureSelection = featureSelection;
	}

	public String getBalancing() {
		return balancing;
	}

	public void setBalancing(String balancing) {
		this.balancing = balancing;
	}

	public String getSensitivity() {
		return sensitivity;
	}

	public void setSensitivity(String sensitivity) {
		this.sensitivity = sensitivity;
	}

	public Double getTruePositive() {
		return truePositive;
	}

	public void setTruePositive(Double truePositive) {
		this.truePositive = truePositive;
	}

	public Double getFalsePositive() {
		return falsePositive;
	}

	public void setFalsePositive(Double falsePositive) {
		this.falsePositive = falsePositive;
	}

	public Double getTrueNegative() {
		return trueNegative;
	}

	public void setTrueNegative(Double trueNegative) {
		this.trueNegative = trueNegative;
	}

	public void setFalseNegative(Double falseNegative) {
		this.falseNegative = falseNegative;
	}
	
	public Double getFalseNegative() {
		return this.falseNegative;
	}
	
	public void setPrecision(Double precision) {
		this.precision = precision;
	}
	
	public Double getPrecision() {
		return this.precision;
	}
	
	public void setRecall(Double recall) {
		this.recall = recall;
	}
	
	public Double getRecall() {
		return this.recall;
	}
	
	public void setAuc(Double auc) {
		this.auc = auc;
	}
	
	public Double getAUC() {
		return this.auc;
	}
	
	public void setKappa(Double kappa) {
		this.kappa = kappa;
	}
	
	public Double getKappa() {
		return this.kappa;
	}
	

}
