package com.twitchy.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/** Backs the GearSet builder GUI. Nine staging slots (3x3) hold whatever items get dragged in -
 *  these don't map to real equipment slots directly; slot assignment is worked out from each
 *  item's own type at save time, in GearSetBuilderGui. No player-inventory slots at all - NEI's
 *  own item panel is already visible alongside this screen and can drag directly into these
 *  staging slots, so binding the player's actual inventory here would just be redundant. */
public class GearSetBuilderContainer extends Container {

    public static final int STAGING_SLOTS = 27;
    private final IInventory staging = new InventoryBasic("gearset_staging", false, STAGING_SLOTS);

    public static final int GRID_LEFT = 20;
    public static final int GRID_TOP = 121;

    public GearSetBuilderContainer(InventoryPlayer playerInv) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlotToContainer(new Slot(staging, row * 9 + col, GRID_LEFT + col * 18, GRID_TOP + row * 18));
            }
        }
    }

    public IInventory getStaging() {
        return staging;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int fromSlot) {
        return null;
    }

    @Override
    public ItemStack slotClick(int slotId, int clickedButton, int mode, EntityPlayer player) {
        if (slotId >= 0 && slotId < STAGING_SLOTS) {
            ItemStack cursorStack = player.inventory.getItemStack();

            if (cursorStack != null) {
                // Ghost placement: copy identity only (item + metadata), quantity fixed at 1,
                // and leave whatever's on the cursor completely untouched - no real item ever
                // changes hands, so this works identically in survival or creative.
                ItemStack ghost = new ItemStack(cursorStack.getItem(), 1, cursorStack.getItemDamage());
                staging.setInventorySlotContents(slotId, ghost);
            } else {
                // Clicking an occupied ghost slot with an empty cursor clears it.
                staging.setInventorySlotContents(slotId, null);
            }

            return null; // cursor stack is never modified
        }

        return super.slotClick(slotId, clickedButton, mode, player);
    }
}
