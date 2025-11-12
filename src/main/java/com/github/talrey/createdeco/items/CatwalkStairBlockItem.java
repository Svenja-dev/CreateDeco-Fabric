package com.github.talrey.createdeco.items;

import com.github.talrey.createdeco.blocks.CatwalkBlock;
import com.github.talrey.createdeco.blocks.CatwalkStairBlock;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.RaycastContext;

import java.util.List;
import java.util.function.Predicate;

public class CatwalkStairBlockItem extends BlockItem {
  private final int placementHelperID;

  public CatwalkStairBlockItem (CatwalkStairBlock block, Properties props) {
    super(block, props);
    placementHelperID = PlacementHelpers.register(new CatwalkStairBlockItem.CatwalkHelper());
  }

  @Override
  public ActionResult useOnBlock (ItemUsageContext ctx) {
    BlockPos pos   = ctx.getBlockPos();
    Direction face = ctx.getSide();
    World world    = ctx.getWorld();
    PlayerEntity player  = ctx.getPlayer();

    BlockState state        = world.getBlockState(pos);
    IPlacementHelper helper = PlacementHelpers.get(placementHelperID);
    BlockHitResult ray = new BlockHitResult(ctx.getHitPos(), face, pos, true);
    if (helper.matchesState(state) && player != null && !player.isSneaking()) {
      return helper.getOffset(player, world, state, pos, ray)
        .placeInWorld(world, this, player, ctx.getHand(), ray).result();
    }
    return super.useOnBlock(ctx);
  }

  @MethodsReturnNonnullByDefault
  public static class CatwalkHelper implements IPlacementHelper {
    @Override
    public Predicate<ItemStack> getItemPredicate () {
      return CatwalkStairBlock::isCatwalkStair;
    }

    @Override
    public Predicate<BlockState> getStatePredicate () {
      return state -> CatwalkStairBlock.isCatwalkStair(state.getBlock());
    }

    @Override
    public PlacementOffset getOffset(PlayerEntity player, World world, BlockState state, BlockPos pos, BlockHitResult ray) {
//      if (face.getAxis() != Direction.Axis.Y) {
//        return PlacementOffset.success(pos.offset(face.getVector()), offsetState -> offsetState);
//      }
      List<Direction> dirs = IPlacementHelper.orderedByDistanceExceptAxis(pos, ray.getLocation(), Direction.Axis.Y);
      for (Direction dir : dirs) {
        Direction facing;
        if (state.contains(Properties.HORIZONTAL_FACING)) {
          facing = state.get(Properties.HORIZONTAL_FACING);
          if (dir.getOpposite() != facing) continue;
        }
        else facing = dir.getOpposite();

        BlockPos newPos = pos.offset(dir).offset(0, 1, 0);
        if (!CatwalkBlock.canPlaceCatwalk(world, newPos)) continue;

        return PlacementOffset.success(newPos,
          offsetState -> {
          // not entirely sure why this is necessary tbh, but if it prevents crashes aight then.
            if (offsetState.contains(Properties.HORIZONTAL_FACING)) {
              offsetState = offsetState.with(Properties.HORIZONTAL_FACING, facing);
            }
            return offsetState;
          }
        );
      }
      return PlacementOffset.fail();
    }


  }
}
