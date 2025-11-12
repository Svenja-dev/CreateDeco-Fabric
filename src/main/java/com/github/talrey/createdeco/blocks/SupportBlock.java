package com.github.talrey.createdeco.blocks;

import com.mojang.serialization.MapCodec;
import com.zurrtum.create.foundation.block.ProperWaterloggedBlock;
import com.zurrtum.create.foundation.placement.PoleHelper;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class SupportBlock extends FacingBlock implements ProperWaterloggedBlock {
  private static final VoxelShape NORTH = Block.createCuboidShape(
    0d, 0d, 0d,
    16d, 16d, 2d
  );
  private static final VoxelShape SOUTH = Block.createCuboidShape(
    0d, 0d, 14d,
    16d, 16d, 16d
  );
  private static final VoxelShape EAST = Block.createCuboidShape(
    0d, 0d, 0d,
    2d, 16d, 16d
  );
  private static final VoxelShape WEST = Block.createCuboidShape(
    14d, 0d, 0d,
    16d, 16d, 16d
  );
  private static final VoxelShape UP = Block.createCuboidShape(
    0d, 14d, 0d,
    16d, 16d, 16d
  );
  private static final VoxelShape DOWN = Block.createCuboidShape(
    0d, 0d, 0d,
    16d, 2d, 16d
  );
  private static final VoxelShape X = VoxelShapes.union(EAST, WEST);
  private static final VoxelShape Y = VoxelShapes.union(UP, DOWN);
  private static final VoxelShape Z = VoxelShapes.union(NORTH, SOUTH);

  private static final int placementHelperId = PlacementHelpers.register(new SupportBlock.PlacementHelper());

  public SupportBlock (Settings props) {
    super(props);
    this.setDefaultState(this.getDefaultState()
            .with(WATERLOGGED, false));
  }

    @Override
    protected MapCodec<? extends FacingBlock> codec() {
        return null;
    }

    @Override
  protected void appendProperties (StateManager.Builder<Block, BlockState> builder) {
    builder.add(Properties.WATERLOGGED, FACING);
  }

  @Override
  protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult ray) {
    ItemStack heldItem = player.getStackInHand(hand);

    IPlacementHelper placementHelper = PlacementHelpers.get(placementHelperId);
    if (!placementHelper.matchesItem(heldItem))
      return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

    return placementHelper.getOffset(player, world, state, pos, ray)
      .placeInWorld(world, ((BlockItem) heldItem.getItem()), player, hand, ray);
  }

  @Nullable
  @Override
  public BlockState getPlacementState (ItemPlacementContext ctx) {
    FluidState fluid = ctx.getWorld().getFluidState(ctx.getBlockPos());
    BlockState result = getDefaultState()
      .with(FACING, ctx.getSide())
      .with(Properties.WATERLOGGED, fluid.isOf(Fluids.WATER));
    return result;
  }

  @Override
  public boolean canFillWithFluid(@Nullable PlayerEntity playerEntity, BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
    return !state.get(Properties.WATERLOGGED) && fluid == Fluids.WATER;
  }

  @Override
  public FluidState getFluidState (BlockState state) {
    return state.get(Properties.WATERLOGGED) ? Fluids.WATER.getStill(false) : Fluids.EMPTY.getDefaultState();
  }

  @Override
  public VoxelShape getCullingShape (BlockState state, BlockView getter, BlockPos pos) {
    return VoxelShapes.empty();
  }

  @Override
  public VoxelShape getOutlineShape (BlockState state, BlockView reader, BlockPos pos, net.minecraft.util.shape.VoxelShapeContext ctx) {
    return switch (state.get(FACING).getAxis()) {
      case X  -> VoxelShapes.union(VoxelShapes.union(Y, Z));
      case Z  -> VoxelShapes.union(VoxelShapes.union(X, Y));
      default -> VoxelShapes.union(VoxelShapes.union(X, Z));
    };
  }

  public static boolean isSupportBlock (ItemStack test) {
    return (test.getItem() instanceof BlockItem)
      && isSupportBlock(((BlockItem)test.getItem()).getBlock());
  }

  public static boolean isSupportBlock (Block test) {
    return test instanceof SupportBlock;
  }

  @MethodsReturnNonnullByDefault
  private static class PlacementHelper extends PoleHelper<Direction> {
    public PlacementHelper() {
      super(state -> SupportBlock.isSupportBlock(state.getBlock()),
        state -> state.get(SupportBlock.FACING).getAxis(), SupportBlock.FACING
      );
    }

    @Override
    public Predicate<ItemStack> getItemPredicate () {
      return (Predicate<ItemStack>) SupportBlock::isSupportBlock;
    }

    @Override
    public Predicate<BlockState> getStatePredicate () {
      return state -> SupportBlock.isSupportBlock(state.getBlock());
    }

    @Override
    public PlacementOffset getOffset(PlayerEntity player, World world, BlockState state, BlockPos pos,
                                     BlockHitResult ray) {
      PlacementOffset offset = super.getOffset(player, world, state, pos, ray);
      offset.withTransform(offset.getTransform());
      return offset;
    }
  }
}
