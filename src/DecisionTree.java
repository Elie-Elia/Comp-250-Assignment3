//Name: Elie Elia
//Student ID: 260759306

import java.io.Serializable;
import java.util.ArrayList;
import java.text.*;
import java.lang.Math;

public class DecisionTree implements Serializable {

	DTNode rootDTNode;
	int minSizeDatalist; //minimum number of datapoints that should be present in the dataset so as to initiate a split
	//Mention the serialVersionUID explicitly in order to avoid getting errors while deserializing.
	public static final long serialVersionUID = 343L;
	public DecisionTree(ArrayList<Datum> datalist , int min) {
		minSizeDatalist = min;
		rootDTNode = (new DTNode()).fillDTNode(datalist);
	}

	class DTNode implements Serializable{
		//Mention the serialVersionUID explicitly in order to avoid getting errors while deserializing.
		public static final long serialVersionUID = 438L;
		boolean leaf; //indicates whether the node is a leaf or not
		int label = -1;      // only defined if node is a leaf
		int attribute; // only defined if node is not a leaf
		double threshold;  // only defined if node is not a leaf



		DTNode left, right; //the left and right child of a particular node. (null if leaf)

		DTNode() {
			leaf = true;
			threshold = Double.MAX_VALUE;
		}



		// this method takes in a datalist (ArrayList of type datum) and a minSizeInClassification (int) and returns
		// the calling DTNode object as the root of a decision tree trained using the datapoints present in the
		// datalist variable
		// Also, KEEP IN MIND that the left and right child of the node correspond to "less than" and "greater than or equal to" threshold
		DTNode fillDTNode(ArrayList<Datum> datalist) {
			DTNode newnode = new DTNode();
			if (datalist.size()>=minSizeDatalist) {
				int datumy = datalist.get(0).y;
				boolean labelEqual = true;
				for (Datum datum :datalist) {
					if (datum.y != datumy) {
						labelEqual =false;
						break;
					}
				}
				if (labelEqual==true) {
					DTNode labelnode = new DTNode();
					labelnode.leaf=true;
					labelnode.label=datumy;
					return labelnode;

				}else {


					double best_avg_entropy=Double.MAX_VALUE;
					int best_attr=-1;
					double best_threshold=-1;

					ArrayList<Datum> split1= new ArrayList<Datum>();
					ArrayList<Datum> split2= new ArrayList<Datum>();
					for (int j=0;j<=1;j++) {
						for (Datum dat:datalist) {
							split1.clear();
							split2.clear();

							double thisthreshold =dat.x[j];
							for (Datum datumx:datalist) {
								if (datumx.x[j]<thisthreshold){
									split1.add(datumx);
								}else {
									split2.add(datumx);
								}
							}
							int sizeofdatalist=datalist.size();
							int size1=split1.size();
							int size2=split2.size();
							double calc1 = ((double)size1)/((double)sizeofdatalist);
							double calc2 = ((double)size2)/((double)sizeofdatalist);

							double current_avg_entropy = (calc1 * calcEntropy(split1))+(calc2 * calcEntropy(split2));

							if (best_avg_entropy > current_avg_entropy) {
								best_avg_entropy=current_avg_entropy;
								best_attr=j;
								best_threshold=thisthreshold;
							}


						}
					}
					
					newnode.attribute=best_attr;
					newnode.leaf=false;
					newnode.threshold=best_threshold;
					ArrayList<Datum> data1 = new ArrayList<Datum>();
					ArrayList<Datum> data2 = new ArrayList<Datum>();
					for (Datum dat:datalist) {
						if (dat.x[best_attr]<best_threshold) {
							data1.add(dat);
						} else {
							data2.add(dat);
						}
					}
					newnode.left = fillDTNode(data1);
					newnode.right = fillDTNode(data2);
				}
			}
			else {

				newnode.leaf = true;
				newnode.label = findMajority(datalist);
			}
			return newnode;

		}



