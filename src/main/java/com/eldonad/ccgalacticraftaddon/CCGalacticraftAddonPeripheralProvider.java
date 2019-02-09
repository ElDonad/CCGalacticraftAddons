package com.eldonad.ccgalacticraftaddon;

import com.eldonad.ccgalacticraftaddon.machines.autolauncherinterface.TileEntityAutoLaunchInterface;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class CCGalacticraftAddonPeripheralProvider implements IPeripheralProvider{

	@Override
	public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
		TileEntity toReturn = world.getTileEntity(x, y, z);
		if (toReturn instanceof TileEntityAutoLaunchInterface) {
			return (TileEntityAutoLaunchInterface)toReturn;
		}
		return null;

	}
	
}
