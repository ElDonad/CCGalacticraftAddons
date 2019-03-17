package com.eldonad.ccgalacticraftaddon.machines.autolauncherinterface;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.eldonad.ccgalacticraftaddon.utils.CCGalacticraftAddonPacketHandler;
import com.eldonad.ccgalacticraftaddon.utils.messages.SetScheduledStateMessageHandler;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import scala.Console;

public class TileEntityAutoLaunchInterface extends TileEntity implements IPeripheral{

	public int targetDestinationNumber;
	public int newLaunchState;
	
	private final ReentrantLock getSheduleStateLock = new ReentrantLock();
	private final Condition scheduleStateUpdated = getSheduleStateLock.newCondition();
	public boolean dirty;
	
	public Class<?> autoLauncherClass;
	public Method setDestinationFrequency;
	public Method setLaunchDropdownSelection;
	public Method setDisabled;
	public Method getDisabled;
	
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
			setDisabled = autoLauncherClass.getMethod("setDisabled", int.class, boolean.class);
			getDisabled = autoLauncherClass.getMethod("getDisabled", int.class);
			
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
			"setLaunchState",
			"toggleSchedule",
			"getScheduleState"
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
			
		case 1: //setLaunchState
			LaunchTimer newState = LaunchTimer.fromTitle((String)arguments[0]);
			if (newState != null) {
				this.newLaunchState = newState.getValue();
				dirty = true;

			}
			else {
				throw new LuaException("Bad argument");
			}
			return null;
			
		case 2: //toggleSchedule
			boolean scheduleState;
			try {
				scheduleState = (Boolean)arguments[0];
			}
			catch(ClassCastException e) {
				throw new LuaException("Bad argument : not bool");
			}
			catch (ArrayIndexOutOfBoundsException e){
				throw new LuaException("Bad argument number : need 1");
			}
			CCGalacticraftAddonPacketHandler.INSTANCE.sendToServer(new SetScheduledStateMessageHandler.SetScheduleStateMessage(scheduleState, this.xCoord, this.yCoord, this.zCoord, this.worldObj.provider.dimensionId));
			this.dirty = true;
			return null;
			
		case 3: //getLaunchState
			/*CCGalacticraftAddonPacketHandler.INSTANCE.sendToServer(new PacketGetScheduleState.RequestMessage(xCoord, yCoord, zCoord, worldObj.provider.dimensionId));
			getSheduleStateLock.lock();
			Console.out().println("now awaiting waking up " + Boolean.toString(worldObj.isRemote));
			while (this.isScheduleStateUpdated != true) {
				scheduleStateUpdated.await();
				Console.out().println("Check update...");
			}
			Console.out().println("Sync done");
			this.isScheduleStateUpdated = false;
			return new Object[] {updatedScheduleState};*/
			TileEntity launcher = getLauncher();
			try {
				boolean truc = (Boolean)getDisabled.invoke(launcher, 1);
				Console.out().print(truc);
				return new Object[] {truc};
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		throw new LuaException("Invalid method");
	}


	@Override
	public void attach(IComputerAccess computer) {
		
	}

	@Override
	public void detach(IComputerAccess computer) {
		
	}
	
	public void notifyScheduleStateUpdate(boolean newState) {
		getSheduleStateLock.lock();
		try {
			Console.out().println("Update received. beginning of sync..." + Boolean.toString(worldObj.isRemote));
			scheduleStateUpdated.signalAll();
		}
		finally {
			getSheduleStateLock.unlock();
		}
	}

	@Override
	public boolean equals(IPeripheral other) {
		return (this == other);
	}
	
	@Override
	public void updateEntity() {	
		if (!worldObj.isRemote && dirty == true) {
			
			TileEntity entity = getLauncher();
			if (entity != null) {
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
		dirty = false;
	}
	
	public TileEntity getLauncher() {
		for (int x = this.xCoord - 1; x <= this.xCoord + 1; ++x) {
			for (int y = this.yCoord - 1; y <= this.yCoord + 1; ++y) {
				for (int z = this.zCoord - 1; z <= this.zCoord + 1; ++z) {
					TileEntity entity = this.getWorldObj().getTileEntity(x, y, z);
					if (entity != null && entity.getClass() == autoLauncherClass) {
						return entity;
					}
				}
			}
		}
		return null;
	}
	
}
