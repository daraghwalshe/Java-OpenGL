
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.FPSAnimator;

/**
 * 
 * @author Daragh Walshe B00064428
 * Computer Graphics Assignment 
 * April 2015
 * Lectured : Simon McLoughlin
 * 
 */
public class Barn3D implements GLEventListener, KeyListener, ActionListener {

	GLProfile glp;
	GLCapabilities caps;
	GLCanvas canvas;
	GLU glu;

	private float eyeX = 25, eyeY = 36, eyeZ = 35;
	private boolean cameraRotating = false;
	private float scaleInc = 0.01f;
	private float moveX = 0, moveY = 0, moveZ = 0;
	private float scaledSize = 1.0f;
	private int rotX = 0, rotY = 0, rotZ = 0;
	private float theta = 0.015f, rAngle = 0.0f;	
	private GL2 gl;

	//======================================================
	private int doorTexture;
	private int roofTexture;
	private int woodTexture;
	private int grassTexture;
	private int stoneTexture;
	private int windowTexture;
	private int moonTexture;
	private TextureReader.Texture texture = null;

	private float doorAngle = 0.0f;
	private boolean doorOpen = false;
	private boolean rendererOn = true;
	private boolean lightingOn = true;
	private JFrame frame;

	//menu items
	private JMenuItem  lightOn, lightOff, textureOn, 
	textureOff, rotateOn, rotateOff, exit;

	private JMenuItem  toggleWhite, toggleRed, toggleGreen, toggleBlue;
	private boolean whiteOn = true, redOn = true, greenOn = true, blueOn = true;

	//======================================================

