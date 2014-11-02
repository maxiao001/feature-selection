package com.alibaba.gongxiang.maxiao.featureselection.selector;

import java.util.ArrayList;
import java.util.List;

public abstract class FeatureSelector {
	
	protected int transformedMatrix[][];
	protected String featureName[];
	
	
	protected List<Integer> resultFeatureList;
	
	public FeatureSelector(int[][] transforedMatrix, String[] featureName) {
		super();
		this.transformedMatrix = transforedMatrix;
		this.featureName = featureName;
		
		resultFeatureList = new ArrayList<Integer>();
	}
	
	
	public abstract void beginSelect();
	
	public int[][] getTransforedMatrix() {
		return transformedMatrix;
	}
	public void setTransforedMatrix(int[][] transforedMatrix) {
		this.transformedMatrix = transforedMatrix;
	}
	public String[] getFeatureName() {
		return featureName;
	}
	public void setFeatureName(String[] featureName) {
		this.featureName = featureName;
	}


	public List<Integer> getResultFeatureList() {
		return resultFeatureList;
	}


	public void setResultFeatureList(List<Integer> resultFeatureList) {
		this.resultFeatureList = resultFeatureList;
	}
	
			
}
