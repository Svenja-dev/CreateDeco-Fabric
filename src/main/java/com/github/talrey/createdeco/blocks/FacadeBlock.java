package com.github.talrey.createdeco.blocks;

import com.mojang.serialization.MapCodec;
import com.zurrtum.create.content.equipment.wrench.IWrenchable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MultifaceGrowthBlock;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class FacadeBlock extends MultifaceGrowthBlock implements IWrenchable, Waterloggable {
  private static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
  private final LichenGrower grower = new LichenGrower(this);

  public FacadeBlock(Settings properties) {
    super(properties);
    this.setDefaultState(this.getDefaultState().with(WATERLOGGED, false));
  }

    @Override
    protected MapCodec<? extends MultifaceGrowthBlock> codec() {
        return null;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    super.appendProperties(builder);
    builder.add(WATERLOGGED);
  }

  public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess level, BlockPos pos, BlockPos neighborPos) {
    if (state.get(WATERLOGGED)) {
      level.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(level));
    }

    return super.getStateForNeighborUpdate(state, direction, neighborState, level, pos, neighborPos);
  }


  public FluidState getFluidState(BlockState state) {
    return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
  }

  public boolean isTransparent(BlockState state, BlockView level, BlockPos pos) {
    return state.getFluidState().isEmpty();
  }

  public LichenGrower getGrower() {
    return this.grower;
  }

  @Override
  public boolean canPlaceAt(BlockState state, WorldView level, BlockPos pos) {
    return true;
  }

  @Override
  public boolean canGrowWithDirection(BlockView level, BlockState state, BlockPos pos, Direction direction) {
    return true;
  }
}
