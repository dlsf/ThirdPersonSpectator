package net.seliba.thirdpersonspectator.commands;

import net.seliba.thirdpersonspectator.configuration.ConfigurationProvider;
import net.seliba.thirdpersonspectator.configuration.types.Message;
import net.seliba.thirdpersonspectator.gui.SpectateGUI;
import net.seliba.thirdpersonspectator.model.SpectatablePlayer;
import net.seliba.thirdpersonspectator.model.SpectatablePlayerFactory;
import net.seliba.thirdpersonspectator.utils.SoundUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Command which allows Player's to spectate others in a third-person view.
 */
public final class SpectateCommand implements CommandExecutor, TabCompleter {

    private final ConfigurationProvider configurationProvider;

    /**
     * The default constructor.
     *
     * @param configurationProvider The provider of configurations for the plugin.
     */
    public SpectateCommand(ConfigurationProvider configurationProvider) {
        this.configurationProvider = configurationProvider;
    }

    /**
     * Executes this command with the provided arguments.
     * Allows Player's to spectate others in a third-person view.
     *
     * @param sender  The sender which executes this command.
     * @param command The Bukkit {@link org.bukkit.command.Command} representation.
     * @param label   The label of this command.
     * @param args    The arguments of this command.
     * @return true if this command was executed successfully
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(configurationProvider.getString(Message.PREFIX, Message.NO_PLAYER));
            return false;
        }

        var player = (Player) sender;

        // Open the Player selection inventory if there is no Player specified
        if (args.length == 0) {
            player.openInventory(new SpectateGUI(configurationProvider).getInventory());
            return true;
        }

        // Try to find the Player specified in the first argument
        var targetPlayer = Bukkit.getPlayerExact(args[0]);
        if (targetPlayer == null) {
            player.sendMessage(configurationProvider.getString(Message.PREFIX, Message.PLAYER_NOT_ONLINE));
            SoundUtils.playFailSound(player);
            return false;
        }

        // Check if the Player can spectate the requested Player
        var spectatablePlayer = SpectatablePlayerFactory.get(targetPlayer);
        if (!spectatablePlayer.isSpectatable() || targetPlayer == player) {
            player.sendMessage(configurationProvider.getString(Message.PREFIX, Message.INVALID_TARGET));
            SoundUtils.playFailSound(player);
            return false;
        }

        spectatablePlayer.startSpectating(player);

        return true;
    }

    /**
     * Handles tab-completion for this command.
     *
     * @param sender  The CommandSender which tries to tab-complete.
     * @param command The command.
     * @param alias   The label of the command.
     * @param args    The arguments already provided by the sender.
     * @return The list of tab completions for this command.
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // Return a list of names of all spectatable Players
        return SpectatablePlayerFactory.getCachedPlayers().stream()
                .filter(SpectatablePlayer::isSpectatable)
                .map(SpectatablePlayer::getBukkitPlayer)
                .map(Player::getName)
                .filter(suggestion -> suggestion.toLowerCase().startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
    }

}
