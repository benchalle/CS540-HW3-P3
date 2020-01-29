import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Fill in the implementation details of the class DecisionTree using this file. Any methods or
 * secondary classes that you want are fine but we will only interact with those methods in the
 * DecisionTree framework.
 */


public class DecisionTreeImpl {
	public DecTreeNode root;
	public List<List<Integer>> trainData;
	public int maxPerLeaf;
	public int maxDepth;
	public int numAttr;

	// Build a decision tree given a training set
	DecisionTreeImpl(List<List<Integer>> trainDataSet, int mPerLeaf, int mDepth) {
		this.trainData = trainDataSet;
		this.maxPerLeaf = mPerLeaf;
		this.maxDepth = mDepth;
		if (this.trainData.size() > 0) this.numAttr = trainDataSet.get(0).size() - 1;
		this.root = buildTree(trainData, 0, new DecTreeNode(-1,-1,-1));
	}
	
	private DecTreeNode buildTree(List<List<Integer>> train, int depth, DecTreeNode parent) {
//		if empty(examples) then return default-label
//				if (examples have same label y) then return y
//				if empty(attributes) then return majority vote in examples
//				q = best_attribute(examples, attributes)
//				tree = create-node with attribute q
//				foreach value v of attribute q do
//				v-ex = subset of examples with q == v
//				subtree = buildtree(v-ex, attributes - {q}, majority-class(examples))
//				add arc from tree to subtree
//				return tree
		if(train.isEmpty()) {
			parent.classLabel = 1;
			return parent;
		}
		if(sameLabel(train)) {
			parent.classLabel = train.get(0).get(9);
			return parent;
		}
		if(train.size() < maxPerLeaf || depth == maxDepth) {
			int majority = 0;
			for(List<Integer> patient : train) {
				if(patient.get(9) == 1) {
					majority++;
				}
			}
			if(majority < ((double)train.size()/2)) {
				majority = 0;
			}else {
				majority = 1;
			}
			parent.classLabel = majority;
			return parent;
		}
		
		int[] attributeInfo = best_attribute(train);
		int attribute = attributeInfo[0];
		int threshold = attributeInfo[1];
		List<List<Integer>> leftList = new ArrayList<List<Integer>>();
		List<List<Integer>> rightList = new ArrayList<List<Integer>>();
		for(List<Integer>  patient : train) {
			if(patient.get(attribute) > threshold) {
				rightList.add(patient);
			}else {
				leftList.add(patient);
			}
		}
		
		parent.attribute = attribute;
		parent.threshold = threshold;
		parent.right = buildTree(rightList, depth+1, new DecTreeNode(-1, -1, -1));
		parent.left = buildTree(leftList, depth+1, new DecTreeNode(-1, -1, -1));
		return parent;
	}
	



	private double getEntropy(List<List<Integer>> train) {
		int classCountOne = 0;
		int classCountZero = 0;
		for(List<Integer> patient : train) {
			if(patient.get(9) == 1) {
				classCountOne++;
			}else {
				classCountZero++;
			}
		}
		int n = train.size();

		double fractionZero = (double)classCountZero/n;
		double fractionOne = (double)classCountOne/n;
		double entropy;
		if(classCountZero == 0) {
			entropy = ((-(double)classCountOne/n)*(Math.log(fractionOne)/Math.log(2)));
		}else if(classCountOne == 0) {
			entropy = ((-(double)classCountZero/n)*(Math.log(fractionZero)/Math.log(2)));
		}else {
			entropy = ((-(double)classCountZero/n)*(Math.log(fractionZero)/Math.log(2))) + ((-(double)classCountOne/n)*(Math.log(fractionOne)/Math.log(2)));
		}
		// TODO Auto-generated method stub
		return entropy;
	}

