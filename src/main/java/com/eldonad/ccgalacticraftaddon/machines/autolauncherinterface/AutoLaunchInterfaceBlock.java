package com.eldonad.ccgalacticraftaddon.machines.autolauncherinterface;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class AutoLaunchInterfaceBlock extends Block {

	public AutoLaunchInterfaceBlock() {
		super(Material.iron);
		this.setBlockName("autoLaunchInterfaceBlock").setBlockTextureName("ccgalacticraftaddon:autoLaunchInterfaceBlock");
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}

	@Override
	public boolean isOpaqueCube() {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileEntityAutoLaunchInterface();
	}
	
	
	
	
	
	

}
