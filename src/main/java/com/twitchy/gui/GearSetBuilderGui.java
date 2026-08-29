package com.twitchy.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import com.twitchy.entity.GearSets;
import com.twitchy.network.RedeemActionHandler;
import com.twitchy.rewards.RewardAction.GearPiece;

public class GearSetBuilderGui extends GuiContainer {

    private static final int BG = 0xE0171717;
    private static final int PANEL = 0xFF0E0E0E;
    private static final int BORDER = 0xFF2A2A2A;
    private static final int TEXT = 0xFFF0F0F0;
    private static final int LABEL = 0xFF888888;
    private static final int ACCENT = 0xFF22C55E;
    private static final int GOLD_BORDER = 0xFFD9A441;

    private final GearSetBuilderContainer builderContainer;
    private GuiTextField keyField;
    private GuiTextField nameField;

    private boolean dropdownOpen = false;
    private List<String> knownKeys = new ArrayList<>();

    private static final int BTN_SAVE = 1;
    private static final int BTN_DELETE = 2;
    private static final int BTN_DROPDOWN = 3;

    private static final int STAGING_BOX_LEFT = 16;
    private static final int STAGING_BOX_TOP = 118;
    private static final int STAGING_BOX_RIGHT_MARGIN = 16;
    private static final int STAGING_BOX_BOTTOM = 176;

    public GearSetBuilderGui(GearSetBuilderContainer container) {
        super(container);
        this.builderContainer = container;
        this.xSize = 200;
        this.ySize = 210;
    }

    @Override
    public void initGui() {
        super.initGui();
        int left = (width - xSize) / 2;
        int top = (height - ySize) / 2;

        knownKeys.clear();
        for (String key : GearSets.allKeys()) knownKeys.add(key);

        keyField = new GuiTextField(fontRendererObj, left + 16, top + 60, xSize - 32, 16);
        nameField = new GuiTextField(fontRendererObj, left + 16, top + 96, xSize - 32, 16);
        keyField.setMaxStringLength(64);
        nameField.setMaxStringLength(64);

        buttonList.clear();
        buttonList.add(new GuiButton(BTN_DROPDOWN, left + 16, top + 22, xSize - 32, 16, "Select a set..."));
        buttonList.add(new GuiButton(BTN_DELETE, left + 16, top + 179, (xSize - 32 - 8) / 2, 20, "Delete"));
        buttonList.add(new GuiButton(BTN_SAVE, left + 16 + (xSize - 32 - 8) / 2 + 8, top + 179,
            (xSize - 32 - 8) / 2, 20, "Save Set"));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glDisable(GL11.GL_LIGHTING);
        int left = (width - xSize) / 2;
        int top = (height - ySize) / 2;

        drawRect(left, top, left + xSize, top + ySize, BG);
        drawGradientRect(left, top, left + xSize, top + 1, BORDER, BORDER);

        fontRendererObj.drawString("Loadout Sets", left + 16, top + 8, TEXT);
        fontRendererObj.drawString("Key", left + 16, top + 50, LABEL);
        fontRendererObj.drawString("Display Name", left + 16, top + 86, LABEL);

        keyField.drawTextBox();
        nameField.drawTextBox();

        int boxLeft = left + STAGING_BOX_LEFT;
        int boxTop = top + STAGING_BOX_TOP;
        int boxRight = left + xSize - STAGING_BOX_RIGHT_MARGIN;
        int boxBottom = top + STAGING_BOX_BOTTOM;

        drawRect(boxLeft - 1, boxTop - 1, boxRight + 1, boxBottom + 1, GOLD_BORDER);
        drawRect(boxLeft, boxTop, boxRight, boxBottom, PANEL);

        // No manual item drawing needed anymore - these are real Slot objects again
        // (SlotDummy extends Slot), so GuiContainer's own base draw loop renders their
        // contents automatically, exactly like every vanilla inventory screen.

        if (dropdownOpen) {
            int dy = top + 40;
            for (int i = 0; i < knownKeys.size(); i++) {
                drawRect(left + 16, dy + i * 16, left + xSize - 16, dy + i * 16 + 16, PANEL);
                fontRendererObj.drawString(GearSets.getDisplayName(knownKeys.get(i)), left + 20, dy + i * 16 + 4, TEXT);
            }
            int newY = dy + knownKeys.size() * 16;
            drawRect(left + 16, newY, left + xSize - 16, newY + 16, PANEL);
            fontRendererObj.drawString("+ New Set", left + 20, newY + 4, ACCENT);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        // Plain vanilla click handling now - ContainerExtended.slotClick already routes
        // correctly to SlotDummy for any real click landing on one of these slots.
        super.mouseClicked(mouseX, mouseY, button);

        keyField.mouseClicked(mouseX, mouseY, button);
        nameField.mouseClicked(mouseX, mouseY, button);

        if (dropdownOpen) {
            int left = (width - xSize) / 2;
            int top = (height - ySize) / 2;
            int dy = top + 40;
            for (int i = 0; i < knownKeys.size(); i++) {
                if (isInRect(mouseX, mouseY, left + 16, dy + i * 16, xSize - 32, 16)) {
                    loadSet(knownKeys.get(i));
                    dropdownOpen = false;
                    return;
                }
            }
            int newY = dy + knownKeys.size() * 16;
            if (isInRect(mouseX, mouseY, left + 16, newY, xSize - 32, 16)) {
                clearForm();
                dropdownOpen = false;
            }
        }
    }

    private boolean isInRect(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == BTN_DROPDOWN) {
            dropdownOpen = !dropdownOpen;
        } else if (button.id == BTN_SAVE) {
            saveCurrentSet();
        } else if (button.id == BTN_DELETE) {
            if (!keyField.getText().isBlank()) GearSets.deleteSet(keyField.getText());
            clearForm();
        }
    }

