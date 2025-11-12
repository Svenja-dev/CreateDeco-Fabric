package com.github.talrey.createdeco;

import com.zurrtum.create.foundation.block.IBE;
import com.zurrtum.create.foundation.item.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;

public class LoaderUtil {
    public static int getSignal (IBE<?> be, BlockState pState, Level pLevel, BlockPos pPos) {
        return be.getBlockEntityOptional(pLevel, pPos).map((vte) ->
                pLevel.getCapability(Capabilities.ItemHandler.BLOCK, pPos, pState, vte, null)
        ).map(ItemHelper::calcRedstoneFromInventory
        ).orElse(0);
    }

    public static boolean checkPlacingNbt (BlockPlaceContext ctx) {
        return true;
    }
}
