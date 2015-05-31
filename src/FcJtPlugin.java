//################################################################################
//	The MIT License
//
//	Copyright (c) 2014 Johannes Raida
//
//	Permission is hereby granted, free of charge, to any person obtaining a copy
//	of this software and associated documentation files (the "Software"), to deal
//	in the Software without restriction, including without limitation the rights
//	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//	copies of the Software, and to permit persons to whom the Software is
//	furnished to do so, subject to the following conditions:
//
//	The above copyright notice and this permission notice shall be included in
//	all copies or substantial portions of the Software.
//
//	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//	THE SOFTWARE.
//################################################################################

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import de.raida.jcadlib.cadimport.jt.JTImporter;

/**
 * Test class for the JT importer.
 */
public class FcJtPlugin {

	
	private void writeCashFile(JTImporter jtImporter,File directory ) throws Exception {
		
		System.out.println("-----Write mesh cash files ---------------------------------------------");

		HashMap<String, ArrayList<Object[]>> faceEntities = jtImporter.getFaces();
		System.out.println(" ... # layers with faces: " + faceEntities.size());
		for(Iterator<String> iterator = faceEntities.keySet().iterator(); iterator.hasNext();){
			String layerName = iterator.next();
			System.out.println("     ... layer: " + layerName);
			ArrayList<Object[]> faces = faceEntities.get(layerName);
			System.out.println("         ... # entities: " + faces.size());

			File cashFile = new File(directory + File.separator + layerName +".FcCache");
			System.out.println("CashFileName: " + cashFile);
			DataOutputStream os = new DataOutputStream(new FileOutputStream(cashFile));
			
			// write number of entities  
			os.writeInt(faces.size());
			
			File file = new File(directory + File.separator + layerName  +".stl");
			System.out.println("FileName: " + file);
				
			file.createNewFile();
			PrintWriter writer = new PrintWriter(file, "UTF-8");
				
			writer.println(" solid test ");

			int n = 1;
			int CntVerts=0;
			int CntIdx=0;
			int CntColor=0;
			int CntNorm=0;
			
			for(Object[] faceList : faces){
				
				double[] vertices = (double[])faceList[0];
				int[] indices = (int[])faceList[1];
				double[] colors = (double[])faceList[2];
				double[] normals = (double[])faceList[3];
				
				CntVerts += vertices.length;
				CntIdx += indices.length ;
				CntColor += colors.length;
				CntNorm += normals.length ;

			}
			
			// write over all sizes   
			os.writeInt(CntVerts);
			os.writeInt(CntIdx);
			os.writeInt(CntColor);
			os.writeInt(CntNorm);
			
			System.out.println("             ... OverAllVert: "  + CntVerts + " Indx: " + CntIdx + " Color: " + CntColor + " Norm: " + CntNorm);

			for(Object[] faceList : faces){
					
				// Write STL files ========================================================================
					
				double[] vertices = (double[])faceList[0];
				int[] indices = (int[])faceList[1];
				double[] colors = (double[])faceList[2];
				double[] normals = (double[])faceList[3];
				
				if(n==1){
					System.out.println("             ... [entity 1] vertices: " + vertices.length + " => (showing 1) [" + vertices[0] + ", " + vertices[1] + ", " + vertices[2] + "]");
					System.out.println("             ... [entity 1] indices: " + indices.length + " => (showing 3) [" + indices[0] + ", " + indices[1] + ", " + indices[2] + "]");
					System.out.println("             ... [entity 1] colors: " + colors.length + " => (showing 1) [" + colors[0] + ", " + colors[1] + ", " + colors[2] + "]");
					System.out.println("             ... [entity 1] normals: " + normals.length + " => (showing 1) [" + normals[0] + ", " + normals[1] + ", " + normals[2] + "]");
				}
				for( int l= 0; l<indices.length; l=l+3) {
					
					int i;
					i = indices[l] * 3;
					writer.println("  facet normal " + normals[i] + " " + normals[i+1] + " " + normals[i+2] );
					
					writer.println("   endloop "); 
					writer.println("     vertex " + vertices[i] + " " + vertices[i+1] + " " + vertices[i+2] );
					i = indices[l+1] * 3;
					writer.println("     vertex " + vertices[i] + " " + vertices[i+1] + " " + vertices[i+2] );
					i = indices[l+2] * 3;
					writer.println("     vertex " + vertices[i] + " " + vertices[i+1] + " " + vertices[i+2] );
					
					writer.println("   endloop "); 
					writer.println(" endfacet "); 
					
				}
				
			
				// write cash file =====================================================================
				
				// vertices 
				os.writeInt(vertices.length);
				for(double d: vertices){
					os.writeFloat((float)d);
				}
				// indexes 
				os.writeInt(indices.length);
				for(int d: indices){
					os.writeInt(d);
				}
				// normals
				os.writeInt(normals.length);
				for(double d: normals){
					os.writeFloat((float)d);
				}
				// color forced to byte
				os.writeInt(colors.length);
				for(double d: colors){
					os.writeByte((int)(d*255.0));
				}
				
				n=n+1;

			}
			
			writer.println("endsolid test ");
			writer.close();
			
			os.close();
		}
		
	
	}
	
