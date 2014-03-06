package info.insomniax.cheatcodes.permissions;

import info.insomniax.cheatcodes.core.OWHCheatCodes;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Permissions {

	
	static String BASE_NODE = "owhcheatcodes";
	OWHCheatCodes myPlugin;
	public static Permission permission = null;
	public static Economy economy = null;
	
	public Permissions(OWHCheatCodes instance)
	{
		myPlugin = instance;
	}
	
	public boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = myPlugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }

    public boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = myPlugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
	
	public boolean has(CommandSender sender, String node)
	{
		if(sender instanceof CommandSender)
			return true;
		
		return permission.has(sender, node);
	}
	
	public static String getBaseNode()
	{
		return BASE_NODE;
	}

}
