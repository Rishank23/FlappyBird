package com.rishank_reddy.flappybirdgame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {

	SpriteBatch batch;
	Texture backGround, birds[];   // is an image
	Texture topTube, bottomTube;
	Texture gameOver;
	int flapState = 0, gameState = 0, numberOfTubes = 4, score = 0, scoringTube = 0;
	float birdY = 0, velocity = 0, gravity = 2, gap = 400, maxTubeOffset, tubeVelocity = 4;
	float[] tubeX = new float[numberOfTubes];
	float[] tubeOffset = new float[numberOfTubes];
	float distanceBetweenTubes;
	Random randomGenerator;
	Circle birdCircle;
	Rectangle[] topTubeRectangles, bottomTubeRectangles;
//	ShapeRenderer shapeRenderer;
	BitmapFont font;

	@Override
	public void create () {
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

		batch = new SpriteBatch();
		birdCircle = new Circle();
		topTubeRectangles = new Rectangle[numberOfTubes];
		bottomTubeRectangles = new Rectangle[numberOfTubes];
//		shapeRenderer = new ShapeRenderer();

		backGround = new Texture("bg.png");
		gameOver = new Texture("gameover.png");

		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");

		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");
		maxTubeOffset = Gdx.graphics.getHeight()/2 - gap/2 - 100;
		randomGenerator = new Random();
		distanceBetweenTubes = Gdx.graphics.getWidth()*3/4;

		startGame();
	}

	public void startGame(){
		birdY = Gdx.graphics.getHeight()/2 - birds[0].getHeight()/2;

		for (int i=0; i<numberOfTubes; ++i){
			tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f)*(Gdx.graphics.getHeight()-gap - 200);
			tubeX[i] = Gdx.graphics.getHeight()/2 - topTube.getWidth()/2 + Gdx.graphics.getWidth() + i*distanceBetweenTubes;

			topTubeRectangles[i] = new Rectangle();
			bottomTubeRectangles[i] = new Rectangle();
		}

		score = 0;
		scoringTube = 0;
		velocity = 0;
	}

	@Override
	public void render () {

		batch.begin();
		batch.draw(backGround, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if (gameState == 1){

			if(tubeX[scoringTube] < Gdx.graphics.getWidth()/2){
				score++;
				if (scoringTube<numberOfTubes-1){
					scoringTube++;
				}else {
					scoringTube = 0;
				}
				Gdx.app.log("score", String.valueOf(score));
			}

			if (Gdx.input.justTouched()){
				velocity = -30;
			}

			for (int i=0; i<numberOfTubes; ++i) {

				if (tubeX[i] < -topTube.getWidth()){
					tubeX[i] = numberOfTubes*distanceBetweenTubes;
					tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f)*(Gdx.graphics.getHeight()-gap - 200);
				}else{
					tubeX[i] = tubeX[i] - tubeVelocity;
				}

				batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight()/2 + gap/2 + tubeOffset[i]);
				batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight()/2 - gap/2 - bottomTube.getHeight() + tubeOffset[i]);

				topTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight()/2 + gap/2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
				bottomTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight()/2 - gap/2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());
			}

			if (birdY>0){
				velocity += gravity;
				birdY -= velocity;
			}else {
				gameState = 2;
			}
		}else if(gameState == 0){
			if(Gdx.input.justTouched()) {
				gameState = 1;
			}
		}else if(gameState == 2){
			batch.draw(gameOver, Gdx.graphics.getWidth()/2-gameOver.getWidth()/2, Gdx.graphics.getHeight()/2-gameOver.getHeight()/2);
			if(Gdx.input.justTouched()) {
				gameState = 1;
				startGame();
			}
		}

		if (flapState == 0){
			flapState = 1;
		}else{
			flapState = 0;
		}

		batch.draw(birds[flapState], Gdx.graphics.getWidth()/2 - birds[flapState].getHeight()/2, birdY);
		font.draw(batch, String.valueOf(score), 100, 200);
		batch.end();

		birdCircle.set(Gdx.graphics.getWidth()/2, birdY + birds[flapState].getHeight()/2, birds[flapState].getWidth()/2);

//		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//		shapeRenderer.setColor(Color.RED);
//		shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);

		for (int i=0; i<numberOfTubes; ++i){
//			shapeRenderer.rect(topTubeRectangles[i].x, topTubeRectangles[i].y, topTubeRectangles[i].width, topTubeRectangles[i].height);
//			shapeRenderer.rect(bottomTubeRectangles[i].x, bottomTubeRectangles[i].y, bottomTubeRectangles[i].width, bottomTubeRectangles[i].height);

			if (Intersector.overlaps(birdCircle, topTubeRectangles[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangles[i])){
				gameState = 2;
			}
		}

//		shapeRenderer.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		backGround.dispose();
		topTube.dispose();
		bottomTube.dispose();
		birds[0].dispose();
		birds[1].dispose();
	}
}