    private void loadSet(String key) {
        keyField.setText(key);
        nameField.setText(GearSets.getDisplayName(key));

        for (int i = 0; i < builderContainer.getStaging().getSizeInventory(); i++) {
            builderContainer.getStaging().setInventorySlotContents(i, null);
        }

        RedeemActionHandler resolver = new RedeemActionHandler();
        List<GearPiece> pieces = GearSets.getSet(key);
        for (int i = 0; i < pieces.size() && i < GearSetBuilderContainer.STAGING_SLOTS; i++) {
            GearPiece piece = pieces.get(i);
            Item item = resolver.resolveItem(piece.item);
            if (item != null) {
                builderContainer.getStaging().setInventorySlotContents(i, new ItemStack(item, 1, piece.metadata));
            }
        }
    }

    private void clearForm() {
        keyField.setText("");
        nameField.setText("");
        for (int i = 0; i < builderContainer.getStaging().getSizeInventory(); i++) {
            builderContainer.getStaging().setInventorySlotContents(i, null);
        }
    }

    private void saveCurrentSet() {
        String key = keyField.getText().trim();
        String name = nameField.getText().trim();
        if (key.isEmpty() || name.isEmpty()) return;

        List<GearPiece> pieces = new ArrayList<>();
        for (int i = 0; i < GearSetBuilderContainer.STAGING_SLOTS; i++) {
            ItemStack stack = builderContainer.getStaging().getStackInSlot(i);
            if (stack == null) continue;

            GearPiece piece = new GearPiece();
            piece.item = Item.itemRegistry.getNameForObject(stack.getItem());
            piece.metadata = stack.getItemDamage();
            piece.slot = resolveSlotFor(stack);
            pieces.add(piece);
        }

        GearSets.putSet(key, name, pieces);
        if (!knownKeys.contains(key)) knownKeys.add(key);
    }

    private int resolveSlotFor(ItemStack stack) {
        if (stack.getItem() instanceof ItemArmor armor) {
            return switch (armor.armorType) {
                case 0 -> 4;
                case 1 -> 3;
                case 2 -> 2;
                case 3 -> 1;
                default -> 0;
            };
        }
        return 0;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            this.mc.displayGuiScreen(null);
            return;
        }

        if (keyField.isFocused()) {
            keyField.textboxKeyTyped(typedChar, keyCode);
        } else if (nameField.isFocused()) {
            nameField.textboxKeyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void updateScreen() {
        keyField.updateCursorCounter();
        nameField.updateCursorCounter();
    }
}
