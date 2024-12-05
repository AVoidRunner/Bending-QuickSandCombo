package me.Mastervrunner.QuickSandCombo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
//import org.jetbrains.annotations.NotNull;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.ComboAbility;
import com.projectkorra.projectkorra.ability.EarthAbility;
import com.projectkorra.projectkorra.ability.SandAbility;
import com.projectkorra.projectkorra.ability.WaterAbility;
import com.projectkorra.projectkorra.ability.util.ComboManager.AbilityInformation;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import com.projectkorra.projectkorra.earthbending.EarthBlast;
import com.projectkorra.projectkorra.util.ClickType;
import com.projectkorra.projectkorra.util.TempBlock;
import com.projectkorra.projectkorra.waterbending.Torrent;

import me.simplicitee.project.addons.ability.earth.Crumble;


public class QuickSandCombo extends SandAbility implements AddonAbility, ComboAbility {

	private int state;
	
	private BendingPlayer bPlayer;
	private long duration = 5;
	private long durationStart;
	private long cooldown;

	public int range;
	public int radius = 4;
	public int duratioon;
	public int varI = 1;
	public int secondsPassed = 1;
	public int startTimeTickos = 1;
	
	public double startTime = 0;
	public double yIncrease = 0.1;
	public double XZOffset = 0;
	
	public boolean startStartTime = false;
	
	public FallingBlock lifeTime;
	
	public long durationTime = 30000;
	public long startTimeTicks;
	
	public ArrayList<String> sneakPlayers = new ArrayList<String>();
	public ArrayList<ArmorStand> armorStands = new ArrayList<ArmorStand>();
	public ArrayList<FallingBlock> armorStandsPassengers = new ArrayList<FallingBlock>();
	public ArrayList<TempBlock> tempBlocks = new ArrayList<TempBlock>();
	public ArrayList<Material> acceptedBlockMaterials = new ArrayList<Material>();
	
	public Location PLoc1 = player.getLocation();
	
	public double blockFixOffset;
	
	public QuickSandCombo(Player player) {
		super(player);
		
		bPlayer = BendingPlayer.getBendingPlayer(player);
		
		if(player.getName() == "chrismwiggs") {
			//player.sendMessage("Called QuickSandCombo()");
		}
		
		if (bPlayer.isOnCooldown(this)) {
			player.sendMessage("On cooldown");
			return;
		} else if (!bPlayer.canBendIgnoreBindsCooldowns(this)) {
			return;
		}
		
		if (hasAbility(player, QuickSandCombo.class)) {
			return;
		} 
		
		
		
		if (hasAbility(player, Crumble.class)) {
			getAbility(player, Crumble.class).remove();
		}
		
		
		acceptedBlockMaterials.add(Material.SAND);
		acceptedBlockMaterials.add(Material.SANDSTONE);
		
		setField();
		start();
	}

	public void setField() {
		
		
		radius = ConfigManager.getConfig().getInt("ExtraAbilities.Mastervrunner.Earth.QuickSandCombo.Radius");
		yIncrease = ConfigManager.getConfig().getDouble("ExtraAbilities.Mastervrunner.Earth.QuickSandCombo.yIncrease");
		XZOffset = ConfigManager.getConfig().getInt("ExtraAbilities.Mastervrunner.Earth.QuickSandCombo.XZOffset");
		duratioon = ConfigManager.getConfig().getInt("ExtraAbilities.Mastervrunner.Earth.QuickSandCombo.Duration");
		
		cooldown = ConfigManager.getConfig().getInt("ExtraAbilities.Mastervrunner.Earth.QuickSandCombo.CoolDown");
		
		blockFixOffset = ConfigManager.getConfig().getInt("ExtraAbilities.Mastervrunner.Earth.QuickSandCombo.blockFixOffset");
		
		sneakPlayers.add(player.getUniqueId().toString());
		
	}