	private int[] best_attribute(List<List<Integer>> train) {
		double[][] infoGain = new double[10][10];
		for(int i = 0; i < 10; i ++) {
			getAttributeInfoGain(i, infoGain, train);
		}
		// TODO Auto-generated method stub
		//redo how maxinfo is calculated
		double maxInfo = -1;
		int att = 10;
		int thresh = 10;
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				if(infoGain[i][j] > maxInfo) {
					att = i;
					thresh = j;
					maxInfo = infoGain[i][j];
				}else if(infoGain[i][j] == maxInfo) {
					//get the attribute that appears earliest
					if(i < att) {
						att = i;
						thresh = j;
						maxInfo = infoGain[i][j];
					}else if(i == att) {
						//get the lowest threshold value
						if(j < thresh) {
							thresh = j;
							maxInfo = infoGain[i][j];
						}
					}
				}
			}
		}
		int[] response = new int[] {att, thresh};
		return response;
	}

	private void getAttributeInfoGain(int att, double[][] infoGain, List<List<Integer>> train) {
		double entropy = getEntropy(train);
		// TODO Auto-generated method stub
		for(int i = 1; i < 10; i++) {
			int lessThan = 0;
			int classCountZero = 0;
			int classCountOne = 0;
			int cc1 = 0;
			int cc0 = 0;
			int greaterThan = 0;
			for(List<Integer> patient : train) {
				if(patient.get(att) <= i) {
					lessThan++;
					if(patient.get(9) == 1) {
						classCountOne++;
					}else {
						classCountZero++;
					}
				}else {
					greaterThan++;
					if(patient.get(9) == 1) {
						cc1++;
					}else {
						cc0++;
					}
				}
			}
			if(lessThan != 0 && greaterThan != 0) {
				double fractionZero = (double)classCountZero/lessThan;
				double fractionOne = (double)classCountOne/lessThan;
				double entropy1;
				if(classCountOne == 0) {
					entropy1 = ((-(double)fractionZero)*(Math.log(fractionZero)/Math.log(2)));
				}else if(classCountZero == 0) {
					entropy1 = ((-(double)fractionOne)*(Math.log(fractionOne)/Math.log(2)));
				}else {
					entropy1 = ((-(double)fractionZero)*(Math.log(fractionZero)/Math.log(2))) + ((-(double)fractionOne)*(Math.log(fractionOne)/Math.log(2)));
				}
//may need logic to see if it is one or not
				fractionZero = (double)cc0/greaterThan;
				fractionOne = (double)cc1/greaterThan;
				double entropy2;
				if(cc1 == 0) {
					entropy2 = ((-(double)fractionZero)*(Math.log(fractionZero)/Math.log(2)));
				}else if(cc0 == 0) {
					entropy2 = ((-(double)fractionOne)*(Math.log(fractionOne)/Math.log(2)));
				}else {
					entropy2 = ((-(double)fractionZero)*(Math.log(fractionZero)/Math.log(2))) + ((-(double)fractionOne)*(Math.log(fractionOne)/Math.log(2)));
				}
				
				double conditional = ((double)lessThan/train.size())*entropy1 + ((double)greaterThan/train.size())*entropy2;
				infoGain[att][i] = entropy - conditional;
			}
			//calculate conditional entropy
			
		}
		
	}

	private boolean sameLabel(List<List<Integer>> data) {
		Iterator<List<Integer>> itr = data.iterator();
		int label = -1;
		boolean first = true;
		while(itr.hasNext()) {
			List<Integer> curr = itr.next();
			if(first) {
				first = false;
				label = curr.get(9);
				}else {
					if(label != curr.get(9)) return false;
				}
				
			}

		
		// TODO Auto-generated method stub
		return true;
	}

	public int classify(List<Integer> instance) {
		DecTreeNode start = root;
		
		
		while(!start.isLeaf()) {
			if(instance.get(start.attribute) > start.threshold) {
				start = start.right;
			}else {
				start = start.left;
			}
		}
		return start.classLabel;
		// TODO: add code here
		// Note that the last element of the array is the label.
		
	}
	
	// Print the decision tree in the specified format
	public void printTree() {
		printTreeNode("", this.root);
	}

	public void printTreeNode(String prefixStr, DecTreeNode node) {
		String printStr = prefixStr + "X_" + node.attribute;
		System.out.print(printStr + " <= " + String.format("%d", node.threshold));
		if(node.left.isLeaf()) {
			System.out.println(" : " + String.valueOf(node.left.classLabel));
		}
		else {
			System.out.println();
			printTreeNode(prefixStr + "|\t", node.left);
		}
		System.out.print(printStr + " > " + String.format("%d", node.threshold));
		if(node.right.isLeaf()) {
			System.out.println(" : " + String.valueOf(node.right.classLabel));
		}
		else {
			System.out.println();
			printTreeNode(prefixStr + "|\t", node.right);
		}
	}
	
	public double printTest(List<List<Integer>> testDataSet) {
		int numEqual = 0;
		int numTotal = 0;
		for (int i = 0; i < testDataSet.size(); i ++)
		{
			int prediction = classify(testDataSet.get(i));
			int groundTruth = testDataSet.get(i).get(testDataSet.get(i).size() - 1);
			System.out.println(prediction);
			if (groundTruth == prediction) {
				numEqual++;
			}
			numTotal++;
		}
		double accuracy = numEqual*100.0 / (double)numTotal;
		System.out.println(String.format("%.2f", accuracy) + "%");
		return accuracy;
	}
}
