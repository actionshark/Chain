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
}
