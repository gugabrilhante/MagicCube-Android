package gustavo.brilhante.magiccube.grafic;

import android.opengl.GLSurfaceView;
import android.util.Log;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import gustavo.brilhante.magiccube.activity.MagicCubeActivity;
import gustavo.brilhante.magiccube.activity.OpcoesActivity;

public class CubeRenderer implements GLSurfaceView.Renderer {

	public float fielOfView;
	private boolean mTranslucentBackground;
	public float angleTest = 0, speedY=(float) 0.1;
	public float angleTest2 = 0 ,speedX=(float) 0.1;
	public float inc = 5;
	public float angleTestAux = 0;
	public float angleTest2Aux = 0;
	public int tamanhoCubo;
	
	//public final static int SS_SUNLIGHT = GL10.GL_LIGHT0;
	
	private float Angle=0;
	private float soma,dist=(float) 2.12;
	private float xdist=0,ydist=0,zdist=0;
	
	
	int e=0;
	public static int rot=20,sense=-1;
	int k,sinal=1,aux1,aux2;
	int n=0,i=0,j=0,a=1,cont=0;
	int j2=0,i2=0,n2=0;
	public int pos[][][]=new int[3][3][3];
	public int pos_init[][][] = new int[3][3][3];
	public int face_color[][] = new int[6][];
	int eixoy[]= new int[] {0,1,2,3};
	int eixoz[]= new int[] {1,5,3,4};
	int eixox[]= new int[] {0,4,2,5};
	
	
	static public boolean rotating = true;
	public boolean embaralhando = true;
	public int num_embaralhar=10*(OpcoesActivity.embaralhar);
	
	
	ArrayList<Cube> cubeList = new ArrayList<Cube>();
	ArrayList<Cube> cubeList2 = new ArrayList<Cube>();
	
	public CubeRenderer(boolean useTranslucentBackground){
		mTranslucentBackground = useTranslucentBackground;
		
		
		
		cubeList.add(new Cube('K','Y','K','G','O','K'));
		cubeList.add(new Cube('K','Y','K','G','K','K'));
		cubeList.add(new Cube('K','Y','R','G','K','K'));
		
		cubeList.add(new Cube('K','Y','K','K','O','K'));
		cubeList.add(new Cube('K','Y','K','K','K','K'));
		cubeList.add(new Cube('K','Y','R','K','K','K'));
		
		cubeList.add(new Cube('B','Y','K','K','O','K'));
		cubeList.add(new Cube('B','Y','K','K','K','K'));
		cubeList.add(new Cube('B','Y','R','K','K','K'));
		
		cubeList.add(new Cube('K','K','K','G','O','K'));
		cubeList.add(new Cube('K','K','K','G','K','K'));
		cubeList.add(new Cube('K','K','R','G','K','K'));
		
		cubeList.add(new Cube('K','K','K','K','O','K')); 
		cubeList.add(new Cube('K','K','K','K','K','K'));
		cubeList.add(new Cube('K','K','R','K','K','K'));
		
		cubeList.add(new Cube('B','K','K','K','O','K'));
		cubeList.add(new Cube('B','K','K','K','K','K'));
		cubeList.add(new Cube('B','K','R','K','K','K'));
	
		cubeList.add(new Cube('K','K','K','G','O','W'));
		cubeList.add(new Cube('K','K','K','G','K','W'));
		cubeList.add(new Cube('K','K','R','G','K','W'));
		
		cubeList.add(new Cube('K','K','K','K','O','W'));
		cubeList.add(new Cube('K','K','K','K','K','W'));
		cubeList.add(new Cube('K','K','R','K','K','W'));
		
		cubeList.add(new Cube('B','K','K','K','O','W'));
		cubeList.add(new Cube('B','K','K','K','K','W'));
		cubeList.add(new Cube('B','K','R','K','K','W'));

		for(int j = 0 ; j<27 ; j++){
			//cubeList.add(new Cube());
			
			pos_init[j%3][n%3][i%3]=j;
			pos[j%3][n%3][i%3]=j;	
			if(i%3==2 && j%3==2)n++;
			if(j%3==2)i++;
			
		}

	} 
	
