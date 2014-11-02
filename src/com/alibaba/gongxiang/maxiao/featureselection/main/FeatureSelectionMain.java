package com.alibaba.gongxiang.maxiao.featureselection.main;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.alibaba.gongxiang.maxiao.datadiscretization.engine.SupervisedTool;
import com.alibaba.gongxiang.maxiao.datadiscretization.engineImp.InformationEntropyTool;
import com.alibaba.gongxiang.maxiao.datadiscretization.model.Feature2Category;
import com.alibaba.gongxiang.maxiao.featureselection.selector.FeatureSelector;
import com.alibaba.gongxiang.maxiao.featureselection.selector.InformationGainSelector;


public class FeatureSelectionMain {
	
	
	
	static String csvPath = "data/iris.data";
	static String features = "petal_width,petal_length,sepal_length,sepal_width";

	static String categoryName = "class";
	static boolean binaryCategory = false;
	
	public static void main(String[] args) throws IOException{
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(csvPath)));
		String line = reader.readLine();

		String featureArray[] = line.split(",");
		int categoryIndex = -1;
		
		String trainFeature[] = features.split(",");
		int trainFeatureIndex[] = new int[trainFeature.length];
		Arrays.fill(trainFeatureIndex, -1);
		for(int i = 0;i < featureArray.length;i++){

			for(int j = 0;j < trainFeature.length;j++){
				if(trainFeature[j].equals(featureArray[i])){
					trainFeatureIndex[j] = i;
				}
			}
			if(categoryName.equals(featureArray[i])) categoryIndex = i;
		}
		for(int i = 0;i < trainFeature.length;i++){
			if(trainFeatureIndex[i] == -1){
				System.out.println("feature not exists!");
				System.exit(0);
			}
		}
		if(categoryIndex == -1){
			System.out.println("category not exists!");
		}
		
		
		//transformed matrix ,include necessary features and category 
		int matrix[][] = transformDataIntoDiscretizedMatrix(trainFeature,trainFeatureIndex,categoryIndex,reader);
		
		FeatureSelector selector = new InformationGainSelector(matrix, trainFeature);
		
		selector.beginSelect();
		
		System.out.println(selector.getResultFeatureList().toString());
		reader.close();
	}

	private static int[][] transformDataIntoDiscretizedMatrix(
			String[] trainFeature, int[] trainFeatureIndex, int categoryIndex, BufferedReader reader) throws NumberFormatException, IOException {

		
		
		ArrayList<float[]> initList = new ArrayList<float[]>();
		String line = null;
		Set<Integer> categoryTypeSet = new HashSet<Integer>(8);
		while((line=reader.readLine())!=null){
			String valueArr[] = line.split(",");
			float featureAndClass[] = new float[trainFeature.length+1];
			for(int i = 0;i < trainFeature.length;i++){
				float feature = Float.parseFloat(valueArr[trainFeatureIndex[i]]);
				featureAndClass[i] = feature;
			}
			int categoryValue = Integer.parseInt(valueArr[categoryIndex]);
			int category = 0;
			if(binaryCategory){
				category = categoryValue > 0 ? 1:0;
			}else{
				category = categoryValue;
			}
			categoryTypeSet.add(category);
			featureAndClass[featureAndClass.length-1] = category;
			initList.add(featureAndClass);
		}
		
		//result matrix
		int transformedMatrix [][] = new int[initList.size()][trainFeature.length+1];
		
		//fill category space
		for(int i = 0;i < initList.size();i++){
			transformedMatrix[i][trainFeature.length] = (int)(initList.get(i)[trainFeature.length]);
		}
		//file feature space
		for(int i = 0;i < trainFeature.length;i++){
			List<Feature2Category> singleFeatureList = new ArrayList<Feature2Category>();
			
			for(float featureAndClass[] : initList){
				Feature2Category fc = new Feature2Category(featureAndClass[i],(int)featureAndClass[featureAndClass.length-1]);
				singleFeatureList.add(fc);
			}
			
			SupervisedTool tool = new InformationEntropyTool(singleFeatureList,categoryTypeSet);
			tool.computeCutIntervals();
			List<Float>  cutPointList = tool.getCutPointList();
			
			for(int index = 0;index < initList.size();index++){
				transformedMatrix[index][i] = getDiscretizedValue(initList.get(index)[i],cutPointList);
			}
		}
		
	/*	for(int i = 0;i < transformedMatrix.length;i++){
			for(int j = 0;j < trainFeature.length+1;j++){
				System.out.print(transformedMatrix[i][j]);
			}
			System.out.println();
		}*/
		return transformedMatrix;
		
	}

	private static int getDiscretizedValue(float f, List<Float> cutPointList) {
		
		//return 1,2,3,4...... for each feature
		
		for(int i = 0;i < cutPointList.size();i++){
			if(cutPointList.get(i) > f){
				return (i+1);
			}
		}
		return 0;
	}
}
