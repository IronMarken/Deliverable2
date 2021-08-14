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
	private Long truePositive;
	private Long falsePositive;
	private Long trueNegative;
	private Long falseNegative;
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

	public Long getTruePositive() {
		return truePositive;
	}

	public void setTruePositive(Long truePositive) {
		this.truePositive = truePositive;
	}

	public Long getFalsePositive() {
		return falsePositive;
	}

	public void setFalsePositive(Long falsePositive) {
		this.falsePositive = falsePositive;
	}

	public Long getTrueNegative() {
		return trueNegative;
	}

	public void setTrueNegative(Long trueNegative) {
		this.trueNegative = trueNegative;
	}

	public void setFalseNegative(long falseNegative) {
		this.falseNegative = falseNegative;
	}
	
	public Long getFalseNegative() {
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
