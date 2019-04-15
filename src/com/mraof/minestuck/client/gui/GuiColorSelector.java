package com.mraof.minestuck.client.gui;

import com.mraof.minestuck.network.MinestuckChannelHandler;
import com.mraof.minestuck.network.MinestuckPacket;
import com.mraof.minestuck.network.SelectionPacket;
import com.mraof.minestuck.network.TransportalizerPacket;
import com.mraof.minestuck.util.ColorCollector;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

import java.io.IOException;

public class GuiColorSelector extends GuiScreen
{
	
	private static final ResourceLocation guiBackground = new ResourceLocation("minestuck", "textures/gui/color_selector.png");
	private static final int guiWidth = 176, guiHeight = 157;
	private int selectedColor;
	private int previewColor = this.hexStringParser("f00000");
	private boolean firstTime;
	private GuiColoredTextField destinationTextField;
	
	
	public GuiColorSelector(Minecraft minecraft, boolean firstTime)
	{
		this.firstTime = firstTime;
		selectedColor = ColorCollector.playerColor;
		this.fontRenderer = minecraft.fontRenderer;
	}
	
	@Override
	public void initGui()
	{
		int yOffset = (this.height - 10) - (guiHeight - 10);
		this.destinationTextField = new GuiColoredTextField(0, this.fontRenderer, (width - guiWidth)/2 + 31, (height - guiHeight)/2 + 130, 45, 20, previewColor);
		this.destinationTextField.setMaxStringLength(6);
		this.destinationTextField.setFocused(false);
		this.destinationTextField.setText(Integer.toHexString(previewColor));
		
		GuiButton button = new GuiButton(0, (width + guiWidth)/2 - 84, (height - guiHeight)/2 + 130, 60, 20, "Choose");
		buttonList.add(button);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		int xOffset = (width - guiWidth)/2;
		int yOffset = (height - guiHeight)/2;
		
		this.drawDefaultBackground();
		
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		
		this.mc.getTextureManager().bindTexture(guiBackground);
		this.drawTexturedModalRect(xOffset, yOffset, 0, 0, guiWidth, guiHeight);
		
		String cacheMessage = I18n.format("gui.selectColor");
		mc.fontRenderer.drawString(cacheMessage, (this.width / 2) - mc.fontRenderer.getStringWidth(cacheMessage) / 2, yOffset + 12, 0x404040);
		
		for(int i = 0; i < 4; i++)
		{
			int color = ColorCollector.getColor(i) | 0xFF000000;
			int x = 21 + 34*i;
			drawRect(xOffset + x, yOffset + 32, xOffset + x + 32, yOffset + 48, color);
		}
		for(int i = 0; i < 4; i++)
		{
			int color = ColorCollector.getColor(i + 4) | 0xFF000000;
			int x = 21 + 34*i;
			drawRect(xOffset + x, yOffset + 53, xOffset + x + 32, yOffset + 69, color);
		}
		for(int xIndex = 0; xIndex < 4; xIndex++)
			for(int yIndex = 0; yIndex < 3; yIndex++)
			{
				int color = ColorCollector.getColor(yIndex*4 + xIndex + 8) | 0xFF000000;
				int x = 21 + 34*xIndex;
				int y = 74 + 18*yIndex;
				drawRect(xOffset + x, yOffset + y, xOffset + x + 32, yOffset + y + 16, color);
			}
		
		
		//draw text entry box
		this.destinationTextField.drawTextBox();
	
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		if(selectedColor != -1)
		{
			int x = 19 + (selectedColor % 4)*34;
			int y = 30 + (selectedColor/4)*18;
			if(selectedColor >= 4)
				y += 3;
			if(selectedColor >= 8)
				y += 3;
			GlStateManager.color(1F, 1F, 1F);
			this.mc.getTextureManager().bindTexture(guiBackground);
			this.drawTexturedModalRect(xOffset + x, yOffset + y, guiWidth, 0, 36, 20);
		}
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
	{
		super.mouseClicked(mouseX, mouseY, mouseButton);
		this.destinationTextField.mouseClicked(mouseX, mouseY, mouseButton);
		
		if(mouseButton == 0)
		{
			int xOffset = (width - guiWidth)/2;
			int yOffset = (height - guiHeight)/2;
			
			for(int x = 0; x < 4; x++)
				for(int y = 0; y < 5; y++)
				{
					int xPos = xOffset + 21 + x*34;
					int yPos = yOffset + 32 + y*18;
					if(y > 0)
						yPos += 3;
					if(y > 1)
						yPos += 3;
					if(mouseX >= xPos && mouseX < xPos + 32 && mouseY >= yPos && mouseY < yPos + 16)
					{
						int index = y*4 + x;
						selectedColor = index != selectedColor ? index : -1;
						this.destinationTextField.setBackgroundColor(ColorCollector.getColor(selectedColor));
						this.destinationTextField.setText(Integer.toHexString(ColorCollector.getColor((selectedColor))));
						return;
					}
				}
			
		}
	}
	
	@Override
	protected void keyTyped(char character, int key) throws IOException
	{
		super.keyTyped(character, key);
		this.destinationTextField.textboxKeyTyped(character, key);
		
		String text = destinationTextField.getText();
		
		if(this.isValidHexString(text)) {
			this.previewColor = this.hexStringParser(text);
			this.destinationTextField.setBackgroundColor(hexStringParser(text));
		}
		
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		/* This is for when the player inputs text in the field and then hits the button. Needs functionality and edits to MinestuckPacket, SelectionPacket.
		if(test for valid hex here)
		{
			MinestuckChannelHandler.sendToServer(MinestuckPacket.makePacket(MinestuckPacket.Type.SELECTION, SelectionPacket.COLOR, this.previewcolor));
		}
		*/
		
		MinestuckChannelHandler.sendToServer(MinestuckPacket.makePacket(MinestuckPacket.Type.SELECTION, SelectionPacket.COLOR, this.selectedColor));
		ColorCollector.playerColor = selectedColor;
		this.mc.displayGuiScreen(null);
	}
	
	@Override
	public void onGuiClosed()
	{
		if(firstTime && mc != null && mc.player != null)
		{
			ITextComponent message;
			if(ColorCollector.playerColor == -1)
				message = new TextComponentTranslation("message.selectDefaultColor");
			else message = new TextComponentTranslation("message.selectColor");
			this.mc.player.sendMessage(new TextComponentString("[Minestuck] ").appendSibling(message));
		}
	}
	
	//Tests a given string as a valid hex code.
	public boolean isValidHexString(String input) {
		return input.matches("-?[0-9a-fA-F]+");
	}
	
	//Parses a string into a integer to be stored and used.
	public static int hexStringParser(String in)
	{
	    int out = 0;
	    for(int i=0; i<in.length(); i++)
	    {
	        out *= 16;    //Compiler should optimize to sll, left as multiplication for readability
	        int curr = hexCharParser(in.charAt(i));
	        if(curr == -1)
	            return -1;
	        out += curr;
	    }
	    return out;
	}
	
	public static int hexCharParser(char x)
	{
	    int out = x - '0';
	    if(out < 0 || out > 9)
	    {
	        out = x - 'A' + 10;
	        if(out < 10 || out > 15)
	        {
	            out = x - 'a' + 10;
	            if(out < 10 || out > 15)
	            {
	                out = -1;
	            }
	        }
	    }
	    return out;
	}
	
}