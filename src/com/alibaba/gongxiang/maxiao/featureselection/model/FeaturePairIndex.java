package com.alibaba.gongxiang.maxiao.featureselection.model;

public class FeaturePairIndex {
	
	private int left;
	private int right;
	
	
	public FeaturePairIndex(int left, int right) {
		super();
		this.left = left;
		this.right = right;
	}
	public int getLeft() {
		return left;
	}
	public void setLeft(int left) {
		this.left = left;
	}
	public int getRight() {
		return right;
	}
	public void setRight(int right) {
		this.right = right;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + left;
		result = prime * result + right;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FeaturePairIndex other = (FeaturePairIndex) obj;
		if (left != other.left)
			return false;
		if (right != other.right)
			return false;
		return true;
	}
	
	
}