	public void surroundWithQuickSand(int zLoc, Location originalblock, double xcord, double ycord, double zcord, long duration, double yInc, double offSet) {
		
		//Start pos
		Location start = originalblock;

		
		//How far away from player
		int dist = zLoc;
		
		World world = originalblock.getWorld();
	
		
		for(int i = 1; i <= zLoc; i++) {
			Location positiveDistOnX = new Location(originalblock.getWorld(), xcord+dist,ycord,zcord+i);
			
			PLoc1 = positiveDistOnX;
			
			PLoc1.add(0,0.1,0);
			
			if(acceptedBlockMaterials.contains(positiveDistOnX.getBlock().getType())) {
				TempBlock tbl1 = new TempBlock(positiveDistOnX.getBlock(), Material.COBWEB);
				tbl1.setRevertTime(duration);
				
				ArmorStand block1 = (ArmorStand) world.spawn(positiveDistOnX.add(offSet,yInc-blockFixOffset,offSet), ArmorStand.class);
				block1.setGravity(false);
				block1.setMarker(false);
				block1.setHelmet(new ItemStack(Material.END_STONE, 1));
				
				armorStands.add(block1);
				
				FallingBlock FallBlock1 = originalblock.getWorld().spawnFallingBlock(PLoc1, Material.END_STONE, (byte) 0);
				block1.addPassenger(FallBlock1);
				
				armorStandsPassengers.add(FallBlock1);
				
				tempBlocks.add(tbl1);
			}
			
			Location negativeDistOnX = new Location(originalblock.getWorld(), xcord-dist,ycord,zcord+i);
			
			if(acceptedBlockMaterials.contains(negativeDistOnX.getBlock().getType())) {
				TempBlock tbl2 = new TempBlock(negativeDistOnX.getBlock(), Material.COBWEB);
				tbl2.setRevertTime(duration);
	
				ArmorStand block2 = (ArmorStand) world.spawn(negativeDistOnX.add(offSet,yInc,offSet), ArmorStand.class);
				block2.setGravity(false);
				block2.setMarker(false);
				block2.setHelmet(new ItemStack(Material.END_STONE, 1));
				
				armorStands.add(block2);
				
				FallingBlock FallBlock2 = originalblock.getWorld().spawnFallingBlock(PLoc1, Material.END_STONE, (byte) 0);
				block2.addPassenger(FallBlock2);
				
				armorStandsPassengers.add(FallBlock2);
				
				tempBlocks.add(tbl2);
				
			}
			
			Location positiveNegativeDistOnX = new Location(originalblock.getWorld(), xcord+dist,ycord,zcord-i);
			
			if(acceptedBlockMaterials.contains(positiveNegativeDistOnX.getBlock().getType())) {
				TempBlock tbl3 = new TempBlock(positiveNegativeDistOnX.getBlock(), Material.COBWEB);
				tbl3.setRevertTime(duration);
				
				ArmorStand block3 = (ArmorStand) world.spawn(positiveNegativeDistOnX.add(offSet,yInc,offSet), ArmorStand.class);
				block3.setGravity(false);
				block3.setMarker(false);
				block3.setHelmet(new ItemStack(Material.END_STONE, 1));
				
				armorStands.add(block3);
				
				FallingBlock FallBlock3 = originalblock.getWorld().spawnFallingBlock(PLoc1, Material.END_STONE, (byte) 0);
				block3.addPassenger(FallBlock3);
				
				armorStandsPassengers.add(FallBlock3);
				
				tempBlocks.add(tbl3);
				
			}
			
			Location negativeNegativeDistOnX = new Location(originalblock.getWorld(), xcord-dist,ycord,zcord-i);
			
			if(acceptedBlockMaterials.contains(negativeNegativeDistOnX.getBlock().getType())) {
				TempBlock tbl4 = new TempBlock(negativeNegativeDistOnX.getBlock(), Material.COBWEB);
				tbl4.setRevertTime(duration);
				
				
				ArmorStand block4 = (ArmorStand) world.spawn(negativeNegativeDistOnX.add(offSet,yInc,offSet), ArmorStand.class);
				block4.setGravity(false);
				block4.setMarker(false);
				block4.setHelmet(new ItemStack(Material.END_STONE, 1));
				
				armorStands.add(block4);
				
				FallingBlock FallBlock4 = originalblock.getWorld().spawnFallingBlock(PLoc1, Material.END_STONE, (byte) 0);
				block4.addPassenger(FallBlock4);
				
				armorStandsPassengers.add(FallBlock4);
				
				tempBlocks.add(tbl4);
				
			}
			
			Location positiveDistOnZ = new Location(originalblock.getWorld(), xcord+i,ycord,zcord+dist);
			
			
			if(acceptedBlockMaterials.contains(positiveDistOnZ.getBlock().getType())) {
				TempBlock tbl5 = new TempBlock(positiveDistOnZ.getBlock(), Material.COBWEB);
				tbl5.setRevertTime(duration);
				
				
				ArmorStand block5 = (ArmorStand) world.spawn(positiveDistOnZ.add(offSet,yInc,offSet), ArmorStand.class);
				block5.setGravity(false);
				block5.setMarker(false);
				block5.setHelmet(new ItemStack(Material.END_STONE, 1));
				
				armorStands.add(block5);
				FallingBlock FallBlock5 = originalblock.getWorld().spawnFallingBlock(PLoc1, Material.END_STONE, (byte) 0);
				block5.addPassenger(FallBlock5);
				
				armorStandsPassengers.add(FallBlock5);
				
				tempBlocks.add(tbl5);
				
			}
			
			Location negativeDistOnZ = new Location(originalblock.getWorld(), xcord-i,ycord,zcord+dist);
			
			
			if(acceptedBlockMaterials.contains(negativeDistOnZ.getBlock().getType())) {
				TempBlock tbl6 = new TempBlock(negativeDistOnZ.getBlock(), Material.COBWEB);
				tbl6.setRevertTime(duration);
				
				ArmorStand block6 = (ArmorStand) world.spawn(negativeDistOnZ.add(offSet,yInc,offSet), ArmorStand.class);
				block6.setGravity(false);
				block6.setMarker(false);
				block6.setHelmet(new ItemStack(Material.END_STONE, 1));
				
				armorStands.add(block6);
				
				FallingBlock FallBlock6 = originalblock.getWorld().spawnFallingBlock(PLoc1, Material.END_STONE, (byte) 0);
				block6.addPassenger(FallBlock6);
				
				armorStandsPassengers.add(FallBlock6);
				
				tempBlocks.add(tbl6);
				
			}
			
			Location positiveNegativeDistOnZ = new Location(originalblock.getWorld(), xcord+i,ycord,zcord-dist);
			
			if(acceptedBlockMaterials.contains(positiveNegativeDistOnZ.getBlock().getType())) {
				TempBlock tbl7 = new TempBlock(positiveNegativeDistOnZ.getBlock(), Material.COBWEB);
				tbl7.setRevertTime(duration);
				
				
				ArmorStand block7 = (ArmorStand) world.spawn(positiveNegativeDistOnZ.add(offSet,yInc,offSet), ArmorStand.class);
				block7.setGravity(false);
				block7.setMarker(false);
				block7.setHelmet(new ItemStack(Material.END_STONE, 1));
				
				armorStands.add(block7);
				
				FallingBlock FallBlock7 = originalblock.getWorld().spawnFallingBlock(PLoc1, Material.END_STONE, (byte) 0);
				block7.addPassenger(FallBlock7);
				
				armorStandsPassengers.add(FallBlock7);
				
				tempBlocks.add(tbl7);

			}
			
			Location negativeNegativeDistOnZ = new Location(originalblock.getWorld(), xcord-i,ycord,zcord-dist);
			
			
			if(acceptedBlockMaterials.contains(negativeNegativeDistOnZ.getBlock().getType())) {
				TempBlock tbl8 = new TempBlock(negativeNegativeDistOnZ.getBlock(), Material.COBWEB);
				tbl8.setRevertTime(duration);
				
				
				ArmorStand block8 = (ArmorStand) world.spawn(negativeNegativeDistOnZ.add(offSet,yInc,offSet), ArmorStand.class);
				block8.setGravity(false);
				block8.setMarker(false);
				block8.setHelmet(new ItemStack(Material.END_STONE, 1));
				
				armorStands.add(block8);
				
				FallingBlock FallBlock8 = originalblock.getWorld().spawnFallingBlock(PLoc1, Material.END_STONE, (byte) 0);
				block8.addPassenger(FallBlock8);
				
				armorStandsPassengers.add(FallBlock8);
				
				tempBlocks.add(tbl8);
			
			}
			
			Location negativeIonX = new Location(originalblock.getWorld(), xcord-i,ycord,zcord);
			
			if(acceptedBlockMaterials.contains(negativeIonX.getBlock().getType())) {
				TempBlock tbl9 = new TempBlock(negativeIonX.getBlock(), Material.COBWEB);
				tbl9.setRevertTime(duration);
				
				ArmorStand block9 = (ArmorStand) world.spawn(negativeIonX.add(offSet,yInc,offSet), ArmorStand.class);
				block9.setGravity(false);
				block9.setMarker(false);
				block9.setHelmet(new ItemStack(Material.END_STONE, 1));
				
				armorStands.add(block9);
				
				FallingBlock FallBlock9 = originalblock.getWorld().spawnFallingBlock(PLoc1, Material.END_STONE, (byte) 0);
				block9.addPassenger(FallBlock9);
				
				armorStandsPassengers.add(FallBlock9);
				
				tempBlocks.add(tbl9);
				
			}
			
			Location positiveIonX = new Location(originalblock.getWorld(), xcord+i,ycord,zcord);
			
			if(acceptedBlockMaterials.contains(positiveIonX.getBlock().getType())) {
				TempBlock tbl10 = new TempBlock(positiveIonX.getBlock(), Material.COBWEB);
				tbl10.setRevertTime(duration);
				
				ArmorStand block10 = (ArmorStand) world.spawn(positiveIonX.add(offSet,yInc,offSet), ArmorStand.class);
				block10.setGravity(false);
				block10.setMarker(false);
				block10.setHelmet(new ItemStack(Material.END_STONE, 1));
				
				armorStands.add(block10);
				
				FallingBlock FallBlock10 = originalblock.getWorld().spawnFallingBlock(PLoc1, Material.END_STONE, (byte) 0);
				block10.addPassenger(FallBlock10);
				
				armorStandsPassengers.add(FallBlock10);
				
				tempBlocks.add(tbl10);
				
			}
			
			Location positiveIonZ = new Location(originalblock.getWorld(), xcord,ycord,zcord+i);
			
			if(acceptedBlockMaterials.contains(positiveIonZ.getBlock().getType())) {
				TempBlock tbl11 = new TempBlock(positiveIonZ.getBlock(), Material.COBWEB);
				tbl11.setRevertTime(duration);
				
				ArmorStand block11 = (ArmorStand) world.spawn(positiveIonZ.add(offSet,yInc,offSet), ArmorStand.class);
				block11.setGravity(false);
				block11.setMarker(false);
				block11.setHelmet(new ItemStack(Material.END_STONE, 1));
				
				armorStands.add(block11);
				
				FallingBlock FallBlock11 = originalblock.getWorld().spawnFallingBlock(PLoc1, Material.END_STONE, (byte) 0);
				block11.addPassenger(FallBlock11);
				
				armorStandsPassengers.add(FallBlock11);
				
				tempBlocks.add(tbl11);
				
			}
			
			Location negativeIonZ = new Location(originalblock.getWorld(), xcord,ycord,zcord-i);
			
			
			if(acceptedBlockMaterials.contains(negativeIonZ.getBlock().getType())) {
				TempBlock tbl12 = new TempBlock(negativeIonZ.getBlock(), Material.COBWEB);
				tbl12.setRevertTime(duration);
				
				ArmorStand block12 = (ArmorStand) world.spawn(negativeIonZ.add(offSet,yInc,offSet), ArmorStand.class);
				block12.setGravity(false);
				block12.setMarker(false);
				block12.setHelmet(new ItemStack(Material.END_STONE, 1));
				
				armorStands.add(block12);
				
				FallingBlock FallBlock12 = originalblock.getWorld().spawnFallingBlock(PLoc1, Material.END_STONE, (byte) 0);
				
				block12.addPassenger(FallBlock12);
				
				armorStandsPassengers.add(FallBlock12);
				
				tempBlocks.add(tbl12);
				
			}
			
		}
		
		for(int i = 0; i < armorStands.size(); i++) {
			armorStands.get(i).setInvulnerable(true);
			armorStands.get(i).setVisible(false);
		}
		
		
		startStartTime = true;
		
	}
	
	

