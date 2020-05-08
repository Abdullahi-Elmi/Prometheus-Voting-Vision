package processing;

import java.awt.image.*;
import java.util.Arrays;
//import sun.reflect.generics.reflectiveObjects.NotImplementedException;
//This isn't really meant for public usage (internal class in java, that can change b/w updates)

/**
 * Performs SRM merging on the image given in the constructor
 * 
 * From: http://www.lix.polytechnique.fr/~nielsen/Srmjava.java
 *
 */
public class SRMerger {

	private int[] raster;
	private int[] rastero;
	private int w,h,n;
	private double aspectratio;

	private double Q;
	private UnionFind UF;
	private double g; // number of levels in a color channel
	private double logdelta;

	// Auxiliary buffers for union-find operations
	private int []N;
	private double []Ravg;
	private double [] Gavg;
	private double [] Bavg;
	private int [] C; // the class number

	// number of pixels that define a "small region" to be collapsed
	private int smallregion;
	private int borderthickness;

	/**
	 * Initialize the raster with the pixel information of img
	 * @param img
	 */
	public SRMerger(BufferedImage img) {
		int height = img.getHeight();
		int width = img.getWidth();
		int[] inputArray = new int[height * width];
		if (img.getType() == BufferedImage.TYPE_INT_ARGB || img.getType() == BufferedImage.TYPE_INT_RGB) 
			img.getRaster().getDataElements(0, 0, width, height, inputArray);
		else
			img.getRGB(0, 0, width, height, inputArray, 0, width);
		Q = 32;
		g = 256.0;
		borderthickness=0;
		this.raster = inputArray.clone();
		w=width;
		h=height;
		aspectratio=(double)h/(double)w;
		n=w*h;
		logdelta = 2.0*Math.log(6.0*n);
		// small regions are less than 0.1% of image pixels
		smallregion=(int)(0.001*n);
		OneRound();
	}

	/**
	 * Initialize the raster with the given pixel information
	 * @param img
	 */
	public SRMerger(int[] img, int width, int height, int q) {
		Q = q;
		g = 256.0;
		borderthickness=0;
		this.raster = img.clone();
		w=width;
		h=height;
		aspectratio=(double)h/(double)w;
		n=w*h;
		logdelta = 2.0*Math.log(6.0*n);
		// small regions are less than 0.1% of image pixels
		smallregion=(int)(0.001*n);
		OneRound();
	}
	
	public int[] getImage() {
		return this.raster;
	}
	
	public String getString() {
		return Arrays.toString(this.raster);
	}

	public int[] getSegmentedImage() {
		return this.rastero.clone();
	}

	void OneRound()
	{
		UF=new UnionFind(n);
		Ravg=new double[n];
		Gavg=new double[n];
		Bavg=new double[n];
		N=new int[n];
		C=new int[n];
		rastero=new int[n];

		InitializeSegmentation();
		FullSegmentation();

	}


	void InitializeSegmentation()
	{
		//
		// Initialization
		//
		int x,y,red,green,blue,index;
		for (y = 0; y < h; y++) {
			for (x = 0; x < w ; x++) {
				index=y*w+x;

				red = (raster[y * w+ x] & 0xFF) ;
				green = ((raster[y *w+ x] & 0xFF00) >> 8);
				blue = ((raster[y *w + x] & 0xFF0000) >> 16);

				Ravg[index]=red;
				Gavg[index]=green;
				Bavg[index]=blue;
				N[index]=1;
				C[index]=index;
			}
		}
	}

	void FullSegmentation()
	{
		Segmentation();
		MergeSmallRegion();
		OutputSegmentation();
		DrawBorder();
	}

	double min(double a, double b)
	{
		if (a<b) return a; else return b;
	}

	double max(double a, double b)
	{
		if (a>b) return a; else return b;
	}

	double max3(double a,double b,double c)
	{
		return max(a,max(b,c));
	}


