package com.mraof.minestuck.inventory.captchalouge;

import java.util.Iterator;
import java.util.LinkedList;

import com.mraof.minestuck.Minestuck;
import com.mraof.minestuck.inventory.captchalouge.CaptchaDeckHandler.ModusType;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class StackModus extends Modus
{
	
	protected int size;
	protected LinkedList<ItemStack> list;
	
	@SideOnly(Side.CLIENT)
	protected boolean changed;
	@SideOnly(Side.CLIENT)
	protected ItemStack[] items;
	
	@Override
	public void initModus(ItemStack[] prev)
	{
		list = new LinkedList<ItemStack>();
		if(prev != null)
		{
			for(ItemStack stack : prev)
				if(stack != null)
					list.add(stack);
			size  = prev.length;
		}
		else size = Minestuck.defaultModusSize;
		if(player.worldObj.isRemote)
		{
			items = new ItemStack[size];
			changed = prev != null;
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		size = nbt.getInteger("size");
		list = new LinkedList<ItemStack>();
		for(int i = 0; i < size; i++)
			if(nbt.hasKey("item"+i))
				list.add(ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("item"+i)));
			else break;
		if(player != null && player.worldObj.isRemote)
		{
			items = new ItemStack[size];
			changed = true;
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		Iterator<ItemStack> iter = list.iterator();
		for(int i = 0; i < list.size(); i++)
		{
			ItemStack stack = iter.next();
			nbt.setTag("item"+i, stack.writeToNBT(new NBTTagCompound()));
		}
		return nbt;
	}
	
	@Override
	public boolean putItemStack(ItemStack item)
	{
		ItemStack firstItem = list.getFirst();
		if(firstItem.getItem() == item.getItem() && firstItem.getItemDamage() == item.getItemDamage() && ItemStack.areItemStackTagsEqual(firstItem, item)
				&& firstItem.stackSize + item.stackSize <= firstItem.getMaxStackSize())
			firstItem.stackSize += item.stackSize;
		else if(list.size() < size)
			list.add(item);
		else
		{
			list.add(item);
			CaptchaDeckHandler.launchItem(player, list.removeLast());
		}
		
		return true;
	}
	
	@Override
	public ItemStack[] getItems()
	{
		if(player.worldObj.isRemote)	//Used only when replacing the modus
		{
			ItemStack[] items = new ItemStack[size];
			fillList(items);
			return items;
		}
		
		if(changed)
		{
			fillList(items);
		}
		return items;
	}
	
	protected void fillList(ItemStack[] items)
	{
		Iterator<ItemStack> iter = list.iterator();
		for(int i = 0; i < size; i++)
			if(iter.hasNext())
				items[i] = iter.next();
			else items[i] = null;
	}
	
	@Override
	public boolean increaseSize()
	{
		if(Minestuck.modusMaxSize > 0 && size >= Minestuck.modusMaxSize)
			return false;
		
		size++;
		
		return true;
	}

	@Override
	public ItemStack getItem(int id)
	{
		return list.removeFirst();
	}

	@Override
	public boolean canSwitchFrom(ModusType modus)
	{
		return modus == ModusType.QUEUE;
	}
	
}