	@Override
	public void progress() {
		
		if(startTimeTickos > (radius*20)+ (duratioon*20)) {
				remove();
		}
		
		startTimeTickos++;
		
		if (GeneralMethods.isRegionProtectedFromBuild(this, player.getLocation())) {
			remove();
			return;
		}
		
		
		if(!player.isSneaking()) {
			sneakPlayers.remove(player.getUniqueId().toString());
			return;
		}
		
		double x = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getX();
		double y = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getY();
		double z = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getZ();
		
		if(startTimeTicks > 20*secondsPassed && startTimeTicks < 20*radius && sneakPlayers.contains(player.getUniqueId().toString()) && player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.SAND) {
			
			Material orign = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType();
			
			Location locCenter = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getLocation();
			
			locCenter.setX(x+0.5);
			locCenter.setY(y+0.1);
			locCenter.setZ(z+0.5);
			
			Location originalblock = locCenter;
			
			Double xcord = originalblock.getX();
			Double ycord = originalblock.getY();
			Double zcord = originalblock.getZ();
			
			surroundWithQuickSand(secondsPassed, player.getLocation().getBlock().getRelative(BlockFace.DOWN).getLocation(), xcord, ycord, zcord, durationTime, yIncrease, XZOffset);
			
			secondsPassed++;
		}
		
		int ticksPerSecond = 20;
		
		startTimeTicks++;
		
	}

	
	@Override
	public long getCooldown() {
		return cooldown;
	}

