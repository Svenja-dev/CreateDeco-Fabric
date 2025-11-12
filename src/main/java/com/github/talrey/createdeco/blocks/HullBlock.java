package com.github.talrey.createdeco.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class HullBlock extends FacingBlock {
  private static final VoxelShape OUTER = Block.createCuboidShape(
    0d, 0d, 0d,
    16d, 16d, 16d
  );
  private static final VoxelShape INNER = Block.createCuboidShape(
    2d, 2d, 2d,
    14d, 14d, 14d
  );
  private static final VoxelShape CUBE =
    VoxelShapes.combineAndSimplify(OUTER, INNER, VoxelShapes.BooleanBiFunction.ONLY_FIRST
    );

  public HullBlock(Settings settings) {
    super(settings);
  }

  @Override
  protected MapCodec<? extends FacingBlock> getCodec() {
    return null;
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    builder.add(FACING);
  }

  @Nullable
  @Override
  public BlockState getPlacementState(ItemPlacementContext ctx) {
    return this.getDefaultState().with(FACING,
      ctx.getSide().getOpposite()
    );
  }

  @Override
  public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
    return CUBE;
  }

  @Override
  public BlockState rotate(BlockState state, BlockRotation rotation) {
    return state.with(FACING, rotation.rotate(state.get(FACING)));
  }
}