	private void writeSTL(JTImporter jtImporter,File directory ) throws Exception {
		
		System.out.println("-----Write mesh cash files ---------------------------------------------");

		HashMap<String, ArrayList<Object[]>> faceEntities = jtImporter.getFaces();
		System.out.println(" ... # layers with faces: " + faceEntities.size());
		for(Iterator<String> iterator = faceEntities.keySet().iterator(); iterator.hasNext();){
			String layerName = iterator.next();
			System.out.println("     ... layer: " + layerName);
			ArrayList<Object[]> faces = faceEntities.get(layerName);
			System.out.println("         ... # entities: " + faces.size());

			layerName = layerName.replace(":","_");
			layerName = layerName.replace(";","_");
			layerName = layerName.replace(",","_");
			layerName = layerName.replace(" ","-");
		
			File file = new File(directory + File.separator + layerName  +".stl");
			System.out.println("FileName: " + file);
				
			file.createNewFile();
			PrintWriter writer = new PrintWriter(file, "UTF-8");
				
			writer.println(" solid test ");

			int n = 1;
			int CntVerts=0;
			int CntIdx=0;
			int CntColor=0;
			int CntNorm=0;
			
			for(Object[] faceList : faces){
				
				double[] vertices = (double[])faceList[0];
				int[] indices = (int[])faceList[1];
				double[] colors = (double[])faceList[2];
				double[] normals = (double[])faceList[3];
				
				CntVerts += vertices.length;
				CntIdx += indices.length ;
				CntColor += colors.length;
				CntNorm += normals.length ;

			}
			
	
			System.out.println("             ... OverAllVert: "  + CntVerts + " Indx: " + CntIdx + " Color: " + CntColor + " Norm: " + CntNorm);

			for(Object[] faceList : faces){
					
				// Write STL files ========================================================================
					
				double[] vertices = (double[])faceList[0];
				int[] indices = (int[])faceList[1];
				double[] colors = (double[])faceList[2];
				double[] normals = (double[])faceList[3];
				
				if(n==1){
					System.out.println("             ... [entity 1] vertices: " + vertices.length + " => (showing 1) [" + vertices[0] + ", " + vertices[1] + ", " + vertices[2] + "]");
					System.out.println("             ... [entity 1] indices: " + indices.length + " => (showing 3) [" + indices[0] + ", " + indices[1] + ", " + indices[2] + "]");
					System.out.println("             ... [entity 1] colors: " + colors.length + " => (showing 1) [" + colors[0] + ", " + colors[1] + ", " + colors[2] + "]");
					System.out.println("             ... [entity 1] normals: " + normals.length + " => (showing 1) [" + normals[0] + ", " + normals[1] + ", " + normals[2] + "]");
				}
				for( int l= 0; l<indices.length; l=l+3) {
					
					int i;
					i = indices[l] * 3;
					writer.println("  facet normal " + normals[i] + " " + normals[i+1] + " " + normals[i+2] );
					
					writer.println("  outer loop "); 
					writer.println("     vertex " + vertices[i] + " " + vertices[i+1] + " " + vertices[i+2] );
					i = indices[l+1] * 3;
					writer.println("     vertex " + vertices[i] + " " + vertices[i+1] + " " + vertices[i+2] );
					i = indices[l+2] * 3;
					writer.println("     vertex " + vertices[i] + " " + vertices[i+1] + " " + vertices[i+2] );
					
					writer.println("   endloop "); 
					writer.println(" endfacet "); 
					
				}
				
			
			}
			
			writer.println("endsolid test ");
			writer.close();
			System.out.println(":STL-File:" + file) ;


		}
		
	
	}
	/**
	 * Prints information, available after loading the file.
	 * @param  jtImporter JT importer
	 * @throws Exception  Thrown when something happens
	 */
	@SuppressWarnings("unchecked")
	private void printInformation(JTImporter jtImporter) throws Exception {
		System.out.println("\nLoad information:");
		System.out.println("--------------------------------------------------");
		ArrayList<String[]> loadInformation = jtImporter.getLoadInformation();
		if(loadInformation.size() == 0){
			System.out.println("   ---");
		} else {
			for(String[] information : loadInformation){
				System.out.println("   " + information[0] + ": " + information[1]);
			}
		}

		System.out.println("\nModel information:");
		System.out.println("--------------------------------------------------");
		for(String[] modelInformation : jtImporter.getModelInformation()){
			if(!modelInformation[0].equals("") && !modelInformation[1].equals("")){
				System.out.println("   " + modelInformation[0] + ": " + modelInformation[1]);
			} else if(!modelInformation[0].equals("")){
				System.out.println("   " + modelInformation[0]);
			} else if(!modelInformation[1].equals("")){
				System.out.println("   " + modelInformation[1]);
			} else {
				System.out.println();
			}
		}

		System.out.println("\nUnsupported entities:");
		System.out.println("--------------------------------------------------");
		for(String unsupportedEntity : jtImporter.getUnsupportedEntities()){
			System.out.println("   " + unsupportedEntity);
		}

		System.out.println("\nMeta data:");
		System.out.println("--------------------------------------------------");
		double[][] boundingBox = jtImporter.getExtremeValues();
		System.out.println(" ... BBox min: " + boundingBox[0][0] + ", " + boundingBox[0][1] + ", " + boundingBox[0][2]);
		System.out.println(" ... BBox max: " + boundingBox[1][0] + ", " + boundingBox[1][1] + ", " + boundingBox[1][2]);
		HashMap<String, Boolean> layerMetadata = jtImporter.getLayerMetaData();
		System.out.println(" ... Number of layers: " + layerMetadata.size());
		for(String layerName : layerMetadata.keySet()){
			System.out.println("     ... visibility: " + layerName + " => " + layerMetadata.get(layerName));
		}

		System.out.println("\nPoints:");
		System.out.println("--------------------------------------------------");
		HashMap<String, ArrayList<Object[]>> pointEntities = jtImporter.getPoints();
		System.out.println(" ... # layers with points: " + pointEntities.size());
		for(Iterator<String> iterator = pointEntities.keySet().iterator(); iterator.hasNext();){
			String layerName = iterator.next();
			System.out.println("     ... layer: " + layerName);
			ArrayList<Object[]> points = pointEntities.get(layerName);
			System.out.println("         ... # entities: " + points.size());
			for(Object[] point : points){
				List<Double> vertices = (List<Double>)point[0];
				System.out.println("             ... [entity 1] vertices: " + (vertices.size() / 3) + " => (showing 1) " + vertices.subList(0, 3));

				List<Float> colors = (List<Float>)point[1];
				System.out.println("             ... [entity 1] colors: " + (colors.size() / 3) + " => (showing 1) " + colors.subList(0, 3));

				if(points.size() > 1){
					System.out.println("             ...");
				}
				break;
			}
		}

		System.out.println("\nPolylines:");
		System.out.println("--------------------------------------------------");
		HashMap<String, ArrayList<Object[]>> polylineEntities = jtImporter.getPolylines();
		System.out.println(" ... # layers with polylines: " + polylineEntities.size());
		for(Iterator<String> iterator = polylineEntities.keySet().iterator(); iterator.hasNext();){
			String layerName = iterator.next();
			System.out.println("     ... layer: " + layerName);
			ArrayList<Object[]> polylines = polylineEntities.get(layerName);
			System.out.println("         ... # entities: " + polylines.size());
			for(Object[] polyline : polylines){
				List<Double[]> vertices = (List<Double[]>)polyline[0];
				System.out.println("             ... [entity 1] vertices: " + vertices.size() + " => (showing 1) " + Arrays.toString(vertices.get(0)));

				List<Double[]> colors = (List<Double[]>)polyline[1];
				System.out.println("             ... [entity 1] colors: " + colors.size() + " => (showing 1) " + Arrays.toString(colors.get(0)));

				if(polylines.size() > 1){
					System.out.println("             ...");
				}
				break;
			}
		}

		System.out.println("\nFaces:");
		System.out.println("--------------------------------------------------");
		HashMap<String, ArrayList<Object[]>> faceEntities = jtImporter.getFaces();
		System.out.println(" ... # layers with faces: " + faceEntities.size());
		for(Iterator<String> iterator = faceEntities.keySet().iterator(); iterator.hasNext();){
			String layerName = iterator.next();
			System.out.println("     ... layer: " + layerName);
			ArrayList<Object[]> faces = faceEntities.get(layerName);
			System.out.println("         ... # entities: " + faces.size());
			for(Object[] faceList : faces){
				double[] vertices = (double[])faceList[0];
				int[] indices = (int[])faceList[1];
				double[] colors = (double[])faceList[2];
				double[] normals = (double[])faceList[3];
				System.out.println("             ... [entity 1] vertices: " + vertices.length + " => (showing 1) [" + vertices[0] + ", " + vertices[1] + ", " + vertices[2] + "]");
				System.out.println("             ... [entity 1] indices: " + indices.length + " => (showing 3) [" + indices[0] + ", " + indices[1] + ", " + indices[2] + "]");
				System.out.println("             ... [entity 1] colors: " + colors.length + " => (showing 1) [" + colors[0] + ", " + colors[1] + ", " + colors[2] + "]");
				System.out.println("             ... [entity 1] normals: " + normals.length + " => (showing 1) [" + normals[0] + ", " + normals[1] + ", " + normals[2] + "]");

				if(faces.size() > 1){
					System.out.println("             ...");
				}
				break;
			}
		}
	}
	/**
	 * Writes the mesh to StdOUt
	 * @param  jtImporter JT importer
	 * @throws Exception  Thrown when something happens
	 */

