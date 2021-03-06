package openmodularturrets.entity.projectiles;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import openmodularturrets.entity.projectiles.damagesources.NormalDamageSource;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.tileentity.turretbase.TurretBase;

import java.util.List;
import java.util.Random;

public class RocketProjectile extends TurretProjectile {

    private int ticksAlive = 0;
    private Entity target;
    public float speed = 0.03F;
    public int upwardsFirst = 15;
    public float yaw;
    public int arrowShake;
    public float accuracy;

    public RocketProjectile(World par1World) {
        super(par1World);
        this.gravity = 0.00F;
    }
    
    public RocketProjectile(World p_i1776_1_, TurretBase turretBase) {
        super(p_i1776_1_, turretBase);
        this.gravity = 0.00F;
    }

    public RocketProjectile(World par1World, Entity target, ItemStack ammo, TurretBase turretBase) {
        super(par1World, ammo, turretBase);
        this.gravity = 0.00F;
        this.target = target;
    }

    @Override
    public void onEntityUpdate() {

        if (ticksExisted >= 100) {
            this.setDead();
        }

        for (int i = 0; i <= 25; i++) {
            Random random = new Random();
            worldObj.spawnParticle("smoke", posX + (random.nextGaussian() / 10), posY + (random.nextGaussian() / 10),
                                   posZ + (random.nextGaussian() / 10), (0), (0), (0));
        }
    }

    @Override
    protected void onImpact(MovingObjectPosition movingobjectposition) {

        if (this.ticksExisted <= 1) {
            return;
        }
        if (movingobjectposition.typeOfHit == movingobjectposition.typeOfHit.BLOCK) {
            Block hitBlock = worldObj.getBlock(movingobjectposition.blockX, movingobjectposition.blockY,
                                               movingobjectposition.blockZ);
            if (hitBlock != null && !hitBlock.getMaterial().isSolid()) {
                // Go through non solid block
                return;
            }
        }

        if (movingobjectposition.typeOfHit.equals(0)) {
            if (worldObj.isAirBlock(movingobjectposition.blockX, movingobjectposition.blockY,
                                    movingobjectposition.blockZ)) {
                return;
            }
        }

        if (!worldObj.isRemote) {

            worldObj.createExplosion(null, posX, posY, posZ, 0.1F, true);
            AxisAlignedBB axis = AxisAlignedBB.getBoundingBox(this.posX - 5, this.posY - 5, this.posZ - 5,
                                                              this.posX + 5, this.posY + 5, this.posZ + 5);
            List<Entity> targets = worldObj.getEntitiesWithinAABB(Entity.class, axis);

            for (Entity mob : targets) {
                int damage = ConfigHandler.getRocketTurretSettings().getDamage();

                if (isAmped) {
                    damage += ConfigHandler.getDamageAmpDmgBonus() * amp_level;
                }

                if (mob instanceof EntityPlayer) {
                    if (canDamagePlayer((EntityPlayer) mob)) {
                        mob.attackEntityFrom(new NormalDamageSource("rocket"), damage);
                        mob.hurtResistantTime = 0;
                    }
                } else {
                    mob.attackEntityFrom(new NormalDamageSource("rocket"), damage);
                    mob.hurtResistantTime = 0;
                }
            }
        }
        this.setDead();
    }

    @Override
    protected float getGravityVelocity() {
        return this.gravity;
    }
}