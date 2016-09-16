package gustavo.brilhante.magiccube.grafic;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

public class Cube {
	FloatBuffer mFVertexBuffer;
	ByteBuffer mColorBuffer;
	ByteBuffer mTfan1;
	ByteBuffer mTfan2;
	ArrayList<Color> cor =new ArrayList<Color>();
	//float[] normalData = new float[108];
	//FloatBuffer m_NormalData;
	
	public void colors(char cor1, char cor2, char cor3, char cor4, char cor5,char cor6){
		byte maxColor = (byte)255;
		
		cor.get(0).Letra = cor1;
		cor.get(1).Letra = cor2;
		cor.get(2).Letra = cor3;
		cor.get(3).Letra = cor4;
		cor.get(4).Letra = cor5;
		cor.get(5).Letra = cor6;
		//RED (R)   : (byte) 130,0,0,maxColor
		//YELLOW (Y) : maxColor,maxColor,0,maxColor
		//BLUE  (B) : 0,0,maxColor,maxColor 
		//GREEN (G) : 0,maxColor,0,maxColor
		//WHITE (W) : maxColor,maxColor,maxColor,maxColor
		//ORANGE (O) : maxColor,69,0,maxColor
		//BLACK (K) : 0,0,0,maxColor
		
		for(int i=0;i<6;i++){
			switch(cor.get(i).Letra){
				case 'R':
						cor.get(i).v1 = (byte) 80;
						cor.get(i).v2 = 0;
						cor.get(i).v3 = 0;
						cor.get(i).v4 = maxColor;
						break;
				case 'Y':
						cor.get(i).v1 = maxColor;
						cor.get(i).v2 = maxColor;
						cor.get(i).v3 = 0;
						cor.get(i).v4 = maxColor;
						break;
				case 'B':
						cor.get(i).v1 = 0;
						cor.get(i).v2 = 0;
						cor.get(i).v3 = maxColor;	
						cor.get(i).v4 = maxColor;
						break;
				case 'G':
						cor.get(i).v1 = 0;
						cor.get(i).v2 = 85; 
						cor.get(i).v3 = 43;		
						cor.get(i).v4 = maxColor;
						break;
				case 'W':
						cor.get(i).v1 = maxColor;
						cor.get(i).v2 = maxColor;
						cor.get(i).v3 = maxColor;
						cor.get(i).v4 = maxColor;
						break;
				case 'O':
						cor.get(i).v1 = (byte)150;
						cor.get(i).v2 = 89;
						cor.get(i).v3 = 0;	
						cor.get(i).v4 = maxColor;
						break;
				case 'K':
						cor.get(i).v1 = 0;
						cor.get(i).v2 = 0;
						cor.get(i).v3 = 0;
						cor.get(i).v4 = maxColor;
						break;
			}
		
		}
		
		byte colors[] = {
			cor.get(0).v1,cor.get(0).v2 ,cor.get(0).v3 ,cor.get(0).v4,      //0
			cor.get(0).v1,cor.get(0).v2 ,cor.get(0).v3 ,cor.get(0).v4,		//1
			cor.get(0).v1,cor.get(0).v2 ,cor.get(0).v3 ,cor.get(0).v4,		//2
			cor.get(0).v1,cor.get(0).v2 ,cor.get(0).v3 ,cor.get(0).v4,		//3
			
			cor.get(1).v1,cor.get(1).v2 ,cor.get(1).v3 ,cor.get(1).v4,		//4
			cor.get(2).v1,cor.get(2).v2 ,cor.get(2).v3 ,cor.get(2).v4,		//5
			cor.get(2).v1,cor.get(2).v2 ,cor.get(2).v3 ,cor.get(2).v4,		//6
			cor.get(3).v1,cor.get(3).v2 ,cor.get(3).v3 ,cor.get(3).v4,		//7
				
			cor.get(1).v1,cor.get(1).v2 ,cor.get(1).v3 ,cor.get(1).v4,		//8
			cor.get(0).v1,cor.get(0).v2 ,cor.get(0).v3 ,cor.get(0).v4,		//9
			cor.get(2).v1,cor.get(2).v2 ,cor.get(2).v3 ,cor.get(2).v4,		//10
			cor.get(0).v1,cor.get(0).v2 ,cor.get(0).v3 ,cor.get(0).v4,		//11
			
			cor.get(1).v1,cor.get(1).v2 ,cor.get(1).v3 ,cor.get(1).v4,		//12	
			cor.get(1).v1,cor.get(1).v2 ,cor.get(1).v3 ,cor.get(1).v4,		//13
			cor.get(2).v1,cor.get(2).v2 ,cor.get(2).v3 ,cor.get(2).v4,		//14
			cor.get(3).v1,cor.get(3).v2 ,cor.get(3).v3 ,cor.get(3).v4,		//15
			
			cor.get(4).v1,cor.get(4).v2 ,cor.get(4).v3 ,cor.get(4).v4,		//16
			cor.get(2).v1,cor.get(2).v2 ,cor.get(2).v3 ,cor.get(2).v4,		//17
			cor.get(5).v1,cor.get(5).v2 ,cor.get(5).v3 ,cor.get(5).v4,		//18
			cor.get(5).v1,cor.get(5).v2 ,cor.get(5).v3 ,cor.get(5).v4,		//19
			
			cor.get(3).v1,cor.get(3).v2 ,cor.get(3).v3 ,cor.get(3).v4,		//20
			cor.get(3).v1,cor.get(3).v2 ,cor.get(3).v3 ,cor.get(3).v4,	    //21
			cor.get(3).v1,cor.get(3).v2 ,cor.get(3).v3 ,cor.get(3).v4,		//22
			cor.get(5).v1,cor.get(5).v2 ,cor.get(5).v3 ,cor.get(5).v4,		//23
			
			cor.get(4).v1,cor.get(4).v2 ,cor.get(4).v3 ,cor.get(4).v4, 		//24
			cor.get(2).v1,cor.get(2).v2 ,cor.get(2).v3 ,cor.get(2).v4,		//25
			cor.get(5).v1,cor.get(5).v2 ,cor.get(5).v3 ,cor.get(5).v4,		//26
			cor.get(4).v1,cor.get(4).v2 ,cor.get(4).v3 ,cor.get(4).v4,		//27
			 
			cor.get(4).v1,cor.get(4).v2 ,cor.get(4).v3 ,cor.get(4).v4,		//28
			cor.get(3).v1,cor.get(3).v2 ,cor.get(3).v3 ,cor.get(3).v4,	    //29
			cor.get(5).v1,cor.get(5).v2 ,cor.get(5).v3 ,cor.get(5).v4,		//30
			cor.get(5).v1,cor.get(5).v2 ,cor.get(5).v3 ,cor.get(5).v4,		//31
								
			cor.get(1).v1,cor.get(1).v2 ,cor.get(1).v3 ,cor.get(1).v4,		//32
			cor.get(4).v1,cor.get(4).v2 ,cor.get(4).v3 ,cor.get(4).v4,		//33
			cor.get(1).v1,cor.get(1).v2 ,cor.get(1).v3 ,cor.get(1).v4,		//34
			cor.get(4).v1,cor.get(4).v2 ,cor.get(4).v3 ,cor.get(4).v4,		//35
			
		};
		mColorBuffer = ByteBuffer.allocateDirect(colors.length);
		mColorBuffer.put(colors);
		mColorBuffer.position(0);
	}
	public Cube(char cor1,char cor2 ,char cor3,char cor4,char cor5,char cor6){
		
		float [] vertices = {
			-1.0f, 1.0f, 1.0f,  //0
			 1.0f, 1.0f, 1.0f,	//1  P1
			 1.0f,-1.0f, 1.0f,	//2
			-1.0f,-1.0f, 1.0f,	//3
			 
			-1.0f, 1.0f,-1.0f,	//4
			 1.0f, 1.0f,-1.0f,	//5
			 1.0f,-1.0f,-1.0f,	//6
			-1.0f,-1.0f,-1.0f,  //7  P2
			
			-1.0f, 1.0f, 1.0f,  //8
			 1.0f, 1.0f, 1.0f,	//9  P1
			 1.0f,-1.0f, 1.0f,	//10
			-1.0f,-1.0f, 1.0f,	//11
			 
			-1.0f, 1.0f,-1.0f,	//12
			 1.0f, 1.0f,-1.0f,	//13
			 1.0f,-1.0f,-1.0f,	//14
			-1.0f,-1.0f,-1.0f,  //15 P2
			
			-1.0f, 1.0f, 1.0f,  //16
			 1.0f, 1.0f, 1.0f,	//17 P1
			 1.0f,-1.0f, 1.0f,	//18
			-1.0f,-1.0f, 1.0f,	//19
			 
			-1.0f, 1.0f,-1.0f,	//20
			 1.0f, 1.0f,-1.0f,	//21
			 1.0f,-1.0f,-1.0f,	//22
			-1.0f,-1.0f,-1.0f,  //23 P2
			
			-1.0f, 1.0f, 1.0f,  //24
			 1.0f, 1.0f, 1.0f,	//25 P1
			 1.0f,-1.0f, 1.0f,	//26
			-1.0f,-1.0f, 1.0f,	//27
			 
			-1.0f, 1.0f,-1.0f,	//28
			 1.0f, 1.0f,-1.0f,	//29
			 1.0f,-1.0f,-1.0f,	//30
			-1.0f,-1.0f,-1.0f,  //31 P2
			
			 1.0f, 1.0f, 1.0f,  //32 P1
			-1.0f,-1.0f,-1.0f,  //33 P2
			 1.0f, 1.0f, 1.0f,  //34 P1
			-1.0f,-1.0f,-1.0f,  //35 P2
					
		};
		for(int i = 0 ; i<27;i++){
			cor.add(new Color());
		}

		colors(cor1,cor2,cor3,cor4,cor5,cor6);
		byte tfan1[] = {
	
			1,0,3,
			9,11,2,
			17,10,6,
			25,14,5,
			32,13,4, //
			34,12,8
		};
			
		byte tfan2[] = {
				
			7,20,21, 
			15,29,22,
			23,30,18,
			31,26,19,
			33,27,16,
			35,24,28
		};
		
	
//		for(int i=0; i<36; i++){
//			normalData[3*i] = vertices[3*i];
//			normalData[3*i+1] = vertices[3*i+1];
//			normalData[3*i+2] = vertices[3*i+2];
//		}
//		
//		m_NormalData = makeFloatBuffer(normalData);
				
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);		
		vbb.order(ByteOrder.nativeOrder());
		mFVertexBuffer = vbb.asFloatBuffer();
		mFVertexBuffer.put(vertices);
		mFVertexBuffer.position(0);
		