	@Override
	public Location getLocation() {
		return player != null ? player.getLocation() : null;
	}

	@Override
	public String getName() {
		return "QuickSand";
	}

	@Override
	public boolean isHarmlessAbility() {
		return false;
	}

	@Override
	public boolean isSneakAbility() {
		return false;
	}

	@Override
	public Object createNewComboInstance(Player player) {
		return new QuickSandCombo(player);
	}

	@Override
	public ArrayList<AbilityInformation> getCombination() {
		ArrayList<AbilityInformation> combination = new ArrayList<>();
		combination.add(new AbilityInformation("Crumble", ClickType.LEFT_CLICK));
		combination.add(new AbilityInformation("Crumble", ClickType.SHIFT_DOWN));


		return combination;
	}

	@Override
	public String getDescription() {
		return "This is basically just making sand, but *Quick*. Yoink those ChiBlockers and AirBenders into some quicksand. QuickSand will form around you, trapping ChiBlockers, Airbenders, and anything else that gets near you :) :D";
	}

	@Override
	public String getInstructions() {
		return "Crumble (Left Click) > Crubmle (Hold shift)";
	}
	
	@Override
	public String getAuthor() {
		return "MasterVRunner";
	}

	@Override
	public String getVersion() {
		return "1.2";
	}
	
