package jard.alchym;

import jard.alchym.init.InitAlchym;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;

/***
 *  Alchym.java
 *  Main mod initializer.
 *
 *  Created by jard at 12:21 AM on ‎December ‎19, ‎2018.
 ***/

public class Alchym implements ModInitializer {
	private static InitAlchym alchymContent = new InitAlchym ();

	@Override
	public void onInitialize () {
		alchymContent.initialize ();
	}

	public static Item getPhilosophersStone () {
		return alchymContent.getPhilosophersStone ();
	}
}
