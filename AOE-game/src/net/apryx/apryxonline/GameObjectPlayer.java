package net.apryx.apryxonline;

import net.apryx.apryxonline.tile.ApryxResources;
import net.apryx.game.NetworkGameObject;
import net.apryx.graphics.SpriteBatch;
import net.apryx.graphics.texture.Sprite;
import net.apryx.input.Input;
import net.apryx.input.Keys;
import net.apryx.input.Mouse;
import net.apryx.math.Mathf;
import net.apryx.time.Time;

public class GameObjectPlayer extends NetworkGameObject{
	
	private float movementSpeed = 64;
	private Sprite sprite;
	
	private boolean isTryCasting = false;
	
	public GameObjectPlayer(float x, float y){
		super(x,y);
		sprite = new Sprite(ApryxResources.player);
		sprite.center();
		sprite.setyOffset(sprite.getHeight());
		sprite.setStraightUp(true);
	}
	
	@Override
	public void render(SpriteBatch batch) {
		super.render(batch);
		
		if(isLocal()){
			batch.color(1, 0, 0);
			batch.depth(0);
			batch.drawRectangleZ(targetX-1, targetY, -1, 2, 2);
			if(isTryCasting){
				batch.color(0.5f, 0.5f, 1);
				batch.depth(0);
				batch.drawRectangleZ(world.getMouseX()-5, world.getMouseY(), -5, 10, 10);
			}
		}
		
		batch.color(1,1,1);
		batch.drawSprite(sprite, x, y, 0, x > targetX ? -1 : 1, 1);
	}
	
	@Override
	public void update() {
		super.update();
		
		if(isLocal()){
			updateLocal();
		}else{
			updateNetwork();
		}
		
	}
	
	public void updateLocal(){
		if(!isTryCasting){
			if(Input.isMouseButtonPressed(Mouse.RIGHT)){
				targetX = world.getMouseX();
				targetY = world.getMouseY();
				setChanged();
			}
			
			if(Input.isKeyPressed(Keys.Q)){
				isTryCasting = true;
			}
		}else{
			if(Input.isMouseButtonPressed(Mouse.RIGHT)){
				isTryCasting = false;
			}
			if(Input.isMouseButtonPressed(Mouse.LEFT)){
				isTryCasting = false;
				//TODO cast the actual spell :)
			}
		}
		
		
		moveToTarget(movementSpeed);
	}
	
	public void moveToTarget(float speed){
		float xDir = targetX - x;
		float yDir = targetY - y;
		
		float l = Mathf.sqrt(xDir * xDir + yDir * yDir);
		if(l < 1f)
			return;
		
		xDir /= l;
		yDir /= l;

		x += xDir * speed * Time.deltaTime;
		y += yDir * speed * Time.deltaTime;
	}
	
	public void updateNetwork(){
		moveToTarget(movementSpeed);
	}
}
