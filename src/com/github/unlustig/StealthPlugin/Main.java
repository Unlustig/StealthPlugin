package com.github.unlustig.StealthPlugin;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.metadata.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public final class Main extends JavaPlugin {
	
	
    @Override
    public void onEnable(){
    	this.saveDefaultConfig();
    	for(Player player : getServer().getOnlinePlayers()){
    		initPlayer(player);
    	}
    	
    	// Add eventHandlers here:    		
    	getServer().getPluginManager().registerEvents(new Listener(){
    		
    		@EventHandler
    	    public void onPlayerJoin(PlayerJoinEvent event) {
    			initPlayer(event.getPlayer());
    		}
    		
    		@EventHandler
    	    public void onPlayerSneak(PlayerToggleSneakEvent event) {
    			Player player = event.getPlayer();
    			
/*Sneak*/		if(player.hasPermission("StealthPlugin.hide.sneak") && !(Boolean) getMetadata(player, "SneakCooldown")){
    				 
    				if(player.getEyeLocation().getBlock().getLightLevel() <= getConfig().getInt("Sneak.LightLevel")){
    					if(event.isSneaking()){
    						hide(player);
    						startSuit(player);
    						setMetadata(player,"SneakHidden", true);
    					}
    					else {
    						show(player);
    						startCooldown(player,"Suit");
    						setMetadata(player,"SneakHidden", false);
    					}
    				}
    			}
    			 
/*Suit*/    	else if(player.hasPermission("StealthPlugin.hide.suit") && !(Boolean) getMetadata(player, "SuitCooldown")){
     				
     				checkSuit(player);
     				if(getMetadata(player,"Equip").equals(true)){
     					if(event.isSneaking()){
     						hide(player);
    						setMetadata(player,"SuitHidden", true);
     					}
     					else {
    						show(player);
    						setMetadata(player,"SuitHidden", false);
    					}
     				}
    			}
    		}
    		
    		@EventHandler
    	    public void onPlayerMove(PlayerMoveEvent event) {
    			Player player = event.getPlayer();
    			
/*Sneak*/    	if(player.hasPermission("StealthPlugin.hide.sneak")){
    				
    				if(getMetadata(player, "SneakHidden").equals(true) && (player.getEyeLocation().getBlock().getLightLevel() > getConfig().getInt("Sneak.LightLevel"))){
    					show(player);
    					setMetadata(player,"SneakHidden", false);
    				}
    			}
    			
/*Stealth*/    	if(player.hasPermission("StealthPlugin.hide.stealth") && !(Boolean) getMetadata(player, "StealthCooldown")){
    				
    				if(getMetadata(player, "StealthHidden").equals(false) && (player.getEyeLocation().getBlock().getLightLevel() <= getConfig().getInt("Stealth.LightLevel"))){
    					hide(player);
    					setMetadata(player,"StealthHidden", true);
    					seeInvisible(player,true);
    				}
    				else if(getMetadata(player, "StealthHidden").equals(true) && (player.getEyeLocation().getBlock().getLightLevel() > getConfig().getInt("Stealth.LightLevel"))){
    					show(player);
    					setMetadata(player,"StealthHidden", false);
    					seeInvisible(player,false);
    				}
    			}
    		}
    		@EventHandler
    	    public void onEntityHit(EntityDamageByEntityEvent event) {
    			Player player;
/*Damager is Player ?*/
    			if(event.getDamager() instanceof Player){
    				player = (Player) event.getDamager();
    			}
    			else return;
    			
    			Entity entity = event.getEntity();
    			
/*Stealth*/    	if(getMetadata(player, "StealthHidden").equals(true)){
    				if(entity instanceof Player){
    					Player target = (Player) entity;
    					if(getMetadata(target, "StealthHidden").equals(false) && getMetadata(target, "SneakHidden").equals(false) && getMetadata(target, "SuitHidden").equals(false) ){
    						event.setDamage(event.getDamage() + getConfig().getInt("Stealth.Damage"));
    					}
    				}
    				else if(entity instanceof LivingEntity){
    					event.setDamage(event.getDamage() + getConfig().getInt("Stealth.Damage"));
    				}
    			}

/*Sneak*/		else if(getMetadata(player, "SneakHidden").equals(true)){
					if(entity instanceof Player){
						Player target = (Player) entity;
						if(getMetadata(target, "StealthHidden").equals(false) && getMetadata(target, "SneakHidden").equals(false) && getMetadata(target, "SuitHidden").equals(false) ){
							event.setDamage(event.getDamage() + getConfig().getInt("Stealth.Damage"));
						}
					}
					else if(entity instanceof LivingEntity){
						event.setDamage(event.getDamage() + getConfig().getInt("Stealth.Damage"));
					}
				}
				
/*Suit*/		else if(getMetadata(player, "SuitHidden").equals(true)){
					if(entity instanceof Player){
						Player target = (Player) entity;
						if(getMetadata(target, "StealthHidden").equals(false) && getMetadata(target, "SneakHidden").equals(false) && getMetadata(target, "SuitHidden").equals(false) ){
							event.setDamage(event.getDamage() + getConfig().getInt("Stealth.Damage"));
						}
					}
					else if(entity instanceof LivingEntity){
						event.setDamage(event.getDamage() + getConfig().getInt("Stealth.Damage"));
					}
				}
    		}
    		
    	}, this);	
    }
    
    public void startCooldown(final Player player, final String key){
    	new BukkitRunnable(){
    		public void run(){
    	   		setMetadata(player, key + "Cooldown", false);
    		}
    	}.runTaskLater(this, getConfig().getLong(key + ".Cooldown") * 20);
    	setMetadata(player, key + "Cooldown", true);
    }

    public void startSuit(final Player player){
    	new BukkitRunnable(){
    		public void run(){
    			if(!(Boolean) getMetadata(player, "SuitCooldown")){
    				startCooldown(player, "Suit");
    				show(player);
    			}
    		}
    	}.runTaskLater(this, getConfig().getLong("Suit.Duration") * 20);
    }
    
    @Override
	public void onDisable() {
    	
    }
    
    // Add commands here (don't forget to add them in plugin.yml)
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	
    	if(cmd.getName().equalsIgnoreCase("GetSuit")){
    		
    		if (args.length > 0){
    			sender.sendMessage("Too many arguments!");
    		}
    		else if(!(sender instanceof Player)){
    			sender.sendMessage("Only Players can use this command!");
    		}
    		else{
    			giveSuit((Player) sender);
    			return true;
    		}
    			
    	}
    	
    	if(cmd.getName().equalsIgnoreCase("GiveSuit")){
    		
    		if(args.length == 1){
    			Player target = (getServer().getPlayer(args[0]));
    	        if (target == null) {
    	           sender.sendMessage(args[0] + " is not online!");
    	           return false;
    	        }
    	        else{
    	        	giveSuit(target);
    	        	return true;
    	        }
    		}
    		else if(args.length == 0){
    			return true;
    		}
    		sender.sendMessage("Too many arguments!");
    		return false;
    	}
    	
    	if(cmd.getName().equalsIgnoreCase("StealthPlugin")){
    		
    		if (args.length > 1) {
    			sender.sendMessage("Too many arguments!");
    			return false;
    		}
    	
    		if (args.length < 1) {
    			sender.sendMessage("Not enough arguments!");
            	return false;
    		}
    	
    		if(args[0].equalsIgnoreCase("reload")){
    			this.reloadConfig();  
    			return true;
    		}
    	}
    	return false; //if nothing happened return false to show the command help! 
    }
    
    public void setMetadata(Player player, String key, Object value){
    	  player.setMetadata(key,new FixedMetadataValue(this,value));
    	}
    
    public Object getMetadata(Player player, String key){
    	  List<MetadataValue> values = player.getMetadata(key);
    	  for(MetadataValue value : values){
    		 if(value.getOwningPlugin().getDescription().getName().equals(this.getDescription().getName())){
    	        return value.value();
    	     }
    	  }
    	  return null;
    	}
    
    public void hide(Player player){
    	for(Player otherplayer : getServer().getOnlinePlayers()) {
    	    if(!otherplayer.hasPermission("StealthPlugin.seeInvisible")) {
    	        otherplayer.hidePlayer(player);
    	    }
    	}
    	if(getConfig().getBoolean("Misc.debug"))getLogger().info("hidden!");
  	}
    
    public void show(Player player){
    	for(Player otherplayer : getServer().getOnlinePlayers()) {
    		otherplayer.showPlayer(player);
    	}
    	setMetadata(player, "SuitHidden", false);
    	if(getConfig().getBoolean("Misc.debug"))getLogger().info("shown!");
    }
    
    public void seeInvisible(Player player, boolean YesNo){
    	
    }
    
    public void giveSuit(Player player){
    	
    	String[] ArmorTypes = {"Helmet","Chestplate","Leggings","Boots"};
    	for(String armorPart : ArmorTypes){
    		int itemId = getConfig().getInt("Suit.Equip." + armorPart + ".Id");
    		String itemName = getConfig().getString("Suit.Equip." + armorPart + ".Name");
    		String itemColor = getConfig().getString("Suit.Equip." + armorPart + ".Color");
    		itemName = itemName.replace('&', '§');
    		if(itemId != -1){
    			ItemStack itemstack = new ItemStack(itemId);
    			ItemMeta im = itemstack.getItemMeta();
    			if(!itemName.equals("any"))im.setDisplayName(itemName);
    			itemstack.setItemMeta(im);
    			if(itemId > 297 && itemId < 302){
    				if(!itemColor.equals("any")){
    					int[] itemRGB = {0,0,0};
    					int i = 0;
    					for (String color : itemColor.split(",")){
    						try{
    							itemRGB[i] = Integer.parseInt(color);
    						}
    						catch (NumberFormatException nfe){
    							return;
    						}
    						i++;
    					}
    					LeatherArmorMeta leatherIm = (LeatherArmorMeta) im;
    					leatherIm.setColor(Color.fromRGB(itemRGB[0],itemRGB[1],itemRGB[2]));
    					itemstack.setItemMeta(leatherIm);
    				}
    			}
    			player.getInventory().addItem(itemstack);
    		}
    	}
    }
    
    public void checkSuit(Player player){
    	getLogger().info("SuitCheck!");
    	boolean result = true;
    	ItemStack[] armor = player.getInventory().getArmorContents();
		for(ItemStack piece : armor){
			String armorPart = "";
			if(piece.getTypeId() != 0){
				armorPart = piece.getType().name().substring(piece.getType().name().indexOf("_")+1).toLowerCase();
				armorPart = Character.toUpperCase(armorPart.charAt(0)) + armorPart.substring(1);
/*ITEMID*/		if(piece.getTypeId() != getConfig().getInt("Suit.Equip."+ armorPart +".Id")) {result = false;}
				else if (getConfig().getInt("Suit.Equip."+ armorPart +".Id") == -1) {result = true; continue;}
/*COLOR*/		if (297 < piece.getTypeId() &&  piece.getTypeId() < 302 && !getConfig().getString("Suit.Equip." + armorPart + ".Color").equals("any")){			
					String[] itemColor = getConfig().getString("Suit.Equip." + armorPart + ".Color").split(",");
					int[] colorRGB = {0,0,0};
					for(int i = 0; i < 3; i++){
						try{
							colorRGB[i] = Integer.parseInt(itemColor[i]);
						}
						catch (NumberFormatException nfe){
/*add message*/   	 	    result = false;
						}
					}
					LeatherArmorMeta ColorTest = (LeatherArmorMeta) piece.getItemMeta();
					if(!(ColorTest.getColor().equals(Color.fromRGB(colorRGB[0], colorRGB[1], colorRGB[2])))) result = false;
/*END*/			}
/*ITEMNAME*/	String itemName = getConfig().getString("Suit.Equip." + armorPart + ".Name");
				if(itemName != null && piece.getItemMeta().hasDisplayName() && !itemName.equals("any")){
					itemName = itemName.replace('&', '§');
					if(!piece.getItemMeta().getDisplayName().equals(itemName)){
						result = false;
					}
/*END*/			}
			}
			else result = false;
		}
		if(result){
			setMetadata(player, "Equip", true);
		}
		else{
			setMetadata(player, "Equip", false);
		}
    }
    
    public List<Player> GetHiddenPlayers(){
    	if(getServer().getOnlinePlayers() != null){
    		List<Player> HiddenPlayers = new ArrayList<Player>();
    		for(Player player : getServer().getOnlinePlayers()){
    			if(getMetadata(player, "SuitHidden").equals(true) || getMetadata(player, "SneakHidden").equals(true) || getMetadata(player, "StealthHidden").equals(true)){
    				HiddenPlayers.add(player);
    			}
    		}
    		return HiddenPlayers;
    	}
    	else return null;
    }

    public void initPlayer(Player player){
		setMetadata(player, "SuitHidden", false);
		setMetadata(player, "SneakHidden", false);
		setMetadata(player, "StealthHidden", false);
		setMetadata(player, "StealthCooldown", false);
		setMetadata(player, "SneakCooldown", false);
		setMetadata(player, "SuitCooldown", false);
		if(!player.hasPermission("StealthPlugin.seeInvisible") && !GetHiddenPlayers().isEmpty()){
			for(Player otherplayer : GetHiddenPlayers()){
					player.hidePlayer(otherplayer);
			}
		}
    }
}
