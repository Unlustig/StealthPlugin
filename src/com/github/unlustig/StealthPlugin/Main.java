package com.github.unlustig.StealthPlugin;

import java.util.List;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    @Override
    public void onEnable(){
    	this.getServer().getPluginManager().registerEvents(new StealthPluginListener(), this);
  		this.saveDefaultConfig();
  		
    }

    @Override
	public void onDisable() {
    	
    }
    
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
    				setMetadata(player, "StealthCD" , (int) 0 );
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
    
    public int getMetadataAsInteger(Player player, String key){
  	  List<MetadataValue> values = player.getMetadata(key);  
  	  for(MetadataValue value : values){
  	     if(value.getOwningPlugin().getDescription().getName().equals(this.getDescription().getName())){
  	        return value.asInt();
  	     }
  	  }
  	  return -1;
  	}
    
    public void hidePlayer(Player player){
    	for(Player otherplayer : getServer().getOnlinePlayers()) {
    	    if(!otherplayer.hasPermission("StealthPlugin.seeInvisible")) {
    	        player.hidePlayer(otherplayer);
    	    }
    	}
  	}
}