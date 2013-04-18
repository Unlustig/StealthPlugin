package com.github.unlustig.StealthPlugin;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.metadata.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
	
	// Add eventHandlers here:
    @Override
    public void onEnable(){
    	this.getServer().getPluginManager().registerEvents(new Listener(){
    		
    		@EventHandler
    	    public void onPlayerJoin(PlayerJoinEvent event) {
    			Player otherplayer = event.getPlayer();
    			if(!otherplayer.hasPermission("StealthPlugin.seeInvisible")) {
    				for(Player player : Main.this.GetHiddenPlayers()){
    						player.hidePlayer(otherplayer);
    				}
    			}
    		}
    		
    		@EventHandler
    	    public void onPlayerSneak(PlayerToggleSneakEvent event) {
    			Player player = event.getPlayer();
/*permission stuff*/    			if(){
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

    	    @EventHandler
    	    public void onIClick(InventoryClickEvent event){ 
    	    	if(event.getSlotType() == InventoryType.SlotType.ARMOR){
    	    		ItemStack[] armor = event.getWhoClicked().getInventory().getArmorContents();
    	    		for(ItemStack piece : armor){
    	    			String armorPart = "";
    	    			if(piece != null){
    	    				if(piece.getTypeId() == Main.this.getConfig().getInt("NinjaSuit.Suit.Helmet.Id")){
    	    					armorPart = "Helmet";
    	    				}
    	    				else if(piece.getTypeId() == Main.this.getConfig().getInt("NinjaSuit.Suit.Chestplate.Id")){
    	    					armorPart = "Chestplate";
    	    				}
    	    				else if(piece.getTypeId() == Main.this.getConfig().getInt("NinjaSuit.Suit.Leggins.Id")){
    	    					armorPart = "Leggins";
    	    				}
    	    				else if(piece.getTypeId() == Main.this.getConfig().getInt("NinjaSuit.Suit.Boots.Id")){
    	    					armorPart = "Boots";
    	    				}
    	    				else return;
    	    			}
    	    			String[] itemColor = Main.this.getConfig().getString("NinjaSuit.Suit" + armorPart + ".color").split(",");
    	    			int[] colorRGB;
    	    			for(int i = 0; i < 3; i++){
    	    				try{
    	    					colorRGB[i] = Integer.parseInt(itemColor[i]);
    	    				}
    	    				catch (NumberFormatException nfe){
/*add message*/    	    		return;
    	    				}
    	    			}
    	    			if (297 < piece.getTypeId() && piece.getTypeId() < 302 && itemColor != null){
    	    				LeatherArmorMeta ColorTest = (LeatherArmorMeta) piece.getItemMeta();
    	    				if(ColorTest.getColor() != Color.fromRGB(colorRGB[0], colorRGB[1], colorRGB[2])) return;
    	    			}
    	    			// Color Test succesfull or not needed!
    	    			String itemName = Main.this.getConfig().getString("NinjaSuit.Suit" + armorPart + ".Name");
    	    		}
    	    	}
    	    }
    	}, this);
    	
  		this.saveDefaultConfig();
  		
}

    @Override
	public void onDisable() {
    	
    }
    
    // Add commands here (don't forget to add them in plugin.yml)
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
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
    	else if(args[0].equalsIgnoreCase("reset")){
    			if (sender instanceof Player) {
    				Player player = (Player) sender;	
    				setMetadata(player, "SuitCD" , (int) 0 );
    				setMetadata(player, "SuitTime" , (int) 0 );
    				return true;
    			}
    			else {
    				sender.sendMessage("Only Players can use this command!");
    				return false;
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
    	        player.hidePlayer(otherplayer);
    	    }
    	}
  	}
    
    public void show(Player player){
    	for(Player otherplayer : getServer().getOnlinePlayers()) {
    		player.showPlayer(otherplayer);
    		setMetadata(player, "SuitHidden", false);
    	}
    }
    
    public List<Player> GetHiddenPlayers(){
    	List<Player> HiddenPlayers = new ArrayList<Player>();
    	for(Player player : this.getServer().getOnlinePlayers()){
    		if( (Boolean) getMetadata(player, "SuitHidden") /*|| add other invisibilitys*/ ){
    			HiddenPlayers.add(player);
    		}
    	}
    	return HiddenPlayers;
    }
}