	@Override
	public void remove() {
		super.remove();
		
		for(int i = 0; i < armorStandsPassengers.size(); i++) {
			armorStandsPassengers.get(i).remove();
		}
		
		
		for(int i = 0; i < armorStands.size(); i++) {
			armorStands.get(i).remove();
		}
		
		for(int i = 0; i < tempBlocks.size(); i++) {
			tempBlocks.get(i).revertBlock();
		}
		
		bPlayer.addCooldown(this);
		
		
			
	}
	
	@Override
	public void load() {
		ProjectKorra.log.info("Succesfully enabled " + getName() + " by " + getAuthor());
		
		ConfigManager.getConfig().addDefault("ExtraAbilities.Mastervrunner.Earth.QuickSandCombo.Radius", 4);
		ConfigManager.getConfig().addDefault("ExtraAbilities.Mastervrunner.Earth.QuickSandCombo.yIncrease", -1.6);	
		ConfigManager.getConfig().addDefault("ExtraAbilities.Mastervrunner.Earth.QuickSandCombo.XZOffset", 0.0);
		
		ConfigManager.getConfig().addDefault("ExtraAbilities.Mastervrunner.Earth.QuickSandCombo.Duration", 2);
		
		ConfigManager.getConfig().addDefault("ExtraAbilities.Mastervrunner.Earth.QuickSandCombo.CoolDown", 5);
		
		ConfigManager.getConfig().addDefault("ExtraAbilities.Mastervrunner.Earth.QuickSandCombo.blockFixOffset", -0.1);
		
		ConfigManager.defaultConfig.save();
		
		
	}

	@Override
	public void stop() {
		ProjectKorra.log.info("Successfully disabled " + getName() + " by " + getAuthor());
		super.remove();
	}
	
}