	//front==0
	//right==1
	//back==2
	//left==3
	//down==4
	//up==5
	
	public void ChangeColor(int cubo, int face, char letter){
		if(face==0)cubeList.get(cubo).setfront(letter);
		if(face==1)cubeList.get(cubo).setright(letter);
		if(face==2)cubeList.get(cubo).setback(letter);
		if(face==3)cubeList.get(cubo).setleft(letter);
		if(face==4)cubeList.get(cubo).setdown(letter);
		if(face==5)cubeList.get(cubo).setup(letter);
			
	}
	public char GetColor(int cubo, int face){
		if(face==0)return cubeList.get(cubo).getfront();
		if(face==1)return cubeList.get(cubo).getright();
		if(face==2)return cubeList.get(cubo).getback();
		if(face==3)return cubeList.get(cubo).getleft();
		if(face==4)return cubeList.get(cubo).getdown();
		if(face==5)return cubeList.get(cubo).getup();
		return 0;
			
	}
	
	
	
	public void SaveRot(int cubo){
		char cor1,cor2 = 0;
		int indice,t1,t2;

			
		if(rot==0 || rot==1 || rot==6){
			for(int q=0;q<4;q++){
				if(sense==1){
					t1=0;
					t2=1;
					indice=q;
				}
				else {
					t1=3;
					t2=0;
					indice = (3-q);
				}
				
				if(q==0)cor1=GetColor(cubo,eixoy[indice]);
				else cor1=cor2;
				cor2=GetColor(cubo, eixoy[((indice+t2)%4+t1)%4] );
				ChangeColor(cubo, eixoy[((indice+t2)%4+t1)%4] , cor1);
			}
		}
		if(rot==2 || rot==3 || rot==7){
			for(int q=0;q<4;q++){
				if(sense==1){
					t1=0;
					t2=1;
					indice=q;
				}
				else {
					t1=3;
					t2=0;
					indice = (3-q);
				}
				
				if(q==0)cor1=GetColor(cubo,eixoz[indice]);
				else cor1=cor2;
				cor2=GetColor(cubo, eixoz[((indice+t2)%4+t1)%4] );
				ChangeColor(cubo, eixoz[((indice+t2)%4+t1)%4] , cor1);
			}
		}
		if(rot==4 || rot==5 || rot==8){
			for(int q=0;q<4;q++){
				if(sense==1){
					t1=0;
					t2=1;
					indice=q;
				}
				else {
					t1=3;
					t2=0;
					indice = (3-q);
				}
				
				if(q==0)cor1=GetColor(cubo,eixox[indice]);
				else cor1=cor2;
				cor2=GetColor(cubo, eixox[((indice+t2)%4+t1)%4] );
				ChangeColor(cubo, eixox[((indice+t2)%4+t1)%4] , cor1);
			}
		}
		
	}
	
