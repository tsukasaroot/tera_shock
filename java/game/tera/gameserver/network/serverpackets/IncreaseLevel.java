package tera.gameserver.network.serverpackets;

import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.ServerPacketType;

import tera.gameserver.tables.ItemTable;
import tera.gameserver.templates.ItemTemplate;
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.items.ItemInstance;

/**
 * Серверный пакет, описыающий обновление уровня игрока.
 *
 * @author Ronn
 */

public class IncreaseLevel extends ServerPacket
{
	private static final ServerPacket instance = new IncreaseLevel();

	public static IncreaseLevel getInstance(Player player)
	{
		ItemTable itemTable = ItemTable.getInstance();
		IncreaseLevel packet = (IncreaseLevel) instance.newInstance();
		ItemTemplate templateWeapon = null, templateArmor1 = null, templateArmor2 = null, templateArmor3 = null,
				templateEarring, templateRing, templateNecklace;

		packet.objectId = player.getObjectId();
		packet.subId = player.getSubId();
		packet.level = player.getLevel();

		int classId = player.getClassId();

		if (player.getLevel() == 60) {
			player.sendMessage("You reached lvl " + player.getLevel() + "! Here some starting equipment ! " + classId);
			switch (classId) {
				case 7: // mystic
					templateWeapon = itemTable.getItem(10400);
					break;
				case 6: // priest
					templateWeapon = itemTable.getItem(13260);
					break;
				case 5: // archer
					templateWeapon = itemTable.getItem(10494);
					break;
				case 4: // sork
					templateWeapon = itemTable.getItem(10493);
					break;
				case 3: // berserk
					templateWeapon = itemTable.getItem(13217);
					break;
				case 2: // slayer
					templateWeapon = itemTable.getItem(10267);
					break;
				case 1: // lancer
					templateWeapon = itemTable.getItem(13303);
					break;
				case 0: // warrior
					templateWeapon = itemTable.getItem(10489);
					break;
			}
			if (classId == 0 || classId == 2 || classId == 5) { // leather class
				templateArmor1 = itemTable.getItem(15554); // glove
				templateArmor2 = itemTable.getItem(15553); // cuirass
				templateArmor3 = itemTable.getItem(15555); // boots
			} else if (classId == 1 || classId == 3) { // metal class
				templateArmor1 = itemTable.getItem(15551); // gauntlets
				templateArmor2 = itemTable.getItem(15552); // Greaves
				templateArmor3 = itemTable.getItem(15550); // Hauberk
			} else if (classId == 4 || classId == 6 || classId == 7) {
				templateArmor1 = itemTable.getItem(18490); // sleeves
				templateArmor2 = itemTable.getItem(18491); // shoes
				templateArmor3 = itemTable.getItem(18489); // robes
			}

			templateEarring = itemTable.getItem(20365);
			templateRing = itemTable.getItem(20129);
			templateNecklace = itemTable.getItem(20725);

			GiveAwayItem(templateArmor3, player);
			GiveAwayItem(templateWeapon, player);
			GiveAwayItem(templateArmor2, player);
			GiveAwayItem(templateArmor1, player);
			GiveAwayItem(templateEarring, player);
			GiveAwayItem(templateRing, player);
			GiveAwayItem(templateNecklace, player);
		}

		return packet;
	}

	private static void GiveAwayItem(ItemTemplate toCreate, Player player)
	{
		ItemInstance newItem = toCreate.newInstance();

		newItem.setAutor("GM_Create_Item");

		Inventory inventory = player.getInventory();

		if (inventory.putItem(newItem))
		{
			ObjectEventManager eventManager = ObjectEventManager.getInstance();

			eventManager.notifyInventoryChanged(player);
		}
	}

	/** ид игрока */
	private int objectId;
	/** под ид игрока */
	private int subId;
	/** уровень игрока */
	private int level;

	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.PLAYER_INCREASE_LEVEL;
	}

	@Override
	protected final void writeImpl()
	{
		writeOpcode();
		writeInt(objectId); // наш ИД
		writeInt(subId);
		writeInt(level); // преобретаемый лвл
	}
}