		//This is a helper method. Given a datalist, this method returns the label that has the most
		// occurences. In case of a tie it returns the label with the smallest value (numerically) involved in the tie.
		int findMajority(ArrayList<Datum> datalist)
		{
			int l = datalist.get(0).x.length;
			int [] votes = new int[l];

			//loop through the data and count the occurrences of datapoints of each label
			for (Datum data : datalist)
			{
				votes[data.y]+=1;
			}
			int max = -1;
			int max_index = -1;
			//find the label with the max occurrences
			for (int i = 0 ; i < l ;i++)
			{
				if (max<votes[i])
				{
					max = votes[i];
					max_index = i;
				}
			}
			return max_index;
		}




		// This method takes in a datapoint (excluding the label) in the form of an array of type double (Datum.x) and
		// returns its corresponding label, as determined by the decision tree
		int classifyAtNode(double[] xQuery) {
			boolean leaf = this.leaf;
			if (leaf == true) {
				return this.label;
			} else {
				if(xQuery[this.attribute]<this.threshold) {
					return (this.left).classifyAtNode(xQuery);
				} else {
					return (this.right).classifyAtNode(xQuery);
				}}}
		public void preorder(DTNode dt2) {
			if (leaf==true) {
				return;}}
		
		
		//given another DTNode object, this method checks if the tree rooted at the calling DTNode is equal to the tree rooted
		//at DTNode object passed as the parameter
		public boolean equals(Object dt2)
		{
			DTNode input = (DTNode) dt2;
			if (input == null)
				return false;

			if (leaf == true) {
				if (input.leaf == false)
					return false;
				else if (input.label != label) {
					return false;
				} else
					return true;
			} else {
				if (input.leaf)
					return false;
				if (attribute != input.attribute || threshold != input.threshold)
					return false;
			}

			boolean leftMatch = false;
			boolean rightMatch = false;

			if (left == null) {
				if (input.left == null)
					leftMatch = true;
				else
					return false;
			} else {
				leftMatch = left.equals(input.left);
			}

			if (right == null) {
				if (input.right == null)
					rightMatch = true;
				else
					return false;
			} else {
				rightMatch = right.equals(input.right);
			}

			return rightMatch && leftMatch;
		}
	}


	//Given a dataset, this retuns the entropy of the dataset
	double calcEntropy(ArrayList<Datum> datalist)
	{
		double entropy = 0;
		double px = 0;
		float [] counter= new float[2];
		if (datalist.size()==0)
			return 0;
		double num0 = 0.00000001,num1 = 0.000000001;

		//calculates the number of points belonging to each of the labels
		for (Datum d : datalist)
		{
			counter[d.y]+=1;
		}
		//calculates the entropy using the formula specified in the document
		for (int i = 0 ; i< counter.length ; i++)
		{
			if (counter[i]>0)
			{
				px = counter[i]/datalist.size();
				entropy -= (px*Math.log(px)/Math.log(2));
			}
		}

		return entropy;
	}


	// given a datapoint (without the label) calls the DTNode.classifyAtNode() on the rootnode of the calling DecisionTree object
	int classify(double[] xQuery ) {
		DTNode node = this.rootDTNode;
		return node.classifyAtNode( xQuery );
	}

	// Checks the performance of a DecisionTree on a dataset
	//  This method is provided in case you would like to compare your
	//results with the reference values provided in the PDF in the Data
	//section of the PDF

	String checkPerformance( ArrayList<Datum> datalist)
	{
		DecimalFormat df = new DecimalFormat("0.000");
		float total = datalist.size();
		float count = 0;

		for (int s = 0 ; s < datalist.size() ; s++) {
			double[] x = datalist.get(s).x;
			int result = datalist.get(s).y;
			if (classify(x) != result) {
				count = count + 1;
			}
		}

		return df.format((count/total));
	}


	//Given two DecisionTree objects, this method checks if both the trees are equal by
	//calling onto the DTNode.equals() method
	public static boolean equals(DecisionTree dt1,  DecisionTree dt2)
	{
		boolean flag = true;
		flag = dt1.rootDTNode.equals(dt2.rootDTNode);
		return flag;
	}

}
