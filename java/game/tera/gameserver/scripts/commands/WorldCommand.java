package tera.gameserver.scripts.commands;

import java.io.File;  // Import the File class
import java.io.IOException;  // Import the IOException class to handle errors
import java.io.FileWriter;   // Import the FileWriter class
import java.nio.file.Files;
import java.nio.file.Path;

import rlib.util.array.Array;
import tera.gameserver.model.TownInfo;
import tera.gameserver.model.World;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.territory.Territory;
import tera.gameserver.tables.TownTable;
import tera.util.Location;

/**
 * Список команд, для работы с территориями.
 * 
 * @author Ronn
 */
public class WorldCommand extends AbstractCommand
{
	public WorldCommand(int access, String[] commands)
	{
		super(access, commands);
	}

	@Override
	public void execution(String command, Player player, String values)
	{
		switch(command)
		{
			case "loc":
				if(values == null || values.isEmpty()) {
					player.sendMessage("Char: " + player.getName() + " Location X: " + player.getX() + "  Y: " + player.getY() + "  Z: " + player.getZ() + " heading: " + player.getHeading());
				} else if (values.length() == 2) {
					player.sendMessage(
							"\n--loc file index name type (Will create a new territory header in given file)\n"+
									"--loc file (to create a new point in the file)\n"+
									"--loc file -t (to end the ongoing territory in the file)"
					);
				} else {
					String[] argloc = values.split(" ");
					player.sendMessage("Char: " + player.getName() + " Location X: " + player.getX() + "  Y: " + player.getY() + "  Z: " + player.getZ() + " heading: " + player.getHeading());
					player.sendMessage("Writing in " + argloc[0]);
					File fs;
					try {
						Path path = Files.createTempFile(argloc[0], ".xml");
						float minZ = player.getZ() - 1000;
						float maxZ = player.getZ() + 1000;
						int continent = player.getContinentId();
						try {
							FileWriter myWriter = new FileWriter(argloc[0] + ".xml", true);
							if (argloc.length == 2) {
								if ("-t".equals(argloc[1])) {
									myWriter.append("\t</territory>\n");
									player.sendMessage("Territory is finished.");
								}
							} else if (argloc.length == 1) {
								myWriter.append("\t\t<point x=\"").append(String.valueOf(player.getX())).append("\" y=\"").append(String.valueOf(player.getY())).append("\" z=\"").append(String.valueOf(player.getZ())).append("\" />\n");
							} else if (argloc.length == 4) {
								myWriter.append("\t<territory id=\"").append(String.valueOf(Integer.parseInt(argloc[1])));
								myWriter.append("\" name=\"").append(argloc[2]).append("\" type=\"").append(argloc[3]);
								myWriter.append("\" centerX=\"").append(String.valueOf(player.getX())).append("\" centerY=\"").append(String.valueOf(player.getY()));
								myWriter.append("\" centerZ=\"").append(String.valueOf(player.getZ())).append("\" minZ=\"").append(String.valueOf(minZ));
								myWriter.append("\" maxZ=\"").append(String.valueOf(maxZ)).append("\" continentId=\"").append(Integer.toString(continent)).append("\"/>\n");
							} else
								player.sendMessage(
										"\n--loc file index name type (Will create a new territory header in given file)\n"+
										"--loc file (to create a new point in the file)\n"+
										"--loc file -t (to end the ongoing territory in the file)"
								);
							myWriter.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				break;
			case "region":
				player.sendMessage("Region: " + player.getCurrentRegion() + ", id = " + player.getCurrentRegion().hashCode());
				break;
			case "territory":
			{
				Array<Territory> terrs = player.getTerritories();
				if(terrs.isEmpty())
				{
					player.sendMessage("You're not in occuped territory.");
					break;
				}

				StringBuilder text = new StringBuilder("Territory:");

				for(Territory terr : terrs)
					if(terr != null)
						text.append(terr).append(", ");

				if(!player.getTerritories().isEmpty())
					text.replace(text.length() - 2, text.length(), ".");

				player.sendMessage(text.toString());

				break;
			}
			case "goto":
			{
				String[] args = values.split(" ");

				if(args.length > 1 && "-p".equals(args[1]))
				{
					Player target = World.getPlayer(args[0]);

					if(target != null)
						player.teleToLocation(target.getLoc());

					break;
				}
				if(args.length == 4)
				{
					float x = Float.parseFloat(args[0]);
					float y = Float.parseFloat(args[1]);
					float z = Float.parseFloat(args[2]);

					int continent = Integer.parseInt(args[3]);

					player.teleToLocation(continent, x, y, z);
				}
				else
				{
					// получаем таблицу городов
					TownTable townTable = TownTable.getInstance();

					// получаем искомый город
					TownInfo town = townTable.getTown(values);

					// обновляем ид зоны
					player.setZoneId(town.getZone());

					// телепортим его в центр города
					player.teleToLocation(town.getCenter());
				}

				break;
			}
			case "recall":
			{
				Player target = World.getPlayer(values);

				if(target == null)
					return;

				Location loc = player.getLoc();

				loc.setContinentId(player.getContinentId());

				target.teleToLocation(loc);

				break;
			}
		}
	}
}