		mTfan1 = ByteBuffer.allocateDirect(tfan1.length);
		mTfan1.put(tfan1);
		mTfan1.position(0);
		
		mTfan2 = ByteBuffer.allocateDirect(tfan2.length);
		mTfan2.put(tfan2);
		mTfan2.position(0);
		} 
	
		public char getfront(){
			return this.cor.get(0).Letra;
		}
		public char getup(){
			return this.cor.get(1).Letra;
		}
		public char getright(){
			return this.cor.get(2).Letra;
		}
		public char getback(){
			return this.cor.get(3).Letra;
		}
		public char getleft(){
			return this.cor.get(4).Letra;
		}
		public char getdown(){
			return this.cor.get(5).Letra;
		}
		public void setfront(char cor){
			colors(cor,this.cor.get(1).Letra,this.cor.get(2).Letra,this.cor.get(3).Letra,this.cor.get(4).Letra,this.cor.get(5).Letra);
			this.cor.get(0).Letra = cor;
		}
		public void setup(char cor){
			colors(this.cor.get(0).Letra,cor,this.cor.get(2).Letra,this.cor.get(3).Letra,this.cor.get(4).Letra,this.cor.get(5).Letra);
			this.cor.get(1).Letra = cor;
		}
		public void setright(char cor){
			colors(this.cor.get(0).Letra,this.cor.get(1).Letra,cor,this.cor.get(3).Letra,this.cor.get(4).Letra,this.cor.get(5).Letra);
			this.cor.get(2).Letra = cor;
		}
		public void setback(char cor){
			colors(this.cor.get(0).Letra,this.cor.get(1).Letra,this.cor.get(2).Letra,cor,this.cor.get(4).Letra,this.cor.get(5).Letra);
			this.cor.get(3).Letra = cor;
		}
		public void setleft(char cor){
			colors(this.cor.get(0).Letra,this.cor.get(1).Letra,this.cor.get(2).Letra,this.cor.get(3).Letra,cor,this.cor.get(5).Letra);
			this.cor.get(4).Letra = cor;
		}
		public void setdown(char cor){
			colors(this.cor.get(0).Letra,this.cor.get(1).Letra,this.cor.get(2).Letra,this.cor.get(3).Letra,this.cor.get(4).Letra,cor);
			this.cor.get(5).Letra = cor;
		}
		
		public static FloatBuffer makeFloatBuffer(float[] arr)
		{
			ByteBuffer bb = ByteBuffer.allocateDirect(arr.length*4);
			bb.order(ByteOrder.nativeOrder());
			FloatBuffer fb = bb.asFloatBuffer();
			fb.put(arr);
			fb.position(0);
			return fb;
		}

		public void draw(GL10 gl){
			gl.glFrontFace(GL10.GL_CCW);
			gl.glVertexPointer(3, GL10.GL_FLOAT , 0 , mFVertexBuffer);		
			gl.glColorPointer(4, GL10.GL_UNSIGNED_BYTE, 0, mColorBuffer);
			
//			gl.glNormalPointer(GL10.GL_FLOAT, 0, m_NormalData);
//			gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
			
			gl.glDrawElements(gl.GL_TRIANGLE_FAN, 6*3 , gl.GL_UNSIGNED_BYTE, mTfan1);
			gl.glDrawElements(gl.GL_TRIANGLE_FAN, 6*3 , gl.GL_UNSIGNED_BYTE, mTfan2);
			gl.glFrontFace(GL10.GL_CCW);
			
		} 
}