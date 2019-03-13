package com.eldonad.ccgalacticraftaddon;

import com.eldonad.ccgalacticraftaddon.machines.autolauncherinterface.AutoLaunchInterfaceBlock;
import com.eldonad.ccgalacticraftaddon.machines.autolauncherinterface.TileEntityAutoLaunchInterface;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import dan200.computercraft.api.ComputerCraftAPI;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

@Mod(modid=CCGalacticraftAddon.MODID, version = CCGalacticraftAddon.VERSION, dependencies="required-after:GalacticraftCore; required-after:GalacticraftMars; required-after:ComputerCraft")
public class CCGalacticraftAddon {
	public static final String GC_CORE_MODID = "GalacticraftCore";
	public static final String MODID = "ccgalacticraftaddon";
	public static final String VERSION = "0.1a";
	public static CCGalacticraftAddonPeripheralProvider provider = new CCGalacticraftAddonPeripheralProvider();
	
	public static Block autoLauncherInterfaceBlock = new AutoLaunchInterfaceBlock();
	public static ItemBlock autoLauncherInterfaceItemBlock = new ItemBlock(autoLauncherInterfaceBlock); 
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		
		GameRegistry.registerTileEntity(TileEntityAutoLaunchInterface.class, "autoLaunchInterface");
		GameRegistry.registerBlock(autoLauncherInterfaceBlock, "autoLaunchInterfaceBlock");
		
		ComputerCraftAPI.registerPeripheralProvider(provider);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event){
		RecipeRegister.register();
	}
	
}