	boolean MergePredicate(int reg1, int reg2)
	{
		double dR, dG, dB;
		double logreg1, logreg2;
		double dev1, dev2, dev;

		dR=(Ravg[reg1]-Ravg[reg2]);
		dR*=dR;

		dG=(Gavg[reg1]-Gavg[reg2]);
		dG*=dG;

		dB=(Bavg[reg1]-Bavg[reg2]);
		dB*=dB;

		logreg1 = min(g,N[reg1])*Math.log(1.0+N[reg1]);
		logreg2 = min(g,N[reg2])*Math.log(1.0+N[reg2]);

		dev1=((g*g)/(2.0*Q*N[reg1]))*(logreg1 + logdelta);
		dev2=((g*g)/(2.0*Q*N[reg2]))*(logreg2 + logdelta);

		dev=dev1+dev2;

		return ( (dR<dev) && (dG<dev) && (dB<dev) );
	}

	Rmpair[] BucketSort(Rmpair []a, int n)
	{
		int i;
		int[] nbe;
		int  [] cnbe;
		Rmpair []b;

		nbe=new int[256];
		cnbe=new int[256];

		b=new Rmpair[n];

		for(i=0;i<256;i++) nbe[i]=0;
		// class all elements according to their family
		for(i=0;i<n;i++) nbe[a[i].diff]++;
		// cumulative histogram
		cnbe[0]=0;
		for(i=1;i<256;i++)
			cnbe[i]=cnbe[i-1]+nbe[i-1]; // index of first element of category i

		// allocation
		for(i=0;i<n;i++)
		{b[cnbe[a[i].diff]++]=a[i];}

		return b;
	}

	//
	// Merge two regions
	//
	void MergeRegions(int C1, int C2)
	{
		int reg,nreg;
		double ravg,gavg,bavg;

		reg=UF.UnionRoot(C1,C2);

		nreg=N[C1]+N[C2];
		ravg=(N[C1]*Ravg[C1]+N[C2]*Ravg[C2])/nreg;
		gavg=(N[C1]*Gavg[C1]+N[C2]*Gavg[C2])/nreg;
		bavg=(N[C1]*Bavg[C1]+N[C2]*Bavg[C2])/nreg;

		N[reg]=nreg;

		Ravg[reg]=ravg;
		Gavg[reg]=gavg;
		Bavg[reg]=bavg;
	}

	//
	// Main segmentation procedure here
	//
	void Segmentation()
	{
		int i,j,index;
		int reg1,reg2;
		Rmpair [] order;
		int npair;
		int cpair=0;
		int C1,C2;
		int r1,g1,b1;
		int r2,g2,b2;

		// Consider C4-connectivity here
		npair=2*(w-1)*(h-1)+(h-1)+(w-1);
		order=new Rmpair[npair];

		for(i=0;i<h-1;i++)
		{
			for(j=0;j<w-1;j++)
			{
				index=i*w+j;

				// C4  left
				order[cpair]=new Rmpair();
				order[cpair].r1=index;
				order[cpair].r2=index+1;


				r1=raster[index] & 0xFF;
				g1= ((raster[index] & 0xFF00) >> 8) ;
				b1=((raster[index] & 0xFF0000) >> 16);

				r2=raster[index+1] & 0xFF;
				g2= ((raster[index+1] & 0xFF00) >> 8) ;
				b2=((raster[index+1] & 0xFF0000) >> 16);

				order[cpair].diff=(int)max3(Math.abs(r2-r1),Math.abs(g2-g1),Math.abs(b2-b1));
				cpair++;


				// C4 below
				order[cpair]=new Rmpair();
				order[cpair].r1=index;
				order[cpair].r2=index+w;

				r2=raster[index+w] & 0xFF;
				g2= ((raster[index+w] & 0xFF00) >> 8) ;
				b2=((raster[index+w] & 0xFF0000) >> 16);

				order[cpair].diff=(int)max3(Math.abs(r2-r1),Math.abs(g2-g1),Math.abs(b2-b1));
				cpair++;
			}
		}

		//
		// The two border lines
		//
		for(i=0;i<h-1;i++)
		{
			index=i*w+w-1;
			order[cpair]=new Rmpair();
			order[cpair].r1=index;
			order[cpair].r2=index+w;

			r1=raster[index] & 0xFF;
			g1= ((raster[index] & 0xFF00) >> 8) ;
			b1=((raster[index] & 0xFF0000) >> 16);
			r2=raster[index+w] & 0xFF;
			g2= ((raster[index+w] & 0xFF00) >> 8) ;
			b2=((raster[index+w] & 0xFF0000) >> 16);
			order[cpair].diff=(int)max3(Math.abs(r2-r1),Math.abs(g2-g1),Math.abs(b2-b1));
			cpair++;
		}

		for(j=0;j<w-1;j++)
		{
			index=(h-1)*w+j;

			order[cpair]=new Rmpair();
			order[cpair].r1=index;
			order[cpair].r2=index+1;

			r1=raster[index] & 0xFF;
			g1= ((raster[index] & 0xFF00) >> 8) ;
			b1=((raster[index] & 0xFF0000) >> 16);
			r2=raster[index+1] & 0xFF;
			g2= ((raster[index+1] & 0xFF00) >> 8) ;
			b2=((raster[index+1] & 0xFF0000) >> 16);
			order[cpair].diff=(int)max3(Math.abs(r2-r1),Math.abs(g2-g1),Math.abs(b2-b1));

			cpair++;
		}

		//
		// Sort the edges according to the maximum color channel difference
		//
		order=BucketSort(order,npair);

		// Main algorithm is here!!!

		for(i=0;i<npair;i++)
		{
			reg1=order[i].r1;
			C1=UF.Find(reg1);
			reg2=order[i].r2;
			C2=UF.Find(reg2);
			if ((C1!=C2)&&(MergePredicate(C1,C2))) MergeRegions(C1,C2);
		}

	}

