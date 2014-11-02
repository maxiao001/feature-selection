package com.alibaba.gongxiang.maxiao.featureselection.selector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.alibaba.gongxiang.maxiao.featureselection.model.FeatureIndexToCategoryInfoGain;

public class InformationGainSelector extends FeatureSelector {

	
	
	//feature limit
	static int featureLimit = 4;
	
	//info Gain Threshold
	static float infoGainThreshold = 100;
	
	
	//private Map<FeaturePairIndex,Float> pairToInfoGainMap = null;
	private List<FeatureIndexToCategoryInfoGain> featureToCategoryList = null;
	private float infoGain[] = null;
	private List<HashMap<Integer,Float>> featureToTypeToprobablity = null;
	public InformationGainSelector(int[][] transformedMatrix, String[] featureName) {
		super(transformedMatrix, featureName);
		//pairToInfoGainMap = new HashMap<FeaturePairIndex,Float>();
		featureToCategoryList = new ArrayList<FeatureIndexToCategoryInfoGain>(featureName.length);
		infoGain = new float[featureName.length+1];
		featureToTypeToprobablity = new ArrayList<HashMap<Integer,Float>>();
		for(int i = 0;i < this.featureName.length+1;i++){
			featureToTypeToprobablity.add(new HashMap<Integer,Float>());
		}
	}

	
	/**
	 * Su(x,y) = 2(H(x)-H(X|Y))/(H(X)+H(Y))
	 * H(x) = info Entropy
	 * H(x|y) = info entropy of x after observed Y
	 */
	@Override
	public void beginSelect() {
		
		caculateSingleFeatureInfoGain();
		for(int i = 0;i < featureName.length;i++){
			float infogain = caculateFeatureInfoGain(i,featureName.length);
			featureToCategoryList.add(new FeatureIndexToCategoryInfoGain(i, infogain));
		}
		System.out.println(featureToCategoryList.toString());
		Collections.sort(featureToCategoryList);
		
		//below is greedy search
		for(int i = 0;i < this.featureName.length;i++){
		
			int currentFeatureIndex = i;
			float SU = caculateFeatureInfoGain(currentFeatureIndex,this.featureName.length);
			boolean flag = true;
			for(int j = 0;j < this.resultFeatureList.size();j++){
				if(caculateFeatureInfoGain(resultFeatureList.get(j),currentFeatureIndex) > SU){
					flag = false;break;
				}
			}
			if(flag){
				resultFeatureList.add(currentFeatureIndex);
			}
		}
		
	}
	
	/**
	 * Su(x,y) = 2(H(x)-H(X|Y))/(H(X)+H(Y))
	 * H(x) = info Entropy
	 * H(x|y) = info entropy of x after observed Y
	 *	@return H(X|Y)
	 */
	//symmetrical  
	private float caculateFeatureInfoGain(int featureIndex,int otherIndex) {
		
		HashMap<Integer,Float> otherTypeToProb = featureToTypeToprobablity.get(otherIndex);
		
		HashMap<Integer,Float> featureTypeToProb = featureToTypeToprobablity.get(featureIndex);
		
		Set<Entry<Integer,Float>> otherSet = otherTypeToProb.entrySet();
		Set<Entry<Integer,Float>> featureSet = featureTypeToProb.entrySet();
		
		//caculate the pair count for later compute probability 
		
		Map<Integer,HashMap<Integer,Integer>> otherToFeatureCount = new HashMap<Integer,HashMap<Integer,Integer>>();
		for(Entry<Integer,Float> otherEntry : otherSet){
			HashMap<Integer,Integer> singleOtherToFeatureMap = new HashMap<Integer,Integer>();
			for(Entry<Integer,Float> featureEntry : featureSet){
				singleOtherToFeatureMap.put(featureEntry.getKey(), 0);
			}
			otherToFeatureCount.put(otherEntry.getKey(), singleOtherToFeatureMap);
		}
		
		for(int i = 0;i < this.transformedMatrix.length;i++){
			int otherToFeatureValue = otherToFeatureCount.get(transformedMatrix[i][otherIndex]).get(transformedMatrix[i][featureIndex]);
			otherToFeatureCount.get(transformedMatrix[i][otherIndex]).put(transformedMatrix[i][featureIndex], otherToFeatureValue+1);
		}
		
		float result = 0;
		float H_X_Y = 0.0f;
		for(Entry<Integer,Float> featureEntry : featureSet){
			for(Entry<Integer,Float> otherEntry : otherSet){
				float prob = (float)otherToFeatureCount.get(otherEntry.getKey()).get(featureEntry.getKey())
						/((float)featureToTypeToprobablity.get(otherIndex).get(otherEntry.getKey())*this.transformedMatrix.length);
				//System.out.println(prob);
				H_X_Y += (-1.0)*featureEntry.getValue()*(prob == 0?0 :(prob*Math.log(prob)));
			}
		}
		result = 2*(infoGain[featureIndex]-H_X_Y)/(infoGain[featureIndex]+infoGain[otherIndex]);
		return result;
	}


	private void caculateSingleFeatureInfoGain() {
		//the i th feature
		for(int i = 0;i < this.featureName.length+1;i++){
			Map<Integer,Integer> featureToCount = new HashMap<Integer,Integer>();
			//the j th instance
			for(int j = 0;j < this.transformedMatrix.length;j++){
				Integer currentCount = 	featureToCount.get(transformedMatrix[j][i]);
				if(currentCount == null){
					featureToCount.put(transformedMatrix[j][i], 1);
				}else{
					featureToCount.put(transformedMatrix[j][i], currentCount+1);
				}
			}
			float sum = 0.0f;
			//List<Float> probabilityList = new ArrayList<Float>();
			Set<Entry<Integer,Integer>> set = featureToCount.entrySet();
			for(Entry<Integer,Integer> entry : set){
				float probability = (float)entry.getValue()/(float)this.transformedMatrix.length;
			//	System.out.println(probability);
				featureToTypeToprobablity.get(i).put(entry.getKey(), probability);
				sum += (-1.0f)*(probability)*(probability==0?0:Math.log(probability));
			}
		//	System.out.println(sum);
			infoGain[i] = sum;
		}
	}
}
