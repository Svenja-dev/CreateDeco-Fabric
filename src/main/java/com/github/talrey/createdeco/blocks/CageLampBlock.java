package com.github.talrey.createdeco.blocks;

import com.mojang.serialization.MapCodec;
import com.zurrtum.create.content.equipment.wrench.IWrenchable;
import com.zurrtum.create.foundation.block.ProperWaterloggedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class CageLampBlock extends FacingBlock implements ProperWaterloggedBlock, IWrenchable {
  public final DustParticleEffect particle;

  protected static final VoxelShape AABB_UP = Block.createCuboidShape(
    5.0D, 0.0D, 5.0D,
    11.0D, 8.0D, 11.0D
  );

  protected static final VoxelShape AABB_DOWN = Block.createCuboidShape(
    5.0D, 8.0D, 5.0D,
    11.0D, 16.0D, 11.0D
  );

  protected static final VoxelShape AABB_EAST = Block.createCuboidShape(
    0.0D, 5.0D, 5.0D,
    8.0D, 11.0D, 11.0D
  );

  protected static final VoxelShape AABB_WEST = Block.createCuboidShape(
    8.0D, 5.0D, 5.0D,
    16.0D, 11.0D, 11.0D
  );

  protected static final VoxelShape AABB_SOUTH = Block.createCuboidShape(
    5.0D, 5.0D, 0.0D,
    11.0D, 11.0D, 8.0D
  );

  protected static final VoxelShape AABB_NORTH = Block.createCuboidShape(
    5.0D, 5.0D, 8.0D,
    11.0D, 11.0D, 16.0D
  );

  public CageLampBlock(Settings settings, Vector3f color) {
    super(settings);
    this.particle = new DustParticleEffect(color, 0.3f);
    this.setDefaultState(this.getDefaultState()
      .with(Properties.LIT, false)
      .with(Properties.INVERTED, false)
      .with(Properties.FACING, Direction.UP)
      .with(WATERLOGGED, false));
  }

  @Nullable
  @Override
  public BlockState getPlacementState(ItemPlacementContext ctx) {
    return withWater(getDefaultState()
      .with(Properties.FACING, ctx.getSide())
      .with(Properties.LIT, ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos())),
    ctx);
  }

  @Override
  public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
    return switch (state.get(Properties.FACING)) {
      case UP    -> AABB_UP;
      case DOWN  -> AABB_DOWN;
      case EAST  -> AABB_EAST;
      case WEST  -> AABB_WEST;
      case SOUTH -> AABB_SOUTH;
      case NORTH -> AABB_NORTH;
    };
  }

  @Override
  public BlockState rotate(BlockState state, BlockRotation rotation) {
    return state.with(FACING, rotation.rotate(state.get(FACING)));
  }

  @Override
  public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
    updateWater(world, state, pos);
    return !this.canPlaceAt(state, world, pos) ? Blocks.AIR.getDefaultState() :
      super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
  }

  @Override
  public FluidState getFluidState(BlockState state) {
    return fluidState(state);
  }

  public static boolean shouldBeLit(BlockState state, World world, BlockPos pos) {
    Direction attach = state.get(Properties.FACING).getOpposite();
    return state.get(Properties.INVERTED) ^ world.isReceivingRedstonePower(pos.offset(attach));
  }

  @Override
  public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
    Direction face = state.get(Properties.FACING);
    if (pos.offset(face.getOpposite()).equals(sourcePos)) {
      BlockState next = toggle(state, world, pos);
    }
  }

  @Override
  protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
    BlockState next = this.toggle(state.cycle(Properties.INVERTED), world, pos);
    if (world.isClient) {
      return ActionResult.SUCCESS;
    } else {
      float pitch = next.get(Properties.INVERTED) ? 0.6f : 0.5f;
      world.playSound(null, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3f, pitch);
      return ActionResult.CONSUME;
    }
  }

  private BlockState toggle(BlockState state, World world, BlockPos pos) {
    if (world.isClient) {
      makeParticle(state, world, pos);
    }
    BlockState next = state.with(Properties.LIT, shouldBeLit(state, world, pos));
    world.setBlockState(pos, next, 3);
    return next;
  }

  private void makeParticle(BlockState state, WorldAccess world, BlockPos pos) {
    Direction direction = state.get(Properties.FACING);
    float x = pos.getX() + 0.5f + 0.1f * direction.getOffsetX();
    float y = pos.getY() + 0.5f + 0.1f * direction.getOffsetY();
    float z = pos.getZ() + 0.5f + 0.1f * direction.getOffsetZ();
    world.addParticle(particle, x, y, z, 0d, 0d, 0d);
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    builder.add(
      Properties.LIT,
      Properties.INVERTED,
      Properties.FACING,
      WATERLOGGED
    );
  }

  @Override
  public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
    return canPlaceAt(world, pos, state.get(Properties.FACING));
  }

  public static boolean canPlaceAt(WorldView world, BlockPos pos, Direction facing) {
    BlockPos opposite = pos.offset(facing.getOpposite());
    return Block.sideCoversSmallSquare(world, opposite, facing);
  }

  @Override
  protected MapCodec<? extends FacingBlock> getCodec() {
    return null;
  }
}
