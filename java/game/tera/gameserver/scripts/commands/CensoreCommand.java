package tera.gameserver.scripts.commands;

import java.text.SimpleDateFormat;
import java.util.Date;

import tera.gameserver.model.World;
import tera.gameserver.model.playable.Player;


/**
 * Список команд, доступных для цензоров.
 *
 * @author Ronn
 */
public class CensoreCommand extends AbstractCommand
{
	private static final SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	private static final Date date = new Date();

	/**
	 * @param access необходимый уровень прав.
	 * @param commands набор комманд.
	 */
	public CensoreCommand(int access, String[] commands)
	{
		super(access, commands);
	}

	@Override
	public void execution(String command, Player player, String values)
	{
		switch(command)
		{
			case "chat_ban":
			{
				String[] vals = values.split(" ", 3);

				if(vals.length < 3)
				{
					player.sendMessage("incorrect values.");
					return;
				}

				String name = vals[0];
				String comment = vals[2];

				if(comment.isEmpty())
				{
					player.sendMessage("no reason entered.");
					return;
				}

				long time = Integer.parseInt(vals[1]) * 60 * 1000;

				Player target = null;

				if(player.getName().equals(name))
					target = player;
				else if((target = World.getAroundByName(Player.class, player, name)) == null)
					target = World.getPlayer(name);

				if(target == null)
				{
					player.sendMessage("the specified player is not in the game.");
					return;
				}

				long endTime = System.currentTimeMillis() + time;

				target.setEndChatBan(endTime);

				String stringDate = null;

				synchronized(date)
				{
					date.setTime(endTime);

					stringDate = timeFormat.format(date);
				}

				World.sendAnnounce(player.getName() + " blocked chat player " + name + " for " + stringDate + " because: " + comment);

				break;
			}
			case "chat_unban":
			{
				Player target = null;

				if(player.getName().equals(values))
					target = player;
				else if((target = World.getAroundByName(Player.class, player, values)) == null)
					target = World.getPlayer(values);

				if(target == null)
				{
					player.sendMessage("the specified player is not in the game.");
					return;
				}

				if(target.getEndChatBan() < 1)
				{
					player.sendMessage("player has no chat block.");
					return;
				}

				target.setEndChatBan(0);

				World.sendAnnounce(player.getName() + " unlocked chat player " + values);
			}
		}
	}
}
