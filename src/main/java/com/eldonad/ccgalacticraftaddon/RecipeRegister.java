package com.eldonad.ccgalacticraftaddon;

import cpw.mods.fml.common.registry.GameRegistry;
import micdoodle8.mods.galacticraft.api.item.GCItems;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import scala.Console;


public class RecipeRegister {
	
	public static void register() {
		
		ItemStack basicWafer = GCItems.requestItem("waferBasic", 1);
		
		if (basicWafer != null) {
			
			Console.out().println(basicWafer.getUnlocalizedName());
		}
		else {
			Console.out().println("Failed to load basic wafer");
		}
		GameRegistry.addRecipe(new ItemStack(CCGalacticraftAddon.autoLauncherInterfaceBlock), new Object[] {
				"X X",
				" Y ",
				"X X",
				'X', Item.getItemFromBlock(Blocks.stone),
				'Y', basicWafer
		});
	}
}
