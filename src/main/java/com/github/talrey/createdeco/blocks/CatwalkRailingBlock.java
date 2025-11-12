package com.github.talrey.createdeco.blocks;

import com.zurrtum.create.content.equipment.wrench.IWrenchable;
import com.zurrtum.create.foundation.block.ProperWaterloggedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CatwalkRailingBlock extends Block implements IWrenchable, ProperWaterloggedBlock {
  private static final VoxelShape VOXEL_NORTH = Block.createCuboidShape(
          0d, 0d, 0d,
          16d, 14d, 2d
  );
  private static final VoxelShape VOXEL_SOUTH = Block.createCuboidShape(
          0d, 0d, 14d,
          16d, 14d, 16d
  );
  private static final VoxelShape VOXEL_EAST = Block.createCuboidShape(
          14d, 0d, 0d,
          16d, 14d, 16d
  );
  private static final VoxelShape VOXEL_WEST = Block.createCuboidShape(
          0d, 0d, 0d,
          2d, 14d, 16d
  );

  public static final BooleanProperty NORTH_FENCE = Properties.NORTH;
  public static final BooleanProperty SOUTH_FENCE = Properties.SOUTH;
  public static final BooleanProperty EAST_FENCE  = Properties.EAST;
  public static final BooleanProperty WEST_FENCE  = Properties.WEST;

  public CatwalkRailingBlock (Settings props) {
    super(props);
    this.setDefaultState(this.getDefaultState()
            .with(NORTH_FENCE, false)
            .with(SOUTH_FENCE, false)
            .with(EAST_FENCE,  false)
            .with(WEST_FENCE,  false)
            .with(Properties.WATERLOGGED, false)
    );
  }

  @Override
  public ActionResult onSneakWrenched (BlockState state, ItemUsageContext context) {
    BlockPos pos   = context.getBlockPos();
    Vec3d subbox    = context.getHitPos().subtract(pos.toCenterPos());
    Direction face = context.getSide();
    World level    = context.getWorld();
    PlayerEntity player  = context.getPlayer();
    var x = subbox.x;
    var z = subbox.z;

    if (level.isClient() || face == Direction.DOWN) return ActionResult.PASS;

    //check if the top face is wrenched, remove side
    if (face == Direction.UP) {
      boolean bottomleft = x < -z;
      boolean topleft = x < z;
      var dir = Direction.WEST;
      if (!bottomleft && topleft) dir = Direction.SOUTH;
      if (!bottomleft && !topleft) dir = Direction.EAST;
      if (bottomleft && !topleft) dir = Direction.NORTH;
      if (bottomleft && topleft) dir = Direction.WEST;

      //obscure edge case where a corner of the top face cannot be wrenched
      if (state.get(fromDirection(dir))) {
        state = state.with(fromDirection(dir), false);
        level.setBlockState(pos, state, 3);
        IWrenchable.playRemoveSound(level, pos);
        if (!player.getAbilities().creativeMode) player.giveItemStack(new ItemStack(state.getBlock().asItem()));
        return ActionResult.SUCCESS;
      }
      else return ActionResult.PASS;
    }

    //check for wrenching the inside faces
    if (x == 0.375 || x == -0.375 || z == 0.375 || z == -0.375) state = state.with(fromDirection(face.getOpposite()), false);

    //check for wrenching the outside faces
    if (x == 0.5 || x == -0.5 || z == 0.5 || z == -0.5) {
      if (!state.get(fromDirection(face))) {
        if (x >= 0.375) state = state.with(EAST_FENCE, false);
        if (x <= -0.375) state = state.with(WEST_FENCE, false);
        if (z <= -0.375) state = state.with(NORTH_FENCE, false);
        if (z >= 0.375) state = state.with(SOUTH_FENCE, false);
      }
      else state = state.with(fromDirection(face), false);
    }

    level.setBlockState(pos, state, 3);
    IWrenchable.playRemoveSound(level, pos);
    if (!player.getAbilities().creativeMode) player.giveItemStack(new ItemStack(state.getBlock().asItem()));
    return ActionResult.SUCCESS;
  }

  @Nullable
  @Override
  public BlockState getPlacementState (ItemPlacementContext ctx) {
    Direction facing = ctx.getHorizontalPlayerFacing();
    FluidState fluid = ctx.getWorld().getFluidState(ctx.getBlockPos());
    BlockState state = getDefaultState()
            .with(NORTH_FENCE, (facing == Direction.NORTH))
            .with(SOUTH_FENCE, (facing == Direction.SOUTH))
            .with(EAST_FENCE,  (facing == Direction.EAST))
            .with(WEST_FENCE,  (facing == Direction.WEST))
            .with(Properties.WATERLOGGED, fluid.getFluid() == Fluids.WATER);
    return state;
  }

  @Override
  protected void appendProperties (StateManager.Builder<Block, BlockState> builder) {
    super.appendProperties(builder);
    builder.add(NORTH_FENCE);
    builder.add(SOUTH_FENCE);
    builder.add(EAST_FENCE);
    builder.add(WEST_FENCE);
    builder.add(Properties.WATERLOGGED);
  }

  @Override
  public BlockState getRotatedBlockState(BlockState originalState, Direction targetedFace) {
    if (targetedFace.getAxis() == Direction.Axis.Y) {
      int state =
              (originalState.get(NORTH_FENCE) ? 8 : 0) +
                      (originalState.get(EAST_FENCE)  ? 4 : 0) +
                      (originalState.get(SOUTH_FENCE) ? 2 : 0) +
                      (originalState.get(WEST_FENCE)  ? 1 : 0);
      return originalState
              .with(NORTH_FENCE, (state & 1) == 1)
              .with(EAST_FENCE,  (state & 8) == 8)
              .with(SOUTH_FENCE, (state & 4) == 4)
              .with(WEST_FENCE,  (state & 2) == 2);
    }
    return originalState;
  }

  @Override
  public VoxelShape getOutlineShape(BlockState state, BlockView reader, BlockPos pos, net.minecraft.util.shape.VoxelShapeContext ctx) {
    return getCollisionShape(state, reader, pos, ctx);
  }

  @Override
  public VoxelShape getCollisionShape (BlockState state, BlockView world, BlockPos pos, net.minecraft.util.shape.VoxelShapeContext ctx) {
    VoxelShape shape = VoxelShapes.empty();
    if (state.get(NORTH_FENCE)) shape = VoxelShapes.union(shape, VOXEL_NORTH);
    if (state.get(SOUTH_FENCE)) shape = VoxelShapes.union(shape, VOXEL_SOUTH);
    if (state.get(EAST_FENCE))  shape = VoxelShapes.union(shape, VOXEL_EAST);
    if (state.get(WEST_FENCE))  shape = VoxelShapes.union(shape, VOXEL_WEST);

    return shape;
  }

  @Override
  public boolean canFillWithFluid(@Nullable PlayerEntity playerEntity, BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
    return !state.get(Properties.WATERLOGGED) && fluid == Fluids.WATER;
  }

  // used to ensure the block doesn't leave a ghost behind if all 4 sides are gone
  @Override
  public void neighborUpdate (
          BlockState state, World level, BlockPos pos,
          Block neighborBlock, BlockPos neighborPos, boolean movedByPiston
  ) {

    if (isEmpty(state)) level.setBlockState(pos, Blocks.AIR.getDefaultState(), 0);
    super.neighborUpdate(state, level, pos, neighborBlock, neighborPos, movedByPiston);
  }

  public static boolean isRailing (ItemStack test) {
    return (test.getItem() instanceof BlockItem) && isRailing(((BlockItem)test.getItem()).getBlock());
  }

  public static boolean isRailing (Block test) {
    return test instanceof CatwalkRailingBlock;
  }

  public static BooleanProperty fromDirection (Direction face) {
    return switch (face) {
      case SOUTH -> SOUTH_FENCE;
      case EAST  -> EAST_FENCE;
      case WEST  -> WEST_FENCE;
      default -> NORTH_FENCE;
    };
  }

  public boolean isEmpty(BlockState state) {
    boolean safe = false;
    for (Direction dir : Properties.HORIZONTAL_FACING.getValues()) {
      safe |= state.get(fromDirection(dir));
    }
    return !safe;
  }

  @Override
  public FluidState getFluidState(BlockState state) {
    return state.get(Properties.WATERLOGGED) ? Fluids.WATER.getStill(false) : Fluids.EMPTY.getDefaultState();
  }

  @Override
  public BlockState rotate(BlockState state, BlockRotation rotation) {
    boolean north = state.get(NORTH_FENCE);
    boolean south = state.get(SOUTH_FENCE);
    boolean east = state.get(EAST_FENCE);
    boolean west = state.get(WEST_FENCE);
    switch (rotation){
      case CLOCKWISE_90 -> {
        north = state.get(WEST_FENCE);
        south = state.get(EAST_FENCE);
        east = state.get(NORTH_FENCE);
        west = state.get(SOUTH_FENCE);
      }
      case CLOCKWISE_180 -> {
        north = state.get(SOUTH_FENCE);
        south = state.get(NORTH_FENCE);
        east = state.get(WEST_FENCE);
        west = state.get(EAST_FENCE);
      }
      case COUNTERCLOCKWISE_90 -> {
        north = state.get(EAST_FENCE);
        south = state.get(WEST_FENCE);
        east = state.get(SOUTH_FENCE);
        west = state.get(NORTH_FENCE);
      }
      case NONE -> {
        north = state.get(NORTH_FENCE);
        south = state.get(SOUTH_FENCE);
        east = state.get(EAST_FENCE);
        west = state.get(WEST_FENCE);
      }
    }
    BlockState newState = getDefaultState().with(NORTH_FENCE, north).with(SOUTH_FENCE, south).with(EAST_FENCE, east).with(WEST_FENCE, west);
    return newState;
  }
}