	public void save(){
		int s1,s2;
		
		
		if(rot==0 || rot==1 || rot==6){
			n2=e;
			for(s1=0;s1<2;s1++){	
				i2=0;
				j2=0;
				for(s2=0;s2<8;s2++){
					if(sense==1){
						if(s2==0)aux1=pos[j2][n2][i2];
						else aux1=aux2;
					}
					else{
						if(s2==0)aux1=pos[i2][n2][j2];
						else aux1=aux2;
					}
					
					
					if(i2==0 && j2!=0)j2--;
					else if(j2==2 && i2!=0)i2--;
					else if(i2==2 && j2!=2)j2++;
					else if(j2==0 && i2!=2)i2++;
					
					if(sense==1){
						aux2=pos[j2][n2][i2];
						pos[j2][n2][i2]=aux1;
					}
					else{
						aux2=pos[i2][n2][j2];
						pos[i2][n2][j2]=aux1;
					}
					
					if(s1==1)SaveRot(aux1);
				}
				

			}
		}
		
		if(rot==2 || rot==3 || rot==7){
			n2=e;
			for(s1=0;s1<2;s1++){
				i2=0;
				j2=0;
				for(s2=0;s2<8;s2++){
					if(sense==1){
						if(s2==0)aux1=pos[j2][i2][n2];
						else aux1=aux2;
					}
					else{
						if(s2==0)aux1=pos[i2][j2][n2];
						else aux1=aux2;
					}
					
					if(i2==0 && j2!=0)j2--;
					else if(j2==2 && i2!=0)i2--;
					else if(i2==2 && j2!=2)j2++;
					else if(j2==0 && i2!=2)i2++;

					if(sense==1){
						aux2=pos[j2][i2][n2];
						pos[j2][i2][n2]=aux1;
					}
					else{
						aux2=pos[i2][j2][n2];
						pos[i2][j2][n2]=aux1;
					}
					
					if(s1==1)SaveRot(aux1);
				}	
			}
		}
		
		if(rot==4 || rot==5 || rot==8){
			n2=e;
			for(s1=0;s1<2;s1++){
				i2=0;
				j2=0;
				for(s2=0;s2<8;s2++){
					if(sense==1){
						if(s2==0)aux1=pos[n2][j2][i2];
						else aux1=aux2;
					}
					else{
						if(s2==0)aux1=pos[n2][i2][j2];
						else aux1=aux2;
					}		
					
					if(i2==0 && j2!=0)j2--;
					else if(j2==2 && i2!=0)i2--;
					else if(i2==2 && j2!=2)j2++;
					else if(j2==0 && i2!=2)i2++;
					
					if(sense==1){
						aux2=pos[n2][j2][i2];
						pos[n2][j2][i2]=aux1;
					}
					else{
						aux2=pos[n2][i2][j2];
						pos[n2][i2][j2]=aux1;
					}
					
					if(s1==1)SaveRot(aux1);
				}	
			}
		}
	
	//	
	}
		
