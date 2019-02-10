package com.eldonad.ccgalacticraftaddon.machines.autolauncherinterface;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import scala.Console;

public class TileEntityAutoLaunchInterface extends TileEntity implements IPeripheral{

	public int targetDestinationNumber;
	public int newLaunchState;
	public boolean dirty;
	
	private Class<?> autoLauncherClass;
	private Method setDestinationFrequency;
	private Method setLaunchDropdownSelection;
	public enum LaunchTimer{
		NONE(0,"none"),
		INSTANT(3, "instant"),
		TIME_10SEC(4, "10sec"),
		TIME_30SEC(5, "30sec"),
		TIME_1MIN(6, "1min"),
		REDSTONE_POWERED(7, "redstonePowered");
		
		
		private final int value;
		private final String title;
		private LaunchTimer(int value, String title) {
			this.value = value;
			this.title = title;
		}
		
		public int getValue() {return this.value;}
		public String getTitle() {return this.title;}
		
		public static LaunchTimer fromTitle(String title) {
			Console.out().println("-" + title + "-");
			LaunchTimer[] values = LaunchTimer.class.getEnumConstants();
			for (LaunchTimer i : values) {
				if (title.equals( i.getTitle())) { 
					return i;
				}
			}
			return null;
		}
		
	}

	public static final String autoLauncherPath = "micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityLaunchController";
	
	public TileEntityAutoLaunchInterface() {
		dirty = false;
		newLaunchState = 0;
		try {
			autoLauncherClass = Class.forName(autoLauncherPath);
			setDestinationFrequency = autoLauncherClass.getMethod("setDestinationFrequency", int.class);
			setLaunchDropdownSelection = autoLauncherClass.getMethod("setLaunchDropdownSelection", int.class);
			
		} catch (ClassNotFoundException e) {
			Console.out().println("class not found");
		} catch (NoSuchMethodException e){
			Console.out().println("method not found");
		}
		
		
	}



	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
		
	}



	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		readFromNBT(pkt.func_148857_g());
			dirty = true;
	}
	
	



	@Override
	public void readFromNBT(NBTTagCompound tag) {
		this.targetDestinationNumber = tag.getInteger("destination_number");
		this.newLaunchState = tag.getInteger("new_launch_state");
	}



	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tag.setInteger("destination_number", targetDestinationNumber);
		tag.setInteger("new_launch_state", newLaunchState);
	}



	@Override
	public String getType() {
		return "autolaunchInterface";
	}

	@Override
	public String[] getMethodNames() {
		return new String[] {
			"setDestination",
			"setLaunchState"
		};
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments)
			throws LuaException, InterruptedException {
		switch (method) {
		case 0: //setDestination	
			targetDestinationNumber = ((Double)arguments[0]).intValue();
			dirty = true;
			Console.out().print("set new destination");
			return null;
			
		case 1:
			LaunchTimer newState = LaunchTimer.fromTitle((String)arguments[0]);
			if (newState != null) {
				this.newLaunchState = newState.getValue();
				dirty = true;

			}
			else {
				throw new LuaException("Bad argument");
			}
			return null;
		}
		throw new LuaException("Invalid method");
	}


	@Override
	public void attach(IComputerAccess computer) {
		
	}

	@Override
	public void detach(IComputerAccess computer) {
		
	}

	@Override
	public boolean equals(IPeripheral other) {
		return (this == other);
	}
	
	@Override
	public void updateEntity() {	
		if (!worldObj.isRemote && dirty == true) {
			
			for (int x = this.xCoord - 1; x <= this.xCoord + 1; ++x) {
				for (int y = this.yCoord - 1; y <= this.yCoord + 1; ++y) {
					for (int z = this.zCoord - 1; z <= this.zCoord + 1; ++z) {
						TileEntity entity = this.getWorldObj().getTileEntity(x, y, z);
						if (entity != null && entity.getClass() == autoLauncherClass) {
							try {
								setDestinationFrequency.invoke(entity, targetDestinationNumber);
								if (this.newLaunchState != 0) {
									setLaunchDropdownSelection.invoke(entity, this.newLaunchState);
								}
							} catch (IllegalAccessException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IllegalArgumentException e) {
								// TODO Auto-generated catch block
								Console.out().println("problème d'argument");
							} catch (InvocationTargetException e) {
								// TODO Auto-generated catch block
								Console.out().println("problème sur l'objet");
							}
						}
					}
				}
			}
		}
		dirty = false;
	}
}
