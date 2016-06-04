

/**
 * Calculates a surface normal of a 3d polygon
 * input 3 x,y,z co-ordinates, anti-clockwise when viewed from outside
 * of the polygonal object to receive the surface normal for the 
 * outside plane of the polygon
 * @author daragh
 *
 */
public class CalcNormal {

	public static void main(String[] args) {

		float[] pointA = {-8f, 0f, -4f};
		float[] pointB = {8f, 0f, -4f};
		float[] pointC = {8f, 0f, 4f};
		
		findNormal(pointA, pointB, pointC);
	}
	
	
	public static void findNormal(float[] pointA, float[] pointB, float[] pointC){
		
		float[] vectorU = new float[3];
		float[] vectorV = new float[3];
		
		vectorU[0] = pointB[0] - pointA[0];
		vectorU[1] = pointB[1] - pointA[1];
		vectorU[2] = pointB[2] - pointA[2];
		
		vectorV[0] = pointC[0] - pointA[0];
		vectorV[1] = pointC[1] - pointA[1];
		vectorV[2] = pointC[2] - pointA[2];		
		
		float[] normal = new float[3];
		
		normal[0] = ( vectorU[1] * vectorV[2] ) - ( vectorU[2] * vectorV[1] );
		normal[1] = ( vectorU[2] * vectorV[0] ) - ( vectorU[0] * vectorV[2] );
		normal[2] = ( vectorU[0] * vectorV[1] ) - ( vectorU[1] * vectorV[0] );
		
		float sum = (normal[0]*normal[0]) + (normal[1]*normal[1]) + (normal[2]*normal[2]);
		float vLength = (float) Math.sqrt( sum );
		
		
		//return normal
		System.out.println("normal x :" + normal[0]/vLength);
		System.out.println("normal y :" + normal[1]/vLength);
		System.out.println("normal z :" + normal[2]/vLength);
		
	}

}















