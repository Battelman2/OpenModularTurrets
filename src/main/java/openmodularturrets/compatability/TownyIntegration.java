package openmodularturrets.compatability;

import com.palmergames.bukkit.towny.object;

/**
 * Created by Battelman2 on 7/21/15.
 */
public class TownyIntegration {

    public static boolean townyPresent = false;

    public static boolean isTownyPresent() {
        return townyPresent;
    }

    public static boolean playerIsAllied (String ownerName, EntityPlayerMP entity) {

        Resident ownerResident = TownyUniverse.getDataSource().getResident(ownerName);

        if (ownerResident.hasTown()) {

            // Check town in which player belongs
            for(Resident r : ownerResident.getTown().getResidents()) {
                if (r.getName().equals(entity.getDisplayName()))
                    return true;
            }

            // Check towns of allied nations
            if (ownerResident.hasNation()) {

                // Check own nation
                for (Town t : ownerResident.getTown().getNation().getTowns()) {
                    for (Resident r : t.getResidents()) {
                        if (r.getName().equals(entity.getDisplayName()))
                            return true;
                    }
                }

                // Check allied nations
                for (Nation n : ownerResident.getTown().getNation().getAllies()) {
                    for (Town t : n.getTowns()) {
                        for (Resident r : t.getResidents()) {
                            if (r.getName().equals(entity.getDisplayName()))
                                return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