	private void writeToStdOut(JTImporter jtImporter ) throws Exception {
		
		System.out.println("-----Write mesh to STd out ---------------------------------------------");

		HashMap<String, ArrayList<Object[]>> faceEntities = jtImporter.getFaces();
		System.out.println(" ... # layers with faces: " + faceEntities.size());
		for(Iterator<String> iterator = faceEntities.keySet().iterator(); iterator.hasNext();){
			String layerName = iterator.next();
			System.out.println("     ... layer: " + layerName);
			ArrayList<Object[]> faces = faceEntities.get(layerName);
			System.out.println("         ... # entities: " + faces.size());


			int n = 1;
			int CntVerts=0;
			int CntIdx=0;
			int CntColor=0;
			int CntNorm=0;
			
			for(Object[] faceList : faces){
				
				double[] vertices = (double[])faceList[0];
				int[] indices = (int[])faceList[1];
				double[] colors = (double[])faceList[2];
				double[] normals = (double[])faceList[3];
				
				CntVerts += vertices.length;
				CntIdx += indices.length ;
				CntColor += colors.length;
				CntNorm += normals.length ;

			}
			
	
			System.out.println("             ... OverAllVert: "  + CntVerts + " Indx: " + CntIdx + " Color: " + CntColor + " Norm: " + CntNorm);

			for(Object[] faceList : faces){
					
				// Write STL files ========================================================================
					
				double[] vertices = (double[])faceList[0];
				int[] indices = (int[])faceList[1];
				double[] colors = (double[])faceList[2];
				double[] normals = (double[])faceList[3];
				
				System.out.println(":F:" + layerName);
				
				if(n==1){
					System.out.println("             ... [entity 1] vertices: " + vertices.length + " => (showing 1) [" + vertices[0] + ", " + vertices[1] + ", " + vertices[2] + "]");
					System.out.println("             ... [entity 1] indices: " + indices.length + " => (showing 3) [" + indices[0] + ", " + indices[1] + ", " + indices[2] + "]");
					System.out.println("             ... [entity 1] colors: " + colors.length + " => (showing 1) [" + colors[0] + ", " + colors[1] + ", " + colors[2] + "]");
					System.out.println("             ... [entity 1] normals: " + normals.length + " => (showing 1) [" + normals[0] + ", " + normals[1] + ", " + normals[2] + "]");
				}
				
				System.out.println(":VC:" + vertices.length);
				for( int l= 0; l<vertices.length; l=l+1) {
					System.out.println(":V:" + vertices[l]);
				}
				System.out.println(":CC:" + colors.length);
				for( int l= 0; l<colors.length; l=l+1) {
					System.out.println(":C:" + colors[l]);
				}
				System.out.println(":NC:" + normals.length);
				for( int l= 0; l<normals.length; l=l+1) {
					System.out.println(":N:" + normals[l]);
				}
				
				System.out.println(":IC:" + indices.length);
				for( int l= 0; l<indices.length; l=l+1) {
					System.out.println(":I:" + indices[l]);
				}
				
				
				
			
			}
			


		}
	}
	