	/**
	 * Main method to start client GUI app.
	 * @param args
	 */
	public static void main(String args[]){
		//set the look and feel to 'Nimbus'
		try{
			for(LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()){
				if("Nimbus".equals(info.getName())){
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		}
		catch(Exception e){
		}
		new Barn3D();
	}//end main

	/**
	 * Create the GUI, add the drawing canvass and start the animation
	 */
	public Barn3D()
	{
		glp = GLProfile.getDefault();
		caps = new GLCapabilities(glp);
		canvas = new GLCanvas(caps);
		glu = new GLU();

		frame = new JFrame("Graphics assignment 1");
		frame.setLayout(new BorderLayout());
		frame.setBounds(250, 150, 1000, 750);

		//create the menu bar
		JMenuBar guiMenuBar = buildMenuBar();
		frame.setJMenuBar(guiMenuBar);
		frame.add(getInfoPanel(), BorderLayout.EAST);

		frame.setVisible(true);
		frame.add(canvas, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		canvas.addGLEventListener(this);
		canvas.addKeyListener(this);
		canvas.requestFocus();
		Animator animator = new FPSAnimator(canvas,60);
		animator.add(canvas);
		animator.start();
	}

	/**
	 * Build a panel with our keyboard controls
	 * for the user to see
	 * @return
	 */
	private JPanel getInfoPanel() {
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
		String line = "___________________";
		Font titleFont = new Font("Meiryo", Font.BOLD, 16);
		Font normalFont = new Font("Meiryo", Font.PLAIN, 14);	
		infoPanel.setBackground(Color.BLACK);
		infoPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 30, 20));

		infoPanel.add(getLabel("Keyboard Controls", titleFont, Color.YELLOW));
		infoPanel.add(getLabel(line, normalFont, Color.WHITE));

		infoPanel.add(getLabel("", normalFont, Color.WHITE));
		infoPanel.add(getLabel(" ", normalFont, Color.WHITE));
		infoPanel.add(getLabel("Move : W, S, A, & D", normalFont, Color.WHITE));
		infoPanel.add(getLabel("Rotate : R, F & V", normalFont, Color.WHITE));

		infoPanel.add(getLabel("Scale Up : U", normalFont, Color.WHITE));
		infoPanel.add(getLabel("Scale Down : I", normalFont, Color.WHITE));
		infoPanel.add(getLabel("Reset Scale : Y", normalFont, Color.WHITE));

		infoPanel.add(getLabel("Rotate Camera : X", normalFont, Color.WHITE));
		infoPanel.add(getLabel("Camera Up : Up Arrow", normalFont, Color.WHITE));
		infoPanel.add(getLabel("Camera Down : Down Arrow", normalFont, Color.WHITE));

		infoPanel.add(getLabel(" ", normalFont, Color.WHITE));

		return infoPanel;
	}

	private JLabel getLabel(String text, Font font, Color fontColour) {	
		JLabel thisLabel = new JLabel(text);
		thisLabel.setFont(font);
		thisLabel.setBackground(Color.BLACK);
		thisLabel.setForeground(fontColour);
		thisLabel.setOpaque(true);
		return thisLabel;
	}
	//---------------------------------------------------------------------------
	/**
	 * Build and return a JMenuBar
	 * @return a JMenuBar
	 */
	private JMenuBar buildMenuBar(){
		JMenuBar optionMenuBar = new JMenuBar();

		JMenu optionMenu = new JMenu(" Options ");
		optionMenuBar.add(optionMenu);
		JMenu colLightMenu = new JMenu(" More Light Options ");
		optionMenuBar.add(colLightMenu);

		//the menu items for the option menu
		lightOn = makeMenuItem(" Turn Lights On");
		lightOff = makeMenuItem(" Turn Lights Off");
		textureOn = makeMenuItem(" Enable Textures");
		textureOff = makeMenuItem(" Disable Textures");
		rotateOn = makeMenuItem(" Start Camera Rotation");
		rotateOff = makeMenuItem(" Stop Camera Rotation");
		exit = makeMenuItem(" Exit");

		//the menu items for the option menu
		toggleWhite = makeMenuItem(" Toggle white light");
		toggleRed = makeMenuItem(" Toggle red light");
		toggleGreen = makeMenuItem(" Toggle green light");
		toggleBlue = makeMenuItem(" Toggle blue light");

		//add the menu items to the menu
		optionMenu.add(lightOn);
		optionMenu.add(lightOff);
		optionMenu.addSeparator();
		optionMenu.add(textureOn);
		optionMenu.add(textureOff);
		optionMenu.addSeparator();
		optionMenu.add(rotateOn);
		optionMenu.add(rotateOff);
		optionMenu.addSeparator();
		optionMenu.add(exit);

		//toggle the coloured lights
		colLightMenu.add(toggleWhite);
		colLightMenu.addSeparator();
		colLightMenu.add(toggleRed);
		colLightMenu.addSeparator();
		colLightMenu.add(toggleGreen);
		colLightMenu.addSeparator();
		colLightMenu.add(toggleBlue);

		return optionMenuBar;

	}//end buildMenuBar

	//method to make a menu item
	private JMenuItem makeMenuItem(String stringIn) {
		
		JMenuItem tempMenuItem = new JMenuItem(stringIn);	
		tempMenuItem.addActionListener(this);
		return tempMenuItem;
		
	}//end makeMenuItem

	//---------------------------------------------------------------------------
	/**
	 * Initialize things like lights and textures
	 * which only need to be referenced on setup
	 */
	public void init(GLAutoDrawable drawable) {	
		System.out.println("... In init ...");
		GL2 gl = drawable.getGL().getGL2();

		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();

		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL); 

		//enable lighting and set up four lights
		gl.glEnable(GL2.GL_LIGHTING);
		gl.glEnable(GL2.GL_LIGHT0);	gl.glEnable(GL2.GL_LIGHT1);
		gl.glEnable(GL2.GL_LIGHT2);	gl.glEnable(GL2.GL_LIGHT3);

		//these values are used to set the colour of our lights
		float [] whiteLight = {1.0f, 1.0f, 1.0f, 1.0f};
		float [] redLight = {1.0f, 0.0f, 0.0f, 1.0f};
		float [] greenLight = {0.0f, 1.0f, 0.0f, 1.0f};
		float [] blueLight = {0.0f, 0.0f, 1.0f, 1.0f};

		//white light
		//note it is the only light used in specular reflection
		float [] whiteLightPosition = {20, 20, 20, 1};
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, whiteLightPosition, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, whiteLight, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, whiteLight, 0);

