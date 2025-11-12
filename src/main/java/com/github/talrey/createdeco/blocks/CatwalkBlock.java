package com.github.talrey.createdeco.blocks;

import com.zurrtum.create.content.equipment.wrench.IWrenchable;
import com.zurrtum.create.foundation.block.ProperWaterloggedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class CatwalkBlock extends Block implements IWrenchable, ProperWaterloggedBlock {
  private static final VoxelShape VOXEL_TOP = Block.createCuboidShape(
    0d, 14d, 0d,
    16d, 16d, 16d
  );
  private static final VoxelShape SUPPORTED = VoxelShapes.fullCube();
  public static final BooleanProperty BOTTOM = Properties.BOTTOM;

  public CatwalkBlock (Settings props) {
    super(props);
    this.setDefaultState(this.getDefaultState()
        .with(BOTTOM, false)
        .with(WATERLOGGED, false));
  }

  @Override
  public VoxelShape getOutlineShape(BlockState state, BlockView reader, BlockPos pos, net.minecraft.util.shape.VoxelShapeContext ctx) {
    return getCollisionShape(state, reader, pos, ctx);
  }

  @Override
  public VoxelShape getCollisionShape (BlockState state, BlockView world, BlockPos pos, net.minecraft.util.shape.VoxelShapeContext ctx) {
    return state.get(BOTTOM) ? SUPPORTED : VOXEL_TOP;
  }


  private boolean isBottom(BlockView level, BlockPos pos) {
    return level.getBlockState(pos.down()).getBlock() instanceof SupportBlock;
  }

  public static boolean isCatwalk (ItemStack test) {
    return (test.getItem() instanceof BlockItem be)
      && be.getBlock() instanceof CatwalkBlock;
  }

  public static boolean isCatwalk (Block test) {
    return test instanceof CatwalkBlock || test instanceof CatwalkStairBlock;
  }

  @Override
  public BlockState getPlacementState (ItemPlacementContext ctx) {
    FluidState fluid = ctx.getWorld().getFluidState(ctx.getBlockPos());
    BlockPos blockPos = ctx.getBlockPos();
    World level = ctx.getWorld();

    return getDefaultState()
        .with(Properties.WATERLOGGED, fluid.getFluid() == Fluids.WATER)
        .with(BOTTOM, this.isBottom(level, blockPos));
  }

  public static boolean canPlaceCatwalk (World world, BlockPos pos) {
    return world.getBlockState(pos).isReplaceable();
  }

  @Override
  protected void appendProperties (StateManager.Builder<Block, BlockState> builder) {
    super.appendProperties(builder);
    builder.add(Properties.WATERLOGGED, BOTTOM);
  }

  @Override
  public boolean canFillWithFluid(@Nullable PlayerEntity playerEntity, BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
    return !state.get(Properties.WATERLOGGED) && fluid == Fluids.WATER;
  }

  @Override
  public FluidState getFluidState(BlockState state) {
    return state.get(Properties.WATERLOGGED) ? Fluids.WATER.getStill(false) : Fluids.EMPTY.getDefaultState();
  }

  public void scheduledTick(BlockState state, ServerWorld level, BlockPos pos, Random random) {
    BlockState blockState = state.with(BOTTOM, this.isBottom(level, pos));
    if (state != blockState) {
      level.setBlockState(pos, blockState, 3);
    }
  }

  public void onBlockAdded(BlockState state, World level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
    if (!level.isClient) {
      level.scheduleBlockTick(pos, this, 1);
    }

  }

  public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess level, BlockPos pos, BlockPos neighborPos) {
    if (state.get(WATERLOGGED)) {
      level.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(level));
    }

    if (!level.isClient()) {
      level.scheduleBlockTick(pos, this, 1);
    }

    return state;
  }
}
