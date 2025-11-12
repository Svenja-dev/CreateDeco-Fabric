package com.github.talrey.createdeco.blocks;

import com.github.talrey.createdeco.ItemRegistry;
import com.zurrtum.create.foundation.block.ProperWaterloggedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class CoinStackBlock extends Block implements ProperWaterloggedBlock {
  public final String material;
  private static final VoxelShape[] SHAPE = {
    Block.createCuboidShape(
      0d, 0d, 0d,
      16, 2d, 16d
    ),
    Block.createCuboidShape(
      0d, 0d, 0d,
      16, 4d, 16d
    ),
    Block.createCuboidShape(
      0d, 0d, 0d,
      16, 6d, 16d
    ),
    Block.createCuboidShape(
      0d, 0d, 0d,
      16, 8d, 16d
    ),
    Block.createCuboidShape(
      0d, 0d, 0d,
      16, 10d, 16d
    ),
    Block.createCuboidShape(
      0d, 0d, 0d,
      16, 12d, 16d
    ),
    Block.createCuboidShape(
      0d, 0d, 0d,
      16, 14d, 16d
    ),
    Block.createCuboidShape(
      0d, 0d, 0d,
      16, 16d, 16d
    )
  };

  public CoinStackBlock (Settings properties) {
    this(properties, "iron");
  }

  public CoinStackBlock (Settings properties, String material) {
    super(properties);
    this.material = material;
    this.setDefaultState(
      this.getDefaultState()
        .with(Properties.LAYERS, 1)
        .with(WATERLOGGED, false)
    );
  }

  @Override
  public VoxelShape getOutlineShape (BlockState state, BlockView world, BlockPos pos, net.minecraft.util.shape.VoxelShapeContext ctx) {
    return SHAPE[state.get(Properties.LAYERS)-1];
  }

  @Override
  public FluidState getFluidState(BlockState pState) {
    return fluidState(pState);
  }

  @Override
  public BlockState getStateForNeighborUpdate (BlockState stateIn, Direction facing, BlockState facingState, WorldAccess worldIn, BlockPos currentPos, BlockPos facingPos) {
    updateWater(worldIn, stateIn, currentPos);
    if (facing == Direction.DOWN && !worldIn.getBlockState(facingPos).isSideSolidFullSquare(worldIn, facingPos, Direction.UP)) return Blocks.AIR.getDefaultState();
    return super.getStateForNeighborUpdate(stateIn, facing, facingState, worldIn, currentPos, facingPos);
  }

  @Nullable
  @Override
  public BlockState getPlacementState (ItemPlacementContext ctx) {
    return withWater(getDefaultState(), ctx);
  }

  @Override
  protected void appendProperties (StateManager.Builder<Block, BlockState> builder) {
    builder.add(Properties.LAYERS, WATERLOGGED);
  }

  @Override
  public ItemStack getPickStack (WorldView level, BlockPos pos, BlockState state) {
    return ItemRegistry.COINSTACKS.containsKey(material)
      ? new ItemStack(ItemRegistry.COINSTACKS.get(material))
      : new ItemStack(Items.AIR);
  }

  @Override
  public boolean canPlaceAt (BlockState state, WorldView level, BlockPos pos) {
    return canSurvive(level, pos);
  }

  public static boolean canSurvive (WorldView level, BlockPos pos) {
    return !level.isAir(pos.down());
  }
}
