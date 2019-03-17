package com.eldonad.ccgalacticraftaddon.utils;

import com.eldonad.ccgalacticraftaddon.CCGalacticraftAddon;
import com.eldonad.ccgalacticraftaddon.utils.messages.SetScheduledStateMessageHandler;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class CCGalacticraftAddonPacketHandler {
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(CCGalacticraftAddon.MODID);
	
	public static void registerMessages() {
		
		INSTANCE.registerMessage(SetScheduledStateMessageHandler.class, SetScheduledStateMessageHandler.SetScheduleStateMessage.class, 1, Side.SERVER);
	}
}
