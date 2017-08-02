package com.as.chain.activity.battle;

import java.util.concurrent.atomic.AtomicReference;

import com.as.chain.ui.BattleHero;
import com.js.thread.ThreadHandler;
import com.js.thread.ThreadUtil;

import android.os.SystemClock;
import android.view.View;

public class GridHolder {
	public View view;
	public int x;
	public int y;
	public int width;
	public int height;
	
	public BattleHero hero;
	
	public static final long FRAME_INTERVAL = 30;
	
	public void moveTo(final int x, final int y, long offset,
				final long duration, final Runnable callback) {
		
		final long startTime = SystemClock.elapsedRealtime() + offset;
		final long endTime = startTime + duration;
		final int startX = (int) hero.getX();
		final int startY = (int) hero.getY();
		final int deltaX = x - startX;
		final int deltaY = y - startY;
		
		final AtomicReference<ThreadHandler> handler = new AtomicReference<ThreadHandler>();
		handler.set(ThreadUtil.getMain().run(new Runnable() {
			public void run() {
				long now = SystemClock.elapsedRealtime();
				
				if (now >= endTime) {
					hero.setX(x);
					hero.setY(y);
					
					if (callback != null) {
						callback.run();
					}
					
					handler.get().cancel();
					return;
				}
				
				long dur = now - startTime;
				
				hero.setX(startX + deltaX * dur / duration);
				hero.setY(startY + deltaY * dur / duration);
			}
		}, offset, FRAME_INTERVAL));
	}
}
