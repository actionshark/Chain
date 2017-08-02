package com.as.chain.game;

public class Define {
	public static enum Country {
		Wei("魏", 0xff0000ff),
		Shu("蜀", 0xffff0000),
		Wu("吴", 0xff00bb00),
		Qun("群", 0xffcc9900);
		
		public final String text;
		public final int color;
		
		private Country(String text, int color) {
			this.text = text;
			this.color = color;
		}
	}
	
	public static enum HeroType {
		Normal, Spare;
	}
	
	public static enum Sex {
		Male("男"), Female("女");
		
		public final String text;
		
		private Sex(String text) {
			this.text = text;
		}
	}
	
	public static enum SkillType {
		Active("大"), Normal("普"), Passive("被"), Chain("连");
		
		public final String text;
		
		private SkillType(String text) {
			this.text = text;
		}
	}
	
	public static enum ChainType {
		Fall, Back, Float, Fly;
	}
	
	public static final String MOMENT_GAME_START = "GameStart";
	public static final String MOMENT_GAME_END = "GameEnd";
	
	public static final String MOMENT_ROUND_START = "RoundStart";
	public static final String MOMENT_ROUND_END = "RoundEnd";
	
	public static final String MOMENT_ATTACK = "Attack";
	public static final String MOMENT_DAMAGE = "Damage";
	public static final String MOMENT_DEATH = "Death";
	
	public static final int PHASE_BEFORE = 0;
	public static final int PHASE_AFTER = 2;
}
