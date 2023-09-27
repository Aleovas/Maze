package com.aleovas.maze;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.utils.Logger;

import java.util.Random;

public class MazeGame extends Game {
	Maze maze;
	Body ball, target;
	public static float zoom=10;
	float radius=9f/zoom;
	public static final float wallWidth=40/zoom;
	public static final float wallHeight=40/zoom;
	public static float worldWidth=720/zoom;
	public static float worldHeight=1280/zoom;
	public int nodeX=26*3/4;
	public int nodeY=44*3/4;
	final float STEP_TIME=1f/300f;
	public ShapeRenderer renderer;
	World world;
	Box2DDebugRenderer debugRenderer;
	public float accumulator=0;
	public OrthographicCamera camera;
	Random rnd=new Random();
	MouseJoint joint;
	final float MAX_VELOCITY=radius*20;
	float xCoef=1,yCoef=1;
	@Override
	public void create () {
		world=new World(new Vector2(0,-9), true);
		maze=new Maze(nodeX,nodeY);
		camera=new OrthographicCamera();
		camera.setToOrtho(false,worldWidth,worldHeight);
		for(Maze.Wall w:maze.allWalls){
			//w.render(renderer);
			if(w.isUp)w.addWallBody(world);
		}
		renderer=new ShapeRenderer();
		renderer.setProjectionMatrix(camera.combined);
		debugRenderer=new Box2DDebugRenderer();
		BodyDef bodyDef1 = new BodyDef();
		bodyDef1.type = BodyDef.BodyType.DynamicBody;
		bodyDef1.position.set(rnd.nextInt((int)worldWidth), rnd.nextInt((int)worldHeight));
		Body body = world.createBody(bodyDef1);
		BodyDef bodyDef2 = new BodyDef();
		bodyDef2.type = BodyDef.BodyType.KinematicBody;
		bodyDef2.position.set(rnd.nextInt((int)worldWidth), rnd.nextInt((int)worldHeight));
		Body targetBody = world.createBody(bodyDef2);

		CircleShape circle = new CircleShape();
		circle.setRadius(radius);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = 0.12f;
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = 0.6f;
		body.createFixture(fixtureDef);
		targetBody.createFixture(circle,0);
		circle.dispose();
		body.setSleepingAllowed(false);
		ball=body;
		target=targetBody;
		while(abs(target.getPosition().sub(ball.getPosition()))<abs(new Vector2(worldWidth,wallHeight))/2.5){
			target.setTransform(rnd.nextInt((int)worldWidth), rnd.nextInt((int)worldHeight),0);
		}
		xCoef=worldWidth/(float)Gdx.graphics.getWidth();
        yCoef=worldHeight/(float)Gdx.graphics.getHeight();


		BodyDef def1=new BodyDef();
		def1.position.set(0,0);
		BodyDef def2=new BodyDef();
		def2.position.set(0,0);
		BodyDef def3=new BodyDef();
		def3.position.set(worldWidth,0);
		BodyDef def4=new BodyDef();
		def4.position.set(0,worldHeight);
		Body wall1=world.createBody(def1);
		Body wall2=world.createBody(def1);
		Body wall3=world.createBody(def1);
		Body wall4=world.createBody(def1);
		EdgeShape line1=new EdgeShape();
		line1.set(0,0,worldWidth*3,0);
		wall1.createFixture(line1,0);
		EdgeShape line2=new EdgeShape();
		line2.set(0,0,0,worldHeight*3);
		wall2.createFixture(line2,0);
		EdgeShape line3=new EdgeShape();
		line3.set(0,0,0,worldHeight);
		wall3.createFixture(line3,0);
		EdgeShape line4=new EdgeShape();
		line4.set(0,0,worldWidth,0);
		wall4.createFixture(line4,0);
//		MouseJointDef jointDef=new MouseJointDef();
//        jointDef.target.set(50,57);
//        //jointDef.bodyA=wall1;
//        jointDef.bodyB=ball;
//        jointDef.collideConnected=true;
//        jointDef.dampingRatio=0;
//        jointDef.maxForce=1000000*ball.getMass();
//        joint=(MouseJoint)world.createJoint(jointDef);
//        joint.setTarget(new Vector2(Gdx.input.getX(),Gdx.input.getY()));

	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		renderer.setColor(1,1,1,1);
		//debugRenderer.render(world, camera.combined);
		renderer.begin(ShapeRenderer.ShapeType.Line);
		for(Maze.Wall w:maze.allWalls){
			w.render(renderer);
		}
		renderer.end();
		renderer.begin(ShapeRenderer.ShapeType.Filled);
		renderer.setColor(1,0.2f,0,1);
		renderer.circle(ball.getPosition().x,ball.getPosition().y,radius);
		renderer.setColor(0,0.2f,1,1);
		renderer.circle(target.getPosition().x,target.getPosition().y,radius);
		renderer.end();
		//Gdx.app.log("coords",Gdx.input.getAccelerometerX()+", "+Gdx.input.getAccelerometerY()+", "+Gdx.input.getAccelerometerZ());
		Vector2 velocity=ball.getLinearVelocity();
        Vector2 position=ball.getPosition();
        if(Gdx.input.isKeyPressed(Input.Keys.A)&&velocity.x>-MAX_VELOCITY){
            ball.applyLinearImpulse(-8,0,position.x,position.y,true);
            //Gdx.app.log("Impulse","Applied");
        }
        if(Gdx.input.isKeyPressed(Input.Keys.D)&&velocity.x<MAX_VELOCITY)ball.applyLinearImpulse(8,0,position.x,position.y,true);
        if(Gdx.input.isKeyPressed(Input.Keys.S)&&velocity.x<MAX_VELOCITY)ball.applyLinearImpulse(0,-8,position.x,position.y,true);
        if(Gdx.input.isKeyPressed(Input.Keys.W)&&velocity.y<MAX_VELOCITY)ball.applyLinearImpulse(0,8,position.x,position.y,true);

//        if(Gdx.input.isTouched()){
//            joint.setTarget(new Vector2(Gdx.input.getX()*xCoef,worldHeight-Gdx.input.getY()*yCoef));
//            joint.setMaxForce(1000*ball.getMass());
//            //Gdx.app.log("position",Gdx.input.getX()*xCoef+","+(Gdx.input.getY()*yCoef));
//        }else{
//            joint.setTarget(ball.getPosition());
//            joint.setMaxForce(0);
//        }
        //ball.applyLinearImpulse(0.5f*Math.round(Gdx.input.getAccelerometerY()),-0.5f*Math.round(Gdx.input.getAccelerometerX()),position.x,position.y,true);
		doPhysicsStep(Gdx.graphics.getDeltaTime());
	}
	private void doPhysicsStep(float delta){
		accumulator+=Math.min(delta, 0.25f);
		//Gdx.app.log("delta",delta+"");
		while(accumulator>=STEP_TIME){
			accumulator-=STEP_TIME;
			if(Gdx.app.getType()!= Application.ApplicationType.Desktop)
				world.setGravity(new Vector2(-5*Gdx.input.getAccelerometerX(),
						-5*Gdx.input.getAccelerometerY()));
			world.step(STEP_TIME,6,2);  //The velocity and position steps are just the rec. values
		}
		if(collides(ball,target)){
			Gdx.app.log("collision","resetting");
//			maze=new Maze(nodeX,nodeY);
//			ball.setTransform(rnd.nextInt((int)worldWidth), rnd.nextInt((int)worldHeight),0);
//			target.setTransform(rnd.nextInt((int)worldWidth), rnd.nextInt((int)worldHeight),0);
			create();
		}

	}
	public boolean collides(Body b1, Body b2){
		return Math.abs(b1.getPosition().x-b2.getPosition().x)<20&&abs(b1.getPosition().sub(b2.getPosition()))<=radius+1.2;
	}
	public float abs(Vector2 v){
		return (float) Math.sqrt(v.x*v.x+v.y*v.y);
	}

	@Override
	public void dispose () {

	}
}
