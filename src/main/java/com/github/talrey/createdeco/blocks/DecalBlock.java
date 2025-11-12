package com.github.talrey.createdeco.blocks;

import com.mojang.serialization.MapCodec;
import com.zurrtum.create.content.equipment.wrench.IWrenchable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class DecalBlock extends WallMountedBlock implements IWrenchable {
  protected static final VoxelShape CEILING_AABB;
  protected static final VoxelShape FLOOR_AABB;
  protected static final VoxelShape NORTH_AABB;
  protected static final VoxelShape SOUTH_AABB;
  protected static final VoxelShape WEST_AABB;
  protected static final VoxelShape EAST_AABB;

  public DecalBlock(Settings properties) {
    super(properties);
    this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH).with(FACE, WallMountLocation.WALL));
  }

    @Override
    protected MapCodec<? extends WallMountedBlock> codec() {
        return null;
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView level, BlockPos pos, net.minecraft.util.shape.VoxelShapeContext context) {
    Direction direction = state.get(FACING);
    return switch (state.get(FACE)) {
      case FLOOR -> FLOOR_AABB;
      case CEILING -> CEILING_AABB;
      case WALL -> switch (direction) {
        case EAST -> EAST_AABB;
        case WEST -> WEST_AABB;
        case SOUTH -> SOUTH_AABB;
        case NORTH, UP, DOWN -> NORTH_AABB;
      };
    };
  }

  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    builder.add(FACING, FACE);
  }

  static {
    CEILING_AABB = Block.createCuboidShape(2.0D, 14.0D, 2.0D, 14.0D, 16.0D, 14.0D);
    FLOOR_AABB = Block.createCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 2.0D, 14.0D);
    NORTH_AABB = Block.createCuboidShape(2.0D, 2.0D, 14.0D, 14.0D, 14.0D, 16.0D);
    SOUTH_AABB = Block.createCuboidShape(2.0D, 2.0D, 0.0D, 14.0D, 14.0D, 2.0D);
    WEST_AABB = Block.createCuboidShape(14.0D, 2.0D, 2.0D, 16.0D, 14.0D, 14.0D);
    EAST_AABB = Block.createCuboidShape(0.0D, 2.0D, 2.0D, 2.0D, 14.0D, 14.0D);
  }
}
