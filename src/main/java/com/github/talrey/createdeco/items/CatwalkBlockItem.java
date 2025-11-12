package com.github.talrey.createdeco.items;

import com.github.talrey.createdeco.blocks.CatwalkBlock;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.RaycastContext;

import java.util.List;
import java.util.function.Predicate;

public class CatwalkBlockItem extends BlockItem {
  private final int placementHelperID;

  public CatwalkBlockItem (CatwalkBlock block, Properties props) {
    super(block, props);
    placementHelperID = PlacementHelpers.register(new CatwalkHelper());
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
    if (helper.matchesState(state) && player != null) {
      return helper.getOffset(player, world, state, pos, ray).placeInWorld(world, this, player, ctx.getHand(), ray).result();
    }
    return super.useOnBlock(ctx);
  }

  @MethodsReturnNonnullByDefault
  public static class CatwalkHelper implements IPlacementHelper {
    @Override
    public Predicate<ItemStack> getItemPredicate () {
      return CatwalkBlock::isCatwalk;
    }

    @Override
    public Predicate<BlockState> getStatePredicate () {
      return state -> CatwalkBlock.isCatwalk(state.getBlock());
    }

    @Override
    public PlacementOffset getOffset(PlayerEntity player, World world, BlockState state, BlockPos pos, BlockHitResult ray) {
      Direction face = ray.getDirection();
      if (face.getAxis() != Direction.Axis.Y) {
        return PlacementOffset.success(pos.offset(face.getVector()), offsetState -> offsetState);
      }
      List<Direction> dirs = IPlacementHelper.orderedByDistanceExceptAxis(pos, ray.getLocation(), Direction.Axis.Y);
      for (Direction dir : dirs) {
        BlockPos newPos = pos.offset(dir);
        if (!CatwalkBlock.canPlaceCatwalk(world, newPos)) continue;
        return PlacementOffset.success(newPos, offsetState -> offsetState);
      }
      return PlacementOffset.fail();
    }
  }
}
