package acs.tabbychat;

import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.ScaledResolution;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiTextField;
import java.util.regex.Pattern;
import java.text.SimpleDateFormat;
import org.lwjgl.input.Mouse;
import org.lwjgl.input.Keyboard;

public class TCSettingsGUITest extends net.minecraft.src.GuiScreen {
	private static TabbyChat tc;
	private static Minecraft mc;
	private final int margin = 10;
	private final int line_height = 16;
	public final int displayWidth = 325;
	public final int displayHeight = 170;
	
	private static final int saveButton = 9120;
	private static final int cancelButton = 9121;
	
	public TCSettingsGUITest() {
		this.mc = Minecraft.getMinecraft();
	}
	
	protected TCSettingsGUITest(TabbyChat _tc) {
		this();
		tc = _tc;
	}	

	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		
		this.buttonList.clear();

		int effWidth = this.displayWidth;
		int effHeight = this.displayHeight;
		int iMargin = (this.line_height - mc.fontRenderer.FONT_HEIGHT)/2;
		
		int col1x = this.width/2 - this.margin;
		int col2x = (this.width+effWidth)/2 - this.margin;
		int row1y = (this.height-effHeight)/2;
		int row2y = row1y + this.line_height + this.margin;
		int row3y = row2y + this.line_height + this.margin;
		int row4y = row3y + this.line_height + this.margin;
		int row5y = row4y + this.line_height + this.margin;
		int row6y = row5y + this.line_height + this.margin;
		int row7y = row6y + this.line_height + this.margin;		
		
		int bW = 40;
		int bH = this.line_height;
		PrefsButton savePrefs = new PrefsButton(saveButton, (this.width + effWidth)/2 - bW, (this.height + effHeight)/2 - bH, bW, bH, "Save");
		this.buttonList.add(savePrefs);
		PrefsButton cancelPrefs = new PrefsButton(cancelButton, (this.width + effWidth)/2 - 2*bW - 2, (this.height + effHeight)/2 - bH, bW, bH, "Cancel");
		this.buttonList.add(cancelPrefs);
		
	}
	
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}
	
	public void drawScreen(int x, int y, float f) {
		int effWidth = this.displayWidth;
		int effHeight = this.displayHeight;
		int iMargin = (this.line_height - mc.fontRenderer.FONT_HEIGHT)/2;
		
		int col1x = (this.width-effWidth)/2;
		int col2x = this.width/2 + this.margin;
		int row1y = (this.height-effHeight)/2 + 4;
		int row2y = row1y + this.line_height + this.margin;
		int row3y = row2y + this.line_height + this.margin;
		int row4y = row3y + this.line_height + this.margin;
		int row5y = row4y + this.line_height + this.margin;
		int row6y = row5y + this.line_height + this.margin;
		int row7y = row6y + this.line_height + this.margin;
		
		drawRect(this.width/2 - effWidth/2 - this.margin, this.height/2 - effHeight/2 - this.margin, this.width/2 + effWidth/2 + this.margin, this.height/2 + effHeight/2 + this.margin, 0x88000000);
		
		super.drawScreen(x, y, f);
	}
}