	//
	// Output the segmentation: Average color for each region
	//
	void OutputSegmentation()
	{
		int i,j, index, indexb;
		int r,g,b,a,rgba;

		index=0;

		for(i=0;i<h;i++) // for each row
			for(j=0;j<w;j++) // for each column
			{
				index=i*w+j;
				indexb=UF.Find(index); // Get the root index

				//
				// average color choice in this demo
				//
				r  =(int)Ravg[indexb];
				g = (int)Gavg[indexb];
				b =(int)Bavg[indexb];

				rgba=(0xff000000 | b << 16 | g << 8 | r);

				rastero[index]=rgba;
			}
	}

	//
	// Merge small regions
	//
	void MergeSmallRegion()
	{
		int i,j, C1,C2, index;

		index=0;

		for(i=0;i<h;i++) // for each row
			for(j=1;j<w;j++) // for each column
			{
				index=i*w+j;
				C1=UF.Find(index);
				C2=UF.Find(index-1);
				if (C2!=C1) {if ((N[C2]<smallregion)||(N[C1]<smallregion)) MergeRegions(C1,C2);}
			}
	}

	// Draw white borders delimiting perceptual regions
	void DrawBorder()
	{
		int i, j, k, l, C1,C2, reg,index;

		for(i=1;i<h;i++) // for each row
			for(j=1;j<w;j++) // for each column
			{
				index=i*w+j;

				C1=UF.Find(index);
				C2=UF.Find(index-1-w);

				if (C2!=C1)
				{
					for(k=-borderthickness;k<=borderthickness;k++)
						for(l=-borderthickness;l<=borderthickness;l++)
						{
							index=(i+k)*w+(j+l);
							if ((index>=0)&&(index<w*h)) {

								rastero[index]=	(0xff000000 | 255 << 16 | 255 << 8 | 255);
							}
						}
				}
			}
	}
}

//
//Union Find Data Structure of Tarjan for Disjoint Sets
//
class UnionFind{

	int  []rank;
	int []parent;

	//
	//Create a UF for n elements
	//
	UnionFind(int n)
	{int k;

	parent=new int[n];
	rank=new int[n];

	for (k = 0; k < n; k++)
	{parent[k]   = k;
	rank[k] = 0;     }
	}

	//
	//Find procedures
	//
	int Find(int k)
	{
		while (parent[k]!=k ) k=parent[k];
		return k;}

	//
	//Assume x and y being roots
	//
	int UnionRoot(int x, int y)
	{
		if ( x == y ) return -1;

		if (rank[x] > rank[y])
		{parent[y]=x; return x;}
		else
		{ parent[x]=y;if (rank[x]==rank[y]) rank[y]++;return y;}
	}
}


//
//An edge: two indices and a difference value
//
class Rmpair
{
	int r1,r2;
	int diff;

	Rmpair()
	{
		r1=0;
		r2=0;
		diff=0;
	}

}
