package com.eldonad.ccgalacticraftaddon.utils.messages;

import java.lang.reflect.InvocationTargetException;

import com.eldonad.ccgalacticraftaddon.machines.autolauncherinterface.TileEntityAutoLaunchInterface;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import scala.Console;

public class SetScheduledStateMessageHandler implements IMessageHandler<SetScheduledStateMessageHandler.SetScheduleStateMessage, IMessage>{

	//public SetScheduledStateMessageHandler() {}
	
	
	@Override
	public IMessage onMessage(SetScheduleStateMessage message, MessageContext ctx) {
		
		Console.out().println("Now changing state");
		World worldServer = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(message.dimID);
		TileEntityAutoLaunchInterface launchInterface = (TileEntityAutoLaunchInterface) worldServer.getTileEntity(message.x, message.y, message.z);
		TileEntity launcher = launchInterface.getLauncher();
		try {
			launchInterface.setDisabled.invoke(launcher, 1, message.newState);
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
		return null;
	}
	
	public static class SetScheduleStateMessage implements IMessage{

		public SetScheduleStateMessage() {
			this.newState = false;
		}
		
		public SetScheduleStateMessage(boolean newState, int x, int y, int z, int dimID) {
			this.newState = newState;
			this.x = x;
			this.y = y;
			this.z = z;
			this.dimID = dimID;
		}

		public boolean newState;
		public int x, y, z, dimID;
		
		
		void setState(boolean toSet) {
			newState = toSet;
		}
		
		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeBoolean(newState);
			buf.writeInt(x);
			buf.writeInt(y);
			buf.writeInt(z);
			buf.writeInt(dimID);
			
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			newState = buf.readBoolean();
			x = buf.readInt();
			y = buf.readInt();
			z = buf.readInt();
			dimID = buf.readInt();
			
		}
		
		
		
	}
	
}

