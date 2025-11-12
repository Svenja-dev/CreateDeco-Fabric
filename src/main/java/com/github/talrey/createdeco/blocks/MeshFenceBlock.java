package com.github.talrey.createdeco.blocks;

import com.zurrtum.create.content.equipment.wrench.IWrenchable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceBlock;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

public class MeshFenceBlock extends FenceBlock implements IWrenchable {
    public static final BooleanProperty UP = Properties.UP;

    public MeshFenceBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState()
            .with(UP, false)
            .with(NORTH, false)
            .with(EAST, false)
            .with(SOUTH, false)
            .with(WEST, false)
            .with(WATERLOGGED, false));
    }

    public ActionResult onWrenched(BlockState state, ItemUsageContext context) {
        World world = context.getWorld();

        world.setBlockState(context.getBlockPos(), state.with(UP, !state.get(UP)));
        IWrenchable.playRotateSound(world, context.getBlockPos());

        return ActionResult.SUCCESS;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(UP, NORTH, EAST, WEST, SOUTH, WATERLOGGED);
    }
}
