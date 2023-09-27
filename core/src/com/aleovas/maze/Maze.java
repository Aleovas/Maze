package com.aleovas.maze;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by OMAR on 9/2/2017.
 */

public class Maze {
    public Node[][] nodes;
    public ArrayList<Wall> walls=new ArrayList<Wall>();
    public ArrayList<Wall> allWalls=new ArrayList<Wall>();
    int x,y;
    Random rnd;
    public Maze(int x, int y){
        nodes=new Node[x][y];
        this.x=x;
        this.y=y;
        rnd=new Random();
        //Nodes init
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                nodes[i][j]=new Node();
            }
        }
        //Walls init
        int i,j;
        for(i=0;i<x-1;i++){
            for(j=0;j<y-1;j++){
                Wall w1,w2;
                w1=new Wall(nodes[i][j],nodes[i+1][j],false,i+1,j);
                nodes[i][j].wall.add(w1);
                nodes[i+1][j].wall.add(w1);
                w2=new Wall(nodes[i][j],nodes[i][j+1],true,i,j+1);
                nodes[i][j].wall.add(w2);
                nodes[i][j+1].wall.add(w2);
                //walls.add(new Wall(nodes[i][j],nodes[i+1][j]));
                //walls.add(new Wall(nodes[i][j],nodes[i][j+1]));
                allWalls.add(w1);
                allWalls.add(w2);
            }
            Wall w1=new Wall(nodes[i][j],nodes[i+1][j],true,i,j);
            nodes[i][j].wall.add(w1);
            nodes[i+1][j].wall.add(w1);
            allWalls.add(w1);
        }
        for(j=0;j<y-1;j++){
            Wall w2=new Wall(nodes[i][j],nodes[i][j+1],false,i,j);
            nodes[i][j].wall.add(w2);
            nodes[i][j+1].wall.add(w2);
            allWalls.add(w2);
        }

        //Starting node prep.
        Node start=nodes[rnd.nextInt(x)][0];
        start.init=true;
        walls.addAll(start.wall);

        //Maze Generation
        while(walls.size()>0){
            i=rnd.nextInt(walls.size());
            Wall w=walls.get(i);
            if(w.nodes[0].init^w.nodes[1].init){
                Node u=!w.nodes[0].init? w.nodes[0]:w.nodes[1];
                u.init=true;
                walls.addAll(u.wall);
                w.isUp=false;
            }
            walls.remove(i);
        }
    }
    public class Node{
        public byte walls;
        public boolean init=false;
        public ArrayList<Wall> wall=new ArrayList<Wall>();
        public Node(){
            walls=15;
        }
        public boolean isLeftWallUp(){
            return (1|walls)==walls;
        }
        public boolean isUpWallUp(){
            return (2|walls)==walls;
        }
        public boolean isRightWallUp(){
            return (4|walls)==walls;
        }
        public boolean isDownWallUp(){
            return (8|walls)==walls;
        }
    }
    public class Wall{
        public Node[] nodes=new Node[2];
        public boolean isUp=true;
        public boolean horizontal;
        public int x,y;
        int xmod,ymod;
        public Wall(Node n1, Node n2,boolean h, int x, int y){
            nodes[0]=n1;
            nodes[1]=n2;
            horizontal=h;
            this.x=x;
            this.y=y;
            xmod=horizontal?1:0;
            ymod=horizontal?0:1;
        }
        public void render(ShapeRenderer s){
            //s.begin(ShapeRenderer.ShapeType.Line);
            if(isUp)s.line(x*MazeGame.wallWidth,y*MazeGame.wallHeight,
                    x*MazeGame.wallWidth+xmod*MazeGame.wallWidth,
                    y*MazeGame.wallHeight+ymod*MazeGame.wallHeight);
            //s.end();
        }
        public void addWallBody(World world){
            float length=horizontal?MazeGame.wallWidth:MazeGame.wallHeight;
            BodyDef def=new BodyDef();
            def.type= BodyDef.BodyType.KinematicBody;
            def.position.set(x*MazeGame.wallWidth,y*MazeGame.wallHeight);
            Body body=world.createBody(def);
            EdgeShape line=new EdgeShape();
            line.set(0,0, xmod*length,ymod*length);
            body.createFixture(line,0);
            line.dispose();
        }
    }

}