	/**
	 * Main entry point.
	 * @param arguments Arguments of the command line
	 */
/*	public static void main(String[] arguments){
		TestJTImporter testJTImporter = new TestJTImporter();
		testJTImporter.testImport(new JTImporter(), "data");
	}*/
	
	public static void main(String[] arguments){
		if(arguments.length == 1)
		{
			try {
				FcJtPlugin importPlugin = new FcJtPlugin();
				JTImporter jtImporter = new JTImporter();
				
				//testJTImporter.testImport(new JTImporter(), "data");
				File file = new File(arguments[0]);
				System.out.println("Load file: "+ file.getAbsoluteFile().toURI().toURL());
				jtImporter.loadFile(file.toURI().toURL());
				
				importPlugin.printInformation(jtImporter);
				
				//importPlugin.writeCashFile(jtImporter,new File("D:\\temp"));
				
				//Path outFolder = Paths.get(arguments[1]);
				
				/*if (Files.notExists(outFolder)) {
					boolean success = (new File(arguments[1])).mkdirs();
					if (!success) {
						throw new IllegalArgumentException("Cannot create output dir");
					}
				 
				}*/
				// write the STL to the output dir
				//importPlugin.writeSTL(jtImporter,new File(arguments[1]));
				
				// write to StdOut
				importPlugin.writeToStdOut(jtImporter);
				
			} catch(Exception exception){
				exception.printStackTrace();
			}

		}
		else
		{
			System.out.println("Wrong parameters: use JtFile OutDir");
		}
	}
}