	public void verificar(){
		for(int k=0; k<6; k++){
			for(int i=0 ; i<3 ; i++){
				for(int j=0; j<3; j++){
					
					if(k==0){
						//pos[i][j][0];
					}
					
				}
			}
		}
		
	}
	
	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		gl.glClearColor(0.0f, 0.5f, 0.5f, 1.0f);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);  
		gl.glLoadIdentity();
	 
		tamanhoCubo = -20 + OpcoesActivity.tamanho; //MODIFICADO!
		
		gl.glTranslatef(0.0f,0.0f, tamanhoCubo);
		
		xdist=0;
		ydist=0;
		zdist=0;
		
		Log.d("TESTE", "TESTE");

			if(Angle>=0 && sense==-1){
				Angle*=-1;
			}
			
			if(MagicCubeActivity.ativar){
				
				if(angleTest - angleTestAux < -2) angleTest -= inc;	
				if(angleTest - angleTestAux > 2) angleTest += inc;
				
				if(angleTest2 - angleTest2Aux < -2) angleTest2 -= inc;
				if(angleTest2 - angleTest2Aux > 2) angleTest2 += inc;
				
				inc -= 0.1;
				
				if(inc < 0.5 ){
					MagicCubeActivity.ativar = false;
					Log.d("teste","zerou");
					inc = 5;
				}
			}
			gl.glRotatef(angleTest ,0, 1 ,0);
			gl.glRotatef(angleTest2, 1, 0, 0);

			if(sinal<0)sinal=-sinal;
			k=0;
			for( n=0 ; n<3 ; n++ ){
				if(rot==0 && n==0){
					gl.glTranslatef(-xdist, -ydist, -zdist);
					gl.glRotatef(Angle, 0, 1, 0);
					gl.glTranslatef(xdist, ydist, zdist);
					e=0;
				}
				if(rot==6 && n==1){
					gl.glTranslatef(-xdist, -ydist, -zdist);
					gl.glRotatef(Angle, 0, 1, 0);
					gl.glTranslatef(xdist, ydist, zdist);
					e=1;
				}	
				if(rot==1 && n==2){
					gl.glTranslatef(-xdist, -ydist, -zdist);
					gl.glRotatef(Angle, 0, 1, 0);
					gl.glTranslatef(xdist, ydist, zdist);
					e=2;
				}						
				gl.glTranslatef(0.0f, sinal*dist , 0.0f);
				ydist+=sinal*dist;
				if(n>0){
					gl.glTranslatef(-dist, 0.0f, -dist);
					xdist+=-dist;
					zdist+=-dist;
				}
				gl.glTranslatef(0.0f , 0.0f , -2*dist);
				gl.glTranslatef(dist , 0.0f , 0.0f);
				zdist+=-2*dist;
				xdist+=dist;
				for( i=0 ; i<3 ; i++){
					if(rot==2 && i==0){
						gl.glTranslatef(-xdist, -ydist, -zdist);
						gl.glRotatef(Angle, 0, 0, 1);
						gl.glTranslatef(xdist, ydist, zdist);
						e=0;
					}
					if(rot==7 && i==1){
						gl.glTranslatef(-xdist, -ydist, -zdist);
						gl.glRotatef(Angle, 0, 0, 1);
						gl.glTranslatef(xdist, ydist, zdist);
						e=1;
					}
					if(rot==3 && i==2){
						gl.glTranslatef(-xdist, -ydist, -zdist);
						gl.glRotatef(Angle, 0, 0, 1);
						gl.glTranslatef(xdist, ydist, zdist);
						e=2;
					}
					gl.glTranslatef(0.0f , 0.0f , dist);
					zdist+=dist;
					gl.glTranslatef(-3*dist , 0.0f , 0.0f);
					xdist+=-3*dist;
					
					for( j=0 ; j<3 ; j++){
						if(rot==4 && j==0){
							gl.glTranslatef(-xdist, -ydist, -zdist);
							gl.glRotatef(Angle, 1, 0, 0);
							gl.glTranslatef(xdist, ydist, zdist);
							e=0;
						}
						if(rot==8 && j==1){
							gl.glTranslatef(-xdist, -ydist, -zdist);
							gl.glRotatef(Angle, 1, 0, 0);
							gl.glTranslatef(xdist, ydist, zdist);
							e=1;
						}
						
						if(rot==5 && j==2){
							gl.glTranslatef(-xdist, -ydist, -zdist);
							gl.glRotatef(Angle, 1, 0, 0);
							gl.glTranslatef(xdist, ydist, zdist);
							e=2;
						}
						gl.glTranslatef(dist , 0.0f , 0.0f);
						xdist+=dist;
						k=pos[j][n][i];
						//
						cubeList.get(k).draw(gl);
						//
						if(rot==5 && j==2){
							gl.glTranslatef(-xdist, -ydist, -zdist);
							gl.glRotatef(-Angle, 1, 0, 0);
							gl.glTranslatef(xdist, ydist, zdist);
						}
						if(rot==8 && j==1){
							gl.glTranslatef(-xdist, -ydist, -zdist);
							gl.glRotatef(-Angle, 1, 0, 0);
							gl.glTranslatef(xdist, ydist, zdist);
							e=1;
						}
						if(rot==4 && j==0){
							gl.glTranslatef(-xdist, -ydist, -zdist);
							gl.glRotatef(-Angle, 1, 0, 0);
							gl.glTranslatef(xdist, ydist, zdist);
						}
					}
					if(rot==3 && i==2){
						gl.glTranslatef(-xdist, -ydist, -zdist);
						gl.glRotatef(-Angle, 0, 0, 1);
						gl.glTranslatef(xdist, ydist, zdist);
					}
					if(rot==7 && i==1){
						gl.glTranslatef(-xdist, -ydist, -zdist);
						gl.glRotatef(-Angle, 0, 0, 1);
						gl.glTranslatef(xdist, ydist, zdist);
						e=1;
					}
					if(rot==2 && i==0){
						gl.glTranslatef(-xdist, -ydist, -zdist);
						gl.glRotatef(-Angle, 0, 0, 1);
						gl.glTranslatef(xdist, ydist, zdist);
					}

				}
				if(n==0)sinal=-sinal;	
				if(rot==1 && n==2){
					gl.glTranslatef(-xdist, -ydist, -zdist);
					gl.glRotatef(-Angle, 0, 1, 0);
					gl.glTranslatef(xdist, ydist, zdist);
				}
				if(rot==6 && n==1){
					gl.glTranslatef(-xdist, -ydist, -zdist);
					gl.glRotatef(-Angle, 0, 1, 0);
					gl.glTranslatef(xdist, ydist, zdist);
					e=1;
				}
				if(rot==0 && n==0){
					gl.glTranslatef(-xdist, -ydist, -zdist);
					gl.glRotatef(-Angle, 0, 1, 0);
					gl.glTranslatef(xdist, ydist, zdist);
				}
		}
	
		
			
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);   //Habilitar na renderiza��o
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);	//Habilitar na renderiza��o
		
		soma=(float) 9.0;
		
		if(Angle==90 || Angle==-90){
			Angle=0;
			save();
			if(!embaralhando){
				rotating=false;	
				rot=20;
			}
			else{
				rot= (int) ((Math.random()*10000)%12)-6;
				if(rot<0){
					rot++;
					rot=-rot;
					sense=-1;
				}
				else{
					sense=1;
				}		
				cont++;
				if(cont==num_embaralhar){ 
					embaralhando=false;
					rotating=false;
					rot=20;
					//cont=0;
				}
			}
			
		}
		
		if(rotating){
			if(Angle>=0)Angle += soma;
			else Angle-=soma;
		}
		
		
		

	}
	
	public void onSurfaceChanged(GL10 gl, int width , int height) {
		
		gl.glViewport(0,0,width,height);
		
		float aspectRatio;
		float zNear = .1f;
		float zFar = 1000;
		fielOfView = 80.0f/57.3f;  
		float size;
		
		gl.glEnable(GL10.GL_NORMALIZE);
		
		aspectRatio = (float) width/height;  
		
		gl.glMatrixMode(GL10.GL_PROJECTION); 
		
		size = zNear * (float)(Math.tan((double)(fielOfView/2.0f))); 
		
		gl.glFrustumf(-size, size, -size/aspectRatio, //VIEWPORT
				size/aspectRatio, zNear, zFar);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);

	}
	
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		
		gl.glDisable(GL10.GL_DITHER);
		//gl.glEnable(GL10.GL_DITHER);
		
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
		
	
		if(mTranslucentBackground){
			gl.glClearColor(0, 0, 0, 0);
		}else{
			gl.glClearColor(1, 1, 1, 1);
		}
		
		gl.glCullFace(GL10.GL_BACK);
		gl.glEnable(GL10.GL_POINT_SMOOTH);
		gl.glEnable(GL10.GL_CULL_FACE);
		//gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glShadeModel(GL10.GL_FLAT); //Mostra apenas a cor do �ltimo vertice especificado.
		gl.glEnable(GL10.GL_DEPTH_TEST);
		
		gl.glClearDepthf(1f);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		
		//initLighting(gl);
	}
	
//	private void initLighting(GL10 gl){
//		float[] diffuse = {1.0f, 1.0f, 1.0f, 1.0f}; //1
//		float[] pos = {0.0f, 10.0f, -3.0f, 1.0f}; //2
//		gl.glLightfv(SS_SUNLIGHT, GL10.GL_POSITION, Cube.makeFloatBuffer(pos)); //3
//		gl.glLightfv(SS_SUNLIGHT, GL10.GL_DIFFUSE, Cube.makeFloatBuffer(diffuse)); //4
//		gl.glShadeModel(GL10.GL_SMOOTH); //5
//		gl.glEnable(GL10.GL_LIGHTING); //6
//		gl.glEnable(SS_SUNLIGHT); //7
//	}
}
