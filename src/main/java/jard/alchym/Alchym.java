package jard.alchym;

import jard.alchym.init.InitAlchym;
import jard.alchym.proxy.ClientProxy;
import jard.alchym.proxy.Proxy;
import jard.alchym.proxy.ServerProxy;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;

/***
 *  Alchym
 *  Main mod initializer.
 *
 *  Created by jard at 12:21 AM on ‎December ‎19, ‎2018.
 ***/
public class Alchym implements ModInitializer {
	private static final InitAlchym alchymContent = new InitAlchym ();
	private static final Proxy proxy;
	static {
		proxy = FabricLoader.getInstance ().getEnvironmentType () == EnvType.CLIENT ? new ClientProxy () : new ServerProxy ();
	}

	@Override
	public void onInitialize () {
		alchymContent.initialize ();
	}

	public static Item getPhilosophersStone () {
		return alchymContent.getPhilosophersStone ();
	}

	public static InitAlchym content () {
		return alchymContent;
	}
	public static Proxy getProxy () { return proxy; }
}
