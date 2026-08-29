package com.twitchy.gui;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;

import codechicken.lib.inventory.ContainerExtended;
import codechicken.lib.inventory.SlotDummy;

/** Uses CodeChickenLib's real, official ghost-slot system - SlotDummy + ContainerExtended is the
 *  exact pair NEI's own NEIDummySlotHandler.handleDragNDrop checks for via instanceof before it
 *  ever hands over a dragged item. Nothing custom needed here: ContainerExtended's own
 *  slotClick(...) already routes to SlotHandleClicks (SlotDummy's parent) for both normal clicks
 *  and NEI drag-drop, and SlotDummy.putStack already caps at the given stack limit (1, since we
 *  only ever want identity, never a real quantity) and never touches/consumes whatever's on the
 *  cursor - it only ever copies from it. */
public class GearSetBuilderContainer extends ContainerExtended {

    public static final int STAGING_SLOTS = 27;
    private final IInventory staging = new InventoryBasic("gearset_staging", false, STAGING_SLOTS);

    public static final int GRID_LEFT = 20;
    public static final int GRID_TOP = 121;

    public GearSetBuilderContainer() {
        super();
        this.windowId = -1;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlotToContainer(
                    new SlotDummy(staging, row * 9 + col, GRID_LEFT + col * 18, GRID_TOP + row * 18, 1));
            }
        }
    }

    public IInventory getStaging() {
        return staging;
    }
}
