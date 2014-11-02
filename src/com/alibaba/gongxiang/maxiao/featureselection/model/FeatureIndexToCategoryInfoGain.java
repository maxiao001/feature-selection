package com.alibaba.gongxiang.maxiao.featureselection.model;

public class FeatureIndexToCategoryInfoGain implements Comparable<FeatureIndexToCategoryInfoGain>{
	
	private int featureIndex;
	private float infoGain;
	public int getFeatureIndex() {
		return featureIndex;
	}
	public void setFeatureIndex(int featureIndex) {
		this.featureIndex = featureIndex;
	}
	public float getInfoGain() {
		return infoGain;
	}
	public void setInfoGain(float infoGain) {
		this.infoGain = infoGain;
	}
	public FeatureIndexToCategoryInfoGain(int featureIndex, float infoGain) {
		super();
		this.featureIndex = featureIndex;
		this.infoGain = infoGain;
	}
	
	@Override
	public int compareTo(FeatureIndexToCategoryInfoGain o) {
		
		return this.infoGain < o.infoGain ? 1 : this.infoGain==o.infoGain?0:-1;
	}
	@Override
	public String toString() {
		return "FeatureIndexToCategoryInfoGain [featureIndex=" + featureIndex
				+ ", infoGain=" + infoGain + "]";
	}
	
	
	
}