		//coloured lights
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, redLight, 0);
		gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_DIFFUSE, greenLight, 0);	
		gl.glLightfv(GL2.GL_LIGHT3, GL2.GL_DIFFUSE, blueLight, 0);		

		float [] lightPosition = {22, 22, 22, 1,   21, 21, 21, 1,   23, 23, 23, 1};
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, lightPosition, 0);
		gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_POSITION, lightPosition, 4);
		gl.glLightfv(GL2.GL_LIGHT3, GL2.GL_POSITION, lightPosition, 8);


		//initialize the textures for the rendering
		doorTexture = loadTexture("door1.png",gl);
		roofTexture = loadTexture("roof1.png",gl);
		woodTexture = loadTexture("wood1.png",gl);
		grassTexture = loadTexture("grass.png", gl);
		stoneTexture = loadTexture("stone.png", gl);
		windowTexture = loadTexture("window.png", gl);
		moonTexture = loadTexture("moon.png", gl);

		//background colour (black)
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
	}


	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl = drawable.getGL().getGL2();

		gl.glViewport(0, 0, width, height);

		glu.gluPerspective(50.0f, 1.0, 5.0, 80.0);
		//gl.glOrtho(-20.0, 20.0, -20.0, 20.0, 1.0, 90.0);
		//gl.glFrustum(-20.0, 20.0, -10.0, 25.0, 25.0, 100.0);
	}

	//---------------------------------------------------------------------------
	/**
	 * Anything which is subject to change is
	 * referenced in this method
	 * @param drawable
	 */
	public void update(GLAutoDrawable drawable) {

		GL2 gl = drawable.getGL().getGL2();
		if(cameraRotating){
			//System.out.println("Camera rotating");
			rotateCamera();
		}

		//scale the object
		gl.glScalef(scaledSize, scaledSize, scaledSize);

		//rotate
		gl.glRotatef(rAngle, rotX, rotY, rotZ);


		gl.glTranslatef(moveX, moveY, moveZ);	

		if(doorOpen){
			if(doorAngle < 90.0f)
				doorAngle += 0.5f;
		}
		else {
			if(doorAngle > 0.0f)
				doorAngle -= 0.5f;
		}

		/**
		 * Turn rendering on / off
		 */
		if(rendererOn){
			gl.glEnable(GL.GL_TEXTURE_2D);
		}
		else {
			gl.glDisable(GL.GL_TEXTURE_2D);
		}

		/**
		 * turn lighting on / off
		 */
		if(lightingOn){
			//System.out.println("lights on");
			gl.glEnable(GL2.GL_LIGHTING);
		}
		if(!lightingOn){
			//System.out.println("lights off");
			gl.glDisable(GL2.GL_LIGHTING);
		}

		if(whiteOn){
			gl.glEnable(GL2.GL_LIGHT0);
		}else{
			gl.glDisable(GL2.GL_LIGHT0);
		}
		if(redOn){
			gl.glEnable(GL2.GL_LIGHT1);
		}else{
			gl.glDisable(GL2.GL_LIGHT1);
		}
		if(greenOn){
			gl.glEnable(GL2.GL_LIGHT2);
		}else{
			gl.glDisable(GL2.GL_LIGHT2);
		}
		if(blueOn){
			gl.glEnable(GL2.GL_LIGHT3);
		}else{
			gl.glDisable(GL2.GL_LIGHT3);
		}
		
	}//end update

	/**
	 * Main display method
	 */
	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();	
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		//the base colour of all materials
		//under the textures
		gl.glColor3f(0.2f, 0.2f, 0.2f);

		//make sure we are in model_view mode
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();

		//Controls the camera position and direction
		glu.gluLookAt(eyeX,eyeY,eyeZ, 0,0,0, 0,1,0);

		//not a grassy knoll
		//what we draw in this function will not be updated
		gl.glBindTexture(GL.GL_TEXTURE_2D, grassTexture);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(15.0f, -0.02f, -15.0f);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(-15.0f, -0.02f, -15.0f);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(-15.0f, -0.02f, 15.0f);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(15.0f, -0.02f, 15.0f);
		gl.glEnd();

		update(drawable);
		drawBarn(drawable);
	}

	/**
	 * Draw all the main polygons which make the barn
	 * @param drawable
	 */
	public void drawBarn(GLAutoDrawable drawable){
		gl = drawable.getGL().getGL2();	
		////////////////////////////////////////////////////////////////////
		//door
		gl.glPushMatrix();
		gl.glTranslatef(0f, 0f, 4.0f);
		gl.glRotatef(doorAngle, 0f, 1.0f, 0f);
		gl.glTranslatef(0f, 0f, -4.0f);
		gl.glBindTexture(GL.GL_TEXTURE_2D, doorTexture);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glNormal3f(0.0f, 0.0f, 1.0f);		
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(-3.0f, 0.0f, 4.0f);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(0.0f, 0.0f, 4.0f);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(0.0f, 7.0f, 4.0f);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(-3.0f, 7.0f, 4.0f);
		gl.glEnd();
		gl.glPopMatrix();
		/////////////////////////////////////////////////////////////////////////		
		//window
		gl.glBindTexture(GL.GL_TEXTURE_2D, windowTexture);
		gl.glPushMatrix();
		gl.glBegin(GL2.GL_POLYGON);
		gl.glNormal3f(0.0f, 0.0f, 1.0f);	
		gl.glTexCoord2f(0.0f, 0.0f);	
		gl.glVertex3f(3.0f, 3.0f, 4.01f);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(5.0f, 3.0f, 4.01f);	
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(5.0f, 7.0f, 4.01f);	
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(3.0f, 7.0f, 4.01f);		
		gl.glEnd();
		gl.glPopMatrix();
		/////////////////////////////////////////////////////////////////////////
		//North
		gl.glBindTexture(GL.GL_TEXTURE_2D, stoneTexture);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glNormal3f(0.0f, 0.0f, 1.0f);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(-8.0f, 0.0f, 4.0f);		
		gl.glTexCoord2f(0.3f, 0.0f);
		gl.glVertex3f(-3.0f, 0.0f, 4.0f);
		gl.glTexCoord2f(0.3f, 0.7f);
		gl.glVertex3f(-3.0f, 7.0f, 4.0f);			
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(-8.0f, 10.0f, 4.0f);
		gl.glEnd();

		gl.glBegin(GL2.GL_POLYGON);
		gl.glNormal3f(0.0f, 0.0f, 1.0f);
		gl.glTexCoord2f(0.3f, 0.7f);
		gl.glVertex3f(-3.0f, 7.0f, 4.0f);
		gl.glTexCoord2f(0.5f, 0.7f);
		gl.glVertex3f(0.0f, 7.0f, 4.0f);
		gl.glTexCoord2f(0.5f, 1.0f);
		gl.glVertex3f(0.0f, 10.0f, 4.0f);	
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(-8.0f, 10.0f, 4.0f);
		gl.glEnd();

		gl.glBegin(GL2.GL_POLYGON);
		gl.glNormal3f(0.0f, 0.0f, 1.0f);	
		gl.glTexCoord2f(0.5f, 0.0f);	
		gl.glVertex3f(0.0f, 0.0f, 4.0f);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(8.0f, 0.0f, 4.0f);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(8.0f, 10.0f, 4.0f);
		gl.glTexCoord2f(0.5f, 1.0f);
		gl.glVertex3f(0.0f, 10.0f, 4.0f);
		gl.glEnd();

		//South
		gl.glBegin(GL2.GL_POLYGON);
		gl.glNormal3f(0.0f, 0.0f, -1.0f);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(8.0f, 0.0f, -4.0f);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(-8.0f, 0.0f, -4.0f);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(-8.0f, 10.0f, -4.0f);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(8.0f, 10.0f, -4.0f);
		gl.glEnd();

		//East
		gl.glBegin(GL2.GL_POLYGON);
		gl.glNormal3f(1.0f, 0.0f, 0.0f);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(8.0f, 0.0f, 4.0f);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(8.0f, 0.0f, -4.0f);
		gl.glTexCoord2f(1.0f, 0.714f);
		gl.glVertex3f(8.0f, 10.0f, -4.0f);	
		gl.glTexCoord2f(0.5f, 1.0f);
		gl.glVertex3f(8.0f, 14.0f, 0.0f);
		gl.glTexCoord2f(0.0f, 0.714f);
		gl.glVertex3f(8.0f, 10.0f, 4.0f);
		gl.glEnd();

		//West
		gl.glBegin(GL2.GL_POLYGON);
		gl.glNormal3f(-1.0f, 0.0f, 0.0f);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(-8.0f, 0.0f, -4.0f);//--1
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(-8.0f, 0.0f, 4.0f);//--6	
		gl.glTexCoord2f(1.0f, 0.714f);
		gl.glVertex3f(-8.0f, 10.0f, 4.0f);//--7
		gl.glTexCoord2f(0.5f, 1.0f);
		gl.glVertex3f(-8.0f, 14.0f, 0.0f);//--8
		gl.glTexCoord2f(0.0f, 0.714f);
		gl.glVertex3f(-8.0f, 10.0f, -4.0f);//--9
		gl.glEnd();

		//Floor
		gl.glBindTexture(GL.GL_TEXTURE_2D, woodTexture);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glNormal3f(0.0f, 1.0f, 0.0f);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(-8.0f, 0.0f, -4.0f);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(8.0f, 0.0f, -4.0f);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(8.0f, 0.0f, 4.0f);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(-8.0f, 0.0f, 4.0f);
		gl.glEnd();

		//N-Roof
		gl.glBindTexture(GL.GL_TEXTURE_2D, roofTexture);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glNormal3f(-0f, 0.70f, 0.70f);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(-8.0f, 10.0f, 4.0f);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(8.0f, 10.0f, 4.0f);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(8.0f, 14.0f, 0.0f);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(-8.0f, 14.0f, 0.0f);
		gl.glEnd();
		//S-Roof
		gl.glBegin(GL2.GL_POLYGON);
		gl.glNormal3f(0f, 0.70f, -0.70f);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(8.0f, 10.0f, -4.0f);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(-8.0f, 10.0f, -4.0f);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(-8.0f, 14.0f, 0.0f);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(8.0f, 14.0f, 0.0f);
		gl.glEnd();

		drawSphere(drawable);
	}

	/**
	 * Draw some extra objects for decoration
	 * @param drawable
	 */
	public void drawSphere(GLAutoDrawable drawable){
		//draw the moon
		gl.glPushMatrix();
		gl.glTranslatef(10.0f, 24.0f, -3.0f);
		gl.glBindTexture(GL.GL_TEXTURE_2D, moonTexture);
		GLUquadric qobj0 = glu.gluNewQuadric();
		glu.gluQuadricTexture(qobj0, true);
		glu.gluSphere(qobj0, 2, 30, 30);	
		gl.glPopMatrix();

		//draw a rain-barrel
		gl.glPushMatrix();
		gl.glBindTexture(GL.GL_TEXTURE_2D, woodTexture);
		gl.glTranslatef(-9f, 0f, 3f);
		gl.glRotatef(270, 1, 0, 0);
		GLUquadric qobj1 = glu.gluNewQuadric();
		glu.gluQuadricNormals(qobj1, doorTexture);
		glu.gluQuadricTexture(qobj1, true);
		glu.gluCylinder( qobj1, 0.75f,0.75f, 3.0f, 10, 2 );
		gl.glPopMatrix();
	}

	/**
	 * all our keyboard commands are handled here
	 */
	@Override
	public void keyPressed(KeyEvent key) {
		//Toggle camera rotation
		if (key.getKeyCode() == KeyEvent.VK_X){
			cameraRotating = !cameraRotating;
		}
		//Move the viewer up 
		if (key.getKeyCode() == KeyEvent.VK_UP){
			eyeY += 0.5f;
		}
		//Move the viewer down
		else if (key.getKeyCode() == KeyEvent.VK_DOWN){
			eyeY -= 0.5f;
		}
		//Rotate Object about X-axis
		else if(key.getKeyCode() == KeyEvent.VK_R){
			rotX = 1; rotY = 0; rotZ = 0;
			rAngle += 1.0f;
		}
		//Rotate Object about Y-axis
		else if(key.getKeyCode() == KeyEvent.VK_F){
			rotY = 1; rotZ = 0; rotX = 0; 
			rAngle += 1.0f;
		}
		//Rotate Object about Z-axis
		else if(key.getKeyCode() == KeyEvent.VK_V){
			rotY = 0; rotZ = 1; rotX = 0; 
			rAngle += 1.0f;
		}

		//Move object forward
		else if(key.getKeyCode() == KeyEvent.VK_W){
			moveZ -= 0.2;
		}
		else if(key.getKeyCode() == KeyEvent.VK_S){
			moveZ += 0.2;
		}
		else if(key.getKeyCode() == KeyEvent.VK_A){
			moveX -= 0.2;
		}
		else if(key.getKeyCode() == KeyEvent.VK_D){
			moveX += 0.2;
		}

		//Scale the object up
		else if(key.getKeyCode() == KeyEvent.VK_U){
			System.out.println("U key pressed");
			scaledSize += scaleInc;
		}
		//Scale the object down
		else if(key.getKeyCode() == KeyEvent.VK_I){
			System.out.println("I key pressed");
			scaledSize -= scaleInc;
		}
		//Reset the objects size
		else if(key.getKeyCode() == KeyEvent.VK_Y){
			System.out.println("Y key pressed");
			scaledSize = 1.0f;
		}

		//Open the door
		else if(key.getKeyCode() == KeyEvent.VK_O){
			System.out.println("O key pressed");
			doorOpen = true;
		}
		//Close the door
		else if(key.getKeyCode() == KeyEvent.VK_C){
			System.out.println("C key pressed");
			doorOpen = false;
		}	
	}//end keyPressed
	//------------------------------------------------------

	@Override
	public void keyReleased(KeyEvent arg0) {
	}
	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	/**
	 * A simple function to rotate the camera about the origin
	 */
	public void rotateCamera() {
		float sinTh = (float) Math.sin(theta);
		float cosTh = (float) Math.cos(theta);

		float tempX = eyeX;	
		eyeX = (eyeX)*cosTh - (eyeZ)*sinTh;//x
		eyeZ = (tempX)*sinTh + (eyeZ)*cosTh;//z
	}

	public void dispose(GLAutoDrawable drawable) {	
	}
	
	/**
	 * Event handling for the menu bar
	 */
	public void actionPerformed(ActionEvent e) {

		if(e.getSource() == lightOn){
			lightingOn = true;
		}
		else if(e.getSource() == lightOff){
			lightingOn = false;
		}
		else if(e.getSource() == textureOn){
			rendererOn = true;
		}
		else if(e.getSource() == textureOff){
			rendererOn = false;
		}
		else if(e.getSource() == rotateOn){
			cameraRotating = true;
		}
		else if(e.getSource() == rotateOff){
			cameraRotating = false;
		}
		else if(e.getSource() == exit){
			System.exit(0);
		}

		if(e.getSource() == toggleWhite){
			whiteOn = !whiteOn;
		}
		else if(e.getSource() == toggleRed){
			redOn = !redOn;
		}
		else if(e.getSource() == toggleGreen){
			greenOn = !greenOn;
		}
		else if(e.getSource() == toggleBlue){
			blueOn = !blueOn;
		}
	}
		
	//=======================================================================

	//Code for texture handling below, provided by lecturer

	private void makeRGBTexture(GL gl, GLU glu, TextureReader.Texture img, 
			int target, boolean mipmapped) {

		//the function glTexImage2D (below) loads the texture into video memory, once loaded we can free
		//the image memory (in java the garbage collector will do this).
		//Parameter 1:	GL_TEXTURE_2D -> inform openGL the texture is a 2D texture
		//Parameter 2:	0-> Should remain 0 for us, used for building texture maps of different sizes
		//				0 means just use the texture map in its current form
		//Parameter 3:	GL_RGB->inform openGL the image is an RGB image (could also be GL_LUMINANCE)
		//Parameter 4:	im_size-> user defined variable that defines the number of rows in the texture
		//Parameter 5:	im_size-> user defined variable that defines the number of columns in the texture
		//Parameter 6:	0-> sets the border of the texture (0 indicates no border)
		//Parameter 7:	GL_RGB-> Specifies the type of texels to be used
		//Parameter 8:	GL_UNSIGNED_BYTE-> Identifies the size the texels to be used 
		//				GL_UNSIGNED_BYTE means 1 byte for each colour channel or 24 bit colour texels
		//Parameter 9:	image-> the actual raw image pixel values to become texel values

		if (mipmapped) {
			glu.gluBuild2DMipmaps(target, GL.GL_RGB8, img.getWidth(), 
					img.getHeight(), GL.GL_RGB, GL.GL_UNSIGNED_BYTE, img.getPixels());
		} else {
			gl.glTexImage2D(target, 0, GL.GL_RGB, img.getWidth(), 
					img.getHeight(), 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, img.getPixels());
		}
	}

	private int genTexture(GL gl) {
		final int[] tmp = new int[1];
		//glGenTextures generates an unused integer id for the texture were about
		//to create, we can use this to reference the texture in future
		gl.glGenTextures(1, tmp, 0);
		return tmp[0];
	}

	private int loadTexture(String filename, GL gl)
	{
		int tex_id = genTexture(gl);//create an unused id for the texture
		//we must inform openGL that this texture should become the current texture
		//so subsequent texture functions will apply it, like the ones below
		gl.glBindTexture(GL.GL_TEXTURE_2D, tex_id);
		//read in the image
		try {
			texture = TextureReader.readTexture(filename);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		//make an openGL texture from the image
		makeRGBTexture(gl, glu, texture, GL.GL_TEXTURE_2D, false);
		/*glTexParameteri is used to set various propertiers of the texturing procedure
    	all texture properties are set by glTexParameteri

        Each pixel in the rendered image corresponds to a small area on the surface of an object and
    	hence a small area of the texture map. If the object is far away from the viewer a single pixel
    	may be representative of a number of texels. If the object is close to the viewer a single texel 
    	may be representative of a number of pixels. This is called minification and magnification.
    	So openGL must either take the large area of the texture map and squash it into the smaller pixel
    	area or take the small texel area and magnify it so it fits into the larger pixel area.
    	The following function calls tell openGL how to do this

    	GL_NEAREST and GL_LINEAR are two techniqes used for magnification/minification.
    	GL_NEAREST is faster but GL_LINEAR normally yields better results*/

		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);

		/* when using the 2D texture defined above, if the s texture coordinate goes outside of the
    	texture range (0->1) then clamp it, that is if its greater than 1 set it to one
    	if it is less than zeros set it to zero. Other option-> GL_REPEAT repeats the texure again
    	outside of the map bounds (0->1)*/

		gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
		//same goes for the t texture coordinate
		gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
		return tex_id;

	}	

	//=======================================================================


}//end class